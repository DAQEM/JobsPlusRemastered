package com.daqem.jobsplus.mixin.crafting.block;

import com.daqem.jobsplus.level.block.JobsFurnaceBlockEntity;
import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible, JobsFurnaceBlockEntity {

    @Nullable
    private UUID playerUUID;

    @Nullable
    private JobsServerPlayer player;

    @Shadow
    int litTime;

    @Shadow
    public abstract @NotNull ItemStack getItem(int i);

    private final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;

    protected MixinAbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType, blockPos, blockState);
        this.quickCheck = RecipeManager.createCheck(recipeType);
    }

    @Inject(at = @At("TAIL"), method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z")
    private void stillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            if (getPlayer() != serverPlayer) {
                setPlayer(serverPlayer);
                setPlayerUUID(serverPlayer.jobsplus$getUUID());
                saveWithFullMetadata();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;)V")
    private void saveAdditional(CompoundTag compoundTag, CallbackInfo ci) {
        if (getPlayer() != null) {
            compoundTag.putString("JobsServerPlayer", getPlayer().jobsplus$getUUID().toString());
        } else {
            if (getPlayerUUID() != null) {
                compoundTag.putString("JobsServerPlayer", getPlayerUUID().toString());
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "load(Lnet/minecraft/nbt/CompoundTag;)V")
    private void load(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("JobsServerPlayer")) {
            setPlayerUUID(UUID.fromString(compoundTag.getString("JobsServerPlayer")));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)V", cancellable = true)
    private static void serverTickRecipe(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, CallbackInfo ci) {
        if (abstractFurnaceBlockEntity instanceof JobsFurnaceBlockEntity block) {
            if (block.getPlayer() == null && block.getPlayerUUID() != null && level.getServer() != null) {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(block.getPlayerUUID());
                if (player instanceof JobsServerPlayer serverPlayer) {
                    block.setPlayer(serverPlayer);
                }
            }
            if (block.getPlayer() != null && !abstractFurnaceBlockEntity.getItem(0).isEmpty()) {
                if (!abstractFurnaceBlockEntity.getItem(1).isEmpty()) {

                    Recipe<?> recipe = block.getRecipe();

                    CraftingResult result = block.getPlayer().jobsplus$canCraft(CraftingType.SMELTING, recipe.getResultItem());

                    if (!result.canCraft()) {
                        if (block.isLit()) {
                            block.setLitTime(block.getLitTime() - 1);
                        }
                        blockState = blockState.setValue(AbstractFurnaceBlock.LIT, false);
                        level.setBlock(blockPos, blockState, 3);
                        setChanged(level, blockPos, blockState);
                        ci.cancel();

                        sendPacketCantCraft(result, block);
                    }
                } else {
                    sendPacketCantCraft(new CraftingResult(true), block);
                }
            } else if (block.getPlayer() != null) {
                sendPacketCantCraft(new CraftingResult(true), block);
            }
        }
    }

    private static void sendPacketCantCraft(CraftingResult result, JobsFurnaceBlockEntity block) {
        if (block.getPlayer().jobsplus$getServerPlayer().containerMenu instanceof AbstractFurnaceMenu menu) {
            if (menu.container.equals(block.getAbstractFurnaceBlockEntity())) {
                new PacketCantCraftS2C(result).sendTo(block.getPlayer().jobsplus$getServerPlayer());
            }
        }
    }

    @Override
    public @Nullable JobsServerPlayer getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(@Nullable JobsServerPlayer player) {
        this.player = player;
    }

    @Override
    public @Nullable UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public void setPlayerUUID(@Nullable UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public int getLitTime() {
        return litTime;
    }

    @Override
    public void setLitTime(int litTime) {
        this.litTime = litTime;
    }

    @Override
    public boolean isLit() {
        return getLitTime() > 0;
    }

    @Override
    public RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> getQuickCheck() {
        return quickCheck;
    }

    @Override
    public AbstractFurnaceBlockEntity getAbstractFurnaceBlockEntity() {
        return (AbstractFurnaceBlockEntity) (Object) this;
    }

    @Override
    @Nullable
    public Recipe<?> getRecipe() {
        if (getLevel() == null) return null;
        if (getItem(0).isEmpty()) return null;
        if (getItem(1).isEmpty()) return null;
        return getQuickCheck().getRecipeFor(this, getLevel()).orElse(null);
    }
}
