package com.daqem.jobsplus.mixin.crafting;

import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCombinerMenu.class)
public abstract class MixinItemCombinerMenu extends AbstractContainerMenu {

    @Shadow
    @Final
    protected Player player;

    protected MixinItemCombinerMenu(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    private ItemCombinerMenu getMenu() {
        return (ItemCombinerMenu) (Object) this;
    }

    @Inject(at = @At("HEAD"), method = "slotsChanged(Lnet/minecraft/world/Container;)V")
    private void slotsChanged(CallbackInfo info) {
        if (getMenu() instanceof AnvilMenu || getMenu() instanceof SmithingMenu) {
            if (this.player instanceof JobsServerPlayer serverPlayer) {
                if (!(!getSlot(0).getItem().isEmpty() && !getSlot(1).getItem().isEmpty())) {
                    new PacketCantCraftS2C(new CraftingResult(true)).sendTo(serverPlayer.getServerPlayer());
                }
            }
        }
    }
}
