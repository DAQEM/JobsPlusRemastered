package com.daqem.jobsplus.mixin.crafting.menu;

import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class MixingSmithingMenu extends ItemCombinerMenu {

    public MixingSmithingMenu(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Inject(at = @At("TAIL"), method = "createResult()V", cancellable = true)
    private void createResult(CallbackInfo ci) {
        if (this.player instanceof JobsServerPlayer serverPlayer) {
            if (!this.inputSlots.getItem(0).isEmpty() && !this.inputSlots.getItem(1).isEmpty()) {
                CraftingResult craftingResult = serverPlayer.jobsplus$canCraft(CraftingType.CRAFTING, this.resultSlots.getItem(0));
                if (!craftingResult.canCraft()) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    new PacketCantCraftS2C(craftingResult).sendTo(serverPlayer.jobsplus$getServerPlayer());
                    ci.cancel();
                }
            }
        }
    }
}
