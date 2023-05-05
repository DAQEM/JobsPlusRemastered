package com.daqem.jobsplus.mixin.crafting;

import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class MixinCraftingMenu extends RecipeBookMenu<CraftingContainer> {

    public MixinCraftingMenu(MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(at = @At("TAIL"), method = "slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;)V")
    private static void slotChangedCraftingGrid(AbstractContainerMenu abstractContainerMenu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, CallbackInfo ci) {
        if (player instanceof JobsServerPlayer serverPlayer) {
            ItemStack itemStack = resultContainer.getItem(0);
            CraftingResult craftingResult = serverPlayer.canCraft(CraftingType.CRAFTING, itemStack);
            if (!craftingResult.canCraft()) {
                resultContainer.setItem(0, ItemStack.EMPTY);
                new PacketCantCraftS2C(craftingResult).sendTo(serverPlayer.getServerPlayer());
            } else {
                new PacketCantCraftS2C(new CraftingResult(true)).sendTo(serverPlayer.getServerPlayer());
            }
        }
    }
}
