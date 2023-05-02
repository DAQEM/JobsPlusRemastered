package com.daqem.jobsplus.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractScreen extends Screen {

    protected AbstractScreen(Component component) {
        super(component);
    }

    public void playClientGUIClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void drawDynamicComponent(PoseStack poseStack, Component component, float x, float y, int max_width, int color) {
        float scale = getScale(component, max_width);

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        font.draw(poseStack, component, x / scale, y / scale, color);
        poseStack.popPose();
    }

    private float getScale(Component component, int max_width) {
        float scale = 1F;
        int tries = 0;
        while (font.width(component) * scale > max_width && tries < 100) {
            scale -= 0.01F;
            tries++;
        }
        return scale;
    }

    public void drawRightAlignedString(@NotNull PoseStack poseStack, @NotNull String text, int posX, int posY, int color) {
        font.draw(poseStack, text, (float) posX - font.width(text), (float) posY, color);
    }

    public void drawCenteredString(@NotNull PoseStack poseStack, @NotNull String text, int posX, int posY, int color) {
        font.draw(poseStack, text, (float) (posX - font.width(text) / 2), (float) posY, color);
    }
}
