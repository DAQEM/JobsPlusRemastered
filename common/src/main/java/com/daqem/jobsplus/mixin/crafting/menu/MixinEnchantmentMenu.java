package com.daqem.jobsplus.mixin.crafting.menu;

import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu extends AbstractContainerMenu {

    private JobsServerPlayer player;

    protected MixinEnchantmentMenu(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(at = @At("HEAD"), method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", cancellable = true)
    private void clickMenuButton(Player player, int level, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            this.player = serverPlayer;
            CraftingResult craftingResult = serverPlayer.jobsplus$canCraft(CraftingType.ENCHANTING, getSlot(0).getItem());
            if (!craftingResult.canCraft()) {
                new PacketCantCraftS2C(craftingResult).sendTo(serverPlayer.jobsplus$getServerPlayer());
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "slotsChanged(Lnet/minecraft/world/Container;)V")
    private void slotsChanged(Container container, CallbackInfo ci) {
        if (player != null) {
            new PacketCantCraftS2C(new CraftingResult(true)).sendTo(this.player.jobsplus$getServerPlayer());
        }
    }
}
