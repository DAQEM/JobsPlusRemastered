package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.render.RenderColor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PowerUpsScreen extends AbstractScreen {

    private static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/powerup_screen.png");

    public PowerUpsScreen() {
        super(JobsPlus.literal("Power-ups"));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();
        RenderSystem.setShaderTexture(0, BACKGROUND);

        generateSkillTree(poseStack, 0, 0, width, height);
    }

    public void generateSkillTree(PoseStack poseStack, int x, int y, int screenWidth, int screenHeight) {
    }
}
