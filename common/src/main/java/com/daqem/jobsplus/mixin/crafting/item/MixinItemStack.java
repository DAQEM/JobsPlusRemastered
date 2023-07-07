package com.daqem.jobsplus.mixin.crafting.item;

import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @Inject(at = @At("HEAD"), method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", cancellable = true)
    private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            CraftingResult craftingResult = serverPlayer.jobsplus$canCraft(CraftingType.USING_ITEM, jobsplus$getItemStack());
            if (!craftingResult.canCraft()) {
                craftingResult.sendHotbarMessage(serverPlayer);
                serverPlayer.jobsplus$getServerPlayer().inventoryMenu.sendAllDataToRemote();
                cir.setReturnValue(InteractionResultHolder.fail(jobsplus$getItemStack()));
                cir.cancel();
            }
        }
    }

    @Unique
    private ItemStack jobsplus$getItemStack() {
        return (ItemStack) (Object) this;
    }
}
