package com.daqem.jobsplus.mixin.crafting;

import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneMenu.class)
public abstract class MixinGrindStoneMenu extends AbstractContainerMenu {

    @Shadow
    @Final
    Container repairSlots;
    @Shadow
    @Final
    private Container resultSlots;
    private JobsServerPlayer player;

    protected MixinGrindStoneMenu(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V")
    private void init(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, CallbackInfo ci) {
        if (inventory.player instanceof JobsServerPlayer serverPlayer) {
            this.player = serverPlayer;
        }
    }

    @Inject(at = @At("HEAD"), method = "slotsChanged(Lnet/minecraft/world/Container;)V")
    private void slotsChanged(Container container, CallbackInfo ci) {
        if (this.player != null) {
            if (!(!this.repairSlots.getItem(0).isEmpty() && !this.repairSlots.getItem(1).isEmpty())) {
                new PacketCantCraftS2C(new CraftingResult(true)).sendTo(this.player.getServerPlayer());
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "createResult()V")
    private void createResult(CallbackInfo ci) {
        if (this.player != null) {
            if (!this.repairSlots.getItem(0).isEmpty() && !this.repairSlots.getItem(1).isEmpty()) {
                ItemStack resultSlotItem = this.resultSlots.getItem(0);
                if (!resultSlotItem.isEmpty()) {
                    CraftingResult result = this.player.canCraft(CraftingType.REPAIRING, resultSlotItem);
                    if (!result.canCraft()) {
                        this.getSlot(2).set(ItemStack.EMPTY);
                        new PacketCantCraftS2C(result).sendTo(this.player.getServerPlayer());
                    }
                }
            }
        }
    }
}
