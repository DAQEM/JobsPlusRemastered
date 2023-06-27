package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.powerup.PowerupWidget;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PowerUpsScreen extends AbstractScreen {

    private static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/powerup_screen.png");
    private static final int WINDOW_WIDTH = 16* 15;
    private static final int WINDOW_HEIGHT = 16 * 8;

    private final Screen previousScreen;
    private final List<PowerupInstance> rootPowerups;
    private final List<Powerup> allPowerups;

    private final PowerupWidget rootWidget;

    public PowerUpsScreen(Screen previousScreen, List<PowerupInstance> rootPowerups, List<Powerup> allPowerups) {
        super(JobsPlus.literal("Power-ups"));
        this.previousScreen = previousScreen;
        this.rootPowerups = rootPowerups;
        this.allPowerups = allPowerups;

        PowerupInstance rootInstance = new PowerupInstance(null, null, "name", "description", 0, 0);
        rootPowerups.forEach(rootInstance::addChild);

        this.rootWidget = PowerupWidget.run(rootInstance, allPowerups);
    }

//    @Override
//    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float ticks) {
//        this.renderBackground(poseStack);
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderColor.normal();
//        RenderSystem.setShaderTexture(0, BACKGROUND);
//
//        if (getHoveredWidget(mouseX, mouseY) != null) {
//            RenderColor.grayedOut();
//            this.rootWidget.draw(poseStack, mouseX, mouseY);
//            RenderColor.normal();
//        } else {
//            this.rootWidget.draw(poseStack, mouseX, mouseY);
//        }
//
//        this.renderTooltip(poseStack, mouseX, mouseY);
//    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();

        int windowX = (this.width - WINDOW_WIDTH) / 2;
        int windowY = (this.height - WINDOW_HEIGHT) / 2;

        if (getHoveredWidget(mouseX, mouseY, windowX, windowY) != null) {
            RenderColor.grayedOut();
            this.renderBackground(poseStack, windowX, windowY);
            RenderColor.normal();
        } else {
            this.renderBackground(poseStack, windowX, windowY);
        }

        this.renderTooltip(poseStack, mouseX, mouseY, windowX, windowY, ticks);
    }

    public void renderBackground(@NotNull PoseStack poseStack, int windowX, int windowY) {
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/block/stone.png"));

        for(int m = -1; m <= 15; ++m) {
            for(int n = -1; n <= 8; ++n) {
                blit(poseStack, 16 * m + windowX, 16 * n + windowY, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.rootWidget.draw(poseStack, windowX, windowY);
    }

    private void renderTooltip(@NotNull PoseStack poseStack, int mouseX, int mouseY, int windowX, int windowY, float ticks) {
        PowerupWidget widget = getHoveredWidget(mouseX, mouseY, windowX, windowY);

        if (widget != null) {
            widget.drawHovered(poseStack, windowX, windowY);
        }
    }

    private PowerupWidget getHoveredWidget(int mouseX, int mouseY, int windowX, int windowY) {
        return this.rootWidget.getHoveredWidget(mouseX, mouseY, windowX, windowY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(previousScreen);
    }
}
