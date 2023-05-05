package com.daqem.jobsplus.mixin.crafting.screen;

import com.daqem.jobsplus.client.screen.JobsCraftingScreen;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreen.class)
public class MixinAbstractFurnaceScreen extends Screen implements JobsCraftingScreen {

    private CraftingResult craftingResult;

    protected MixinAbstractFurnaceScreen(Component component) {
        super(component);
    }

    @Override
    public void cantCraft(CraftingResult craftingResult) {
        this.craftingResult = craftingResult;
    }

    @Inject(at = @At("TAIL"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V")
    private void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (craftingResult != null && !craftingResult.canCraft()) {
            renderCantCraftMessage(poseStack, font, width, height, 166, craftingResult);
        }
    }
}
