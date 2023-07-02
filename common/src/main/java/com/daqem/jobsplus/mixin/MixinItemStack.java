package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.event.triggers.PlayerEvents;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.action.ActionResult;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract UseAnim getUseAnimation();

    @Shadow
    public abstract Item getItem();

    @Inject(at = @At("HEAD"), method = "finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;")
    private void finishUsingItem(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof JobsServerPlayer player) {
            if (this.getUseAnimation() == UseAnim.DRINK) {
                PlayerEvents.onPlayerDrink(player, getItemStack());
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", cancellable = true)
    private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            CraftingResult craftingResult = serverPlayer.canCraft(CraftingType.USING_ITEM, getItemStack());
            if (!craftingResult.canCraft()) {
                craftingResult.sendHotbarMessage(serverPlayer);
                serverPlayer.getServerPlayer().inventoryMenu.sendAllDataToRemote();
                cir.setReturnValue(InteractionResultHolder.fail(getItemStack()));
                cir.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "hurt(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;)Z", cancellable = true)
    private void hurt(int i, RandomSource randomSource, ServerPlayer serverPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (serverPlayer instanceof JobsServerPlayer player) {
            if (getItemStack().isDamageableItem()) {
                ActionResult actionResult = PlayerEvents.onPlayerHurtItem(player, getItemStack());
                if (actionResult.shouldCancelAction()) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }

    private ItemStack getItemStack() {
        return (ItemStack) (Object) this;
    }
}
