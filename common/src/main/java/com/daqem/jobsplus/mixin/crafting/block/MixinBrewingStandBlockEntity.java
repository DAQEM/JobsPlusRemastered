package com.daqem.jobsplus.mixin.crafting.block;

import com.daqem.jobsplus.level.block.JobsBrewingStandBlockEntity;
import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, JobsBrewingStandBlockEntity {

    @Shadow
    int brewTime;
    @Shadow
    private NonNullList<ItemStack> items;
    @Shadow
    private boolean[] lastPotionCount;
    @Nullable
    private UUID playerUUID;

    @Nullable
    private JobsServerPlayer player;

    protected MixinBrewingStandBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(at = @At("TAIL"), method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z")
    private void stillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            if (getPlayer() != serverPlayer) {
                setPlayer(serverPlayer);
                setPlayerUUID(serverPlayer.getUUID());
                saveWithFullMetadata();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;)V")
    private void saveAdditional(CompoundTag compoundTag, CallbackInfo ci) {
        if (getPlayer() != null) {
            compoundTag.putString("JobsServerPlayer", getPlayer().getUUID().toString());
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

    @Inject(at = @At("TAIL"), method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;)V")
    private static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BrewingStandBlockEntity brewingStandBlockEntity, CallbackInfo ci) {
        if (brewingStandBlockEntity instanceof JobsBrewingStandBlockEntity block) {
            if (block.getPlayerUUID() != null) {
                if (block.getPlayer() == null) {
                    if (level.getServer() != null) {
                        Player player = level.getServer().getPlayerList().getPlayer(block.getPlayerUUID());
                        if (player instanceof JobsServerPlayer serverPlayer) {
                            block.setPlayer(serverPlayer);
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;)V", cancellable = true)
    private static void doBrew(Level level, BlockPos blockPos, BlockState blockState, BrewingStandBlockEntity brewingStandBlockEntity, CallbackInfo ci) {
        if (brewingStandBlockEntity instanceof JobsBrewingStandBlockEntity block) {
            if (block.getPlayer() != null && !brewingStandBlockEntity.getItem(3).isEmpty()) {
                if (!brewingStandBlockEntity.getItem(0).isEmpty() && !brewingStandBlockEntity.getItem(1).isEmpty() && !brewingStandBlockEntity.getItem(2).isEmpty()) {
                    ItemStack ingredient = brewingStandBlockEntity.getItem(3);
                    for (int i = 0; i < 3; i++) {
                        ItemStack potion = brewingStandBlockEntity.getItem(i);
                        ItemStack mixedPotion = PotionBrewing.mix(ingredient, potion);

                        CraftingResult result = block.getPlayer().canCraft(CraftingType.BREWING, mixedPotion);

                        if (!result.canCraft()) {
                            block.setBrewTime(0);
                            setChanged(level, blockPos, brewingStandBlockEntity.getBlockState());
                            ci.cancel();
                            if (block.getPlayer().getServerPlayer().containerMenu instanceof BrewingStandMenu) {
                                new PacketCantCraftS2C(result).sendTo(block.getPlayer().getServerPlayer());
                            }

                            boolean[] bls = block.getPotionBits();
                            if (!Arrays.equals(bls, block.getLastPotionCount())) {
                                block.setLastPotionCount(bls);
                                BlockState blockState2 = blockState;
                                if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
                                    return;
                                }

                                for (int j = 0; j < BrewingStandBlock.HAS_BOTTLE.length; ++j) {
                                    blockState2 = blockState2.setValue(BrewingStandBlock.HAS_BOTTLE[j], bls[j]);
                                }

                                level.setBlock(blockPos, blockState2, 2);
                            }

                            return;
                        }
                    }
                } else {
                    if (block.getPlayer().getServerPlayer().containerMenu instanceof BrewingStandMenu) {
                        new PacketCantCraftS2C(new CraftingResult(true)).sendTo(block.getPlayer().getServerPlayer());
                    }
                }
            } else if (block.getPlayer() != null && brewingStandBlockEntity.getItem(3).isEmpty()) {
                if (block.getPlayer().getServerPlayer().containerMenu instanceof BrewingStandMenu) {
                    new PacketCantCraftS2C(new CraftingResult(true)).sendTo(block.getPlayer().getServerPlayer());
                }
            }
        }
    }


    @Override
    @Nullable
    public JobsServerPlayer getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(@Nullable JobsServerPlayer player) {
        this.player = player;
    }

    @Override
    @Nullable
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public void setPlayerUUID(@Nullable UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void setBrewTime(int i) {
        this.brewTime = i;
    }

    @Override
    public boolean[] getPotionBits() {
        boolean[] bls = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!this.items.get(i).isEmpty()) {
                bls[i] = true;
            }
        }

        return bls;
    }

    @Override
    public boolean[] getLastPotionCount() {
        return this.lastPotionCount;
    }

    @Override
    public void setLastPotionCount(boolean[] lastPotionCount) {
        this.lastPotionCount = lastPotionCount;
    }
}
