package com.daqem.jobsplus.mixin.crafting;

import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public class MixinCraftingMenu {

    @Inject(at = @At("TAIL"), method = "slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;)V")
    private static void slotChangedCraftingGrid(AbstractContainerMenu abstractContainerMenu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, CallbackInfo ci) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            ItemStack itemStack = resultContainer.getItem(0);
            if (!serverPlayer.canCraftItem(itemStack)) {
                resultContainer.setItem(0, ItemStack.EMPTY);
                new PacketCantCraftS2C(itemStack.getItem().arch$registryName(), new ResourceLocation("jobsplus:miner"), 10).sendTo(serverPlayer.getServerPlayer());
            }
        }
    }
}
