package com.daqem.jobsplus.mixin.crafting;

import com.daqem.jobsplus.event.triggers.PlayerEvents;
import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {

    public MixinAnvilMenu(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Inject(at = @At("HEAD"), method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V")
    private void onTake(Player player, ItemStack itemStack, CallbackInfo info) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            PlayerEvents.onUseAnvil(serverPlayer, itemStack);
        }
    }

    @Inject(at = @At("TAIL"), method = "createResult()V", cancellable = true)
    private void createResult(CallbackInfo ci) {
        if (this.player instanceof JobsServerPlayer serverPlayer) {
            if (!this.inputSlots.getItem(0).isEmpty() && !this.inputSlots.getItem(1).isEmpty() && !this.inputSlots.getItem(1).is(Items.ENCHANTED_BOOK)) {
                CraftingResult craftingResult = serverPlayer.canCraft(CraftingType.REPAIRING, this.inputSlots.getItem(0));
                if (!craftingResult.canCraft()) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    new PacketCantCraftS2C(craftingResult).sendTo(serverPlayer.getServerPlayer());
                    ci.cancel();
                }
            }
        }
    }
}
