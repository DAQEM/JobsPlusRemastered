package com.daqem.jobsplus.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

    public void drawDynamicComponent(GuiGraphics guiGraphics, Component component, float x, float y, int max_width, int color) {
        float scale = getScale(component, max_width);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        guiGraphics.drawString(font, component, (int) (x / scale), (int) (y / scale), color, false);
        guiGraphics.pose().popPose();
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

    public void drawRightAlignedString(@NotNull GuiGraphics guiGraphics, @NotNull String text, int posX, int posY, int color) {
        guiGraphics.drawString(font, text, posX - font.width(text), posY, color, false);
    }

    public void drawCenteredString(@NotNull GuiGraphics guiGraphics, @NotNull String text, int posX, int posY, int color) {
        guiGraphics.drawString(font, text, posX - font.width(text) / 2, posY, color, false);
    }
}
