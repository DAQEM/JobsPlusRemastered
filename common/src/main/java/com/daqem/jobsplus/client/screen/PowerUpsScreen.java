package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.powerup.PowerupWidget;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.networking.c2s.PacketOpenPowerupsMenuC2S;
import com.daqem.jobsplus.networking.c2s.PacketTogglePowerUpC2S;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PowerUpsScreen extends AbstractScreen {

    private static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/powerup_screen.png");
    private static final int WINDOW_WIDTH = 16 * 15;
    private static final int WINDOW_HEIGHT = 16 * 8;

    private final JobsScreen previousScreen;
    private final Job job;
    private final JobInstance jobInstance;
    private final List<PowerupInstance> rootPowerups;
    private final List<Powerup> allPowerups;
    private final int coins;

    private final PowerupWidget rootWidget;

    private boolean isScrolling = false;
    private double scrollX = 0.0;
    private double scrollY = 0.0;
    private double startX;
    private double startY;
    private double minX = Double.MAX_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;
    private double maxY = Double.MIN_VALUE;
    private boolean centered = false;

    private int lastWidth = 0;
    private int lastHeight = 0;

    public PowerUpsScreen(JobsScreen previousScreen, Job job, List<PowerupInstance> rootPowerups, List<Powerup> allPowerups, int coins) {
        super(JobsPlus.literal("Power-ups"));
        this.previousScreen = previousScreen;
        this.job = job;
        this.jobInstance = job.getJobInstance();
        this.rootPowerups = rootPowerups;
        this.allPowerups = allPowerups;
        this.coins = coins;

        PowerupInstance rootInstance = new PowerupInstance(null, null, jobInstance.getName() + " Power-ups", "Choose a power-up you want to buy.", jobInstance.getIconItem(), 0, 0);
        rootPowerups.forEach(rootInstance::addChild);

        this.rootWidget = PowerupWidget.run(job, rootInstance, allPowerups);
        this.rootWidget.setMinMaxXY(this);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float ticks) {

        this.startX = (double) (this.width - WINDOW_WIDTH) / 2;
        this.startY = (double) (this.height - WINDOW_HEIGHT) / 2;

        if ((this.scrollX > this.startX || this.scrollX < -(this.maxX - WINDOW_WIDTH - this.startX)) && this.maxX > WINDOW_WIDTH) {
            this.scrollX = Mth.clamp(this.scrollX, -(this.maxX - WINDOW_WIDTH - this.startX), this.startX);
        }
        if ((this.scrollY > this.startY || this.scrollY < -(this.maxY - WINDOW_HEIGHT - this.startY)) && this.maxY > WINDOW_HEIGHT) {
            this.scrollY = Mth.clamp(this.scrollY, -(this.maxY - WINDOW_HEIGHT - this.startY), this.startY);
        }

        if (!this.centered) {
            this.scrollX = this.maxX < WINDOW_WIDTH ? this.startX + (WINDOW_WIDTH - this.maxX) / 2 : Mth.clamp(Double.MAX_VALUE, -(this.maxX - WINDOW_WIDTH - this.startX), this.startX);
            this.scrollY = this.startY + (WINDOW_HEIGHT - this.maxY) / 2;
            this.centered = true;
        }

        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();

        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);
        fill(poseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        poseStack.translate(0.0, 0.0, -1);
        RenderSystem.depthFunc(518);
        fill(poseStack, (int) (WINDOW_WIDTH + startX) + 21, (int) (WINDOW_HEIGHT + startY) + 24, (int) this.startX - 16 - 6, (int) this.startY - 16 - 18, -16777216);
        RenderSystem.depthFunc(515);

        int windowX = Mth.floor(this.scrollX);
        int windowY = Mth.floor(this.scrollY);

        if (getHoveredWidget(mouseX, mouseY, windowX, windowY) != null && isHoveringInWindow(mouseX, mouseY)) {
            RenderColor.grayedOut();
            this.renderBackground(poseStack, windowX, windowY);
            RenderColor.normal();
        } else {
            this.renderBackground(poseStack, windowX, windowY);
        }


        if (jobInstance != null) {
            font.draw(poseStack, jobInstance.getName() + " (level " + job.getLevel() + ") Power-ups", (float) this.startX - 16, (float) this.startY - 16 - 12, 0x404040);
            drawRightAlignedString(poseStack, "Coins: " + coins, (int) ((float) this.startX + WINDOW_WIDTH + 16), (int) ((float) this.startY - 16 - 12), 0x404040);
        }

        poseStack.pushPose();
        if (isHoveringInWindow(mouseX, mouseY)) {
            PoseStack poseStack2 = RenderSystem.getModelViewStack();
            poseStack2.pushPose();
            poseStack2.translate(0, 0, 400.0);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableDepthTest();
            this.renderTooltip(poseStack, mouseX, mouseY, windowX, windowY, ticks);
            RenderSystem.disableDepthTest();
            poseStack2.popPose();
            RenderSystem.applyModelViewMatrix();
        }
        poseStack.popPose();

        if (this.width != this.lastWidth || this.height != this.lastHeight) {
            this.centered = false;
        }

        this.lastWidth = this.width;
        this.lastHeight = this.height;
    }

    private boolean isHoveringInWindow(int mouseX, int mouseY) {
        return mouseX >= this.startX - 17
                && mouseX <= this.startX + WINDOW_WIDTH + 15
                && mouseY >= this.startY - 18
                && mouseY <= this.startY + WINDOW_HEIGHT + 15;
    }

    public void renderBackground(@NotNull PoseStack poseStack, int windowX, int windowY) {
        RenderSystem.setShaderTexture(0, jobInstance.getPowerupBackground());

        for(int m = -1; m <= WINDOW_WIDTH / 16; ++m) {
            for(int n = -1; n <= WINDOW_HEIGHT / 16; ++n) {
                blit(poseStack, (int) (16 * m + this.startX), (int) (16 * n + this.startY), 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.rootWidget.draw(poseStack, windowX, windowY, this.startX, this.startY);
        RenderSystem.depthFunc(518);
        poseStack.translate(0.0, 0.0, -950.0);
        RenderSystem.colorMask(false, false, false, false);
        fill(poseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);

        //Border
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/advancements/window.png"));
        poseStack.popPose();

        int width = WINDOW_WIDTH + 32;
        int height = WINDOW_HEIGHT + 32;
        int textureWidth = 252;
        int textureHeight = 140;
        int borderSize = 9;
        int borderSizeTop = 18;
        blit(poseStack, (int) this.startX - 16 - borderSize, (int) this.startY - 16 - borderSizeTop, 0.0F, 0.0F, width / 2 + borderSize, textureHeight / 2 + borderSizeTop + borderSize + 1, 256, 256);
        blit(poseStack, (int) this.startX - 16 - borderSize + (width / 2 + borderSize), (int) this.startY - 16 - borderSizeTop, (float) textureWidth / 2 - borderSize * 2, 0.0F, width / 2 + borderSize, textureHeight / 2 + borderSizeTop + borderSize + 1, 256, 256);

        blit(poseStack, (int) this.startX - 16 - borderSize, (int) this.startY - 16 - borderSize + height / 2 + borderSize, 0.0F, (float) textureHeight / 2 - borderSize * 2, width / 2 + borderSize, textureHeight / 2 + borderSizeTop, 256, 256);
        blit(poseStack, (int) this.startX - 16 - borderSize + (width / 2 + borderSize), (int) this.startY - 16 - borderSize + height / 2 + borderSize, (float) textureWidth / 2 - borderSize * 2, (float) textureHeight / 2 - borderSize * 2, width / 2 + borderSize, textureHeight / 2 + borderSizeTop, 256, 256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickType) {

        PowerupWidget widget = this.getHoveredWidget((int) mouseX, (int) mouseY, Mth.floor(this.scrollX), Mth.floor(this.scrollY));
        if (widget != null) {
            playClientGUIClick();
            if (widget != rootWidget) {
                switch (widget.getPowerupState()) {
                    case ACTIVE, INACTIVE -> {
                        new PacketTogglePowerUpC2S(jobInstance, widget.getPowerupInstance()).sendToServer();
                        new PacketOpenPowerupsMenuC2S(jobInstance).sendToServer();
                    }
                    case NOT_OWNED -> {
                        ConfirmationMessageType confirmationMessageType = coins >= widget.getPowerupInstance().getPrice()
                                ? ConfirmationMessageType.BUY_POWER_UP
                                : ConfirmationMessageType.NOT_ENOUGH_COINS_POWERUP;
                        openConfirmScreen(confirmationMessageType, jobInstance, widget.getPowerupInstance());
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double speedX, double speedY, int clickType, double mouseX, double mouseY){
        if (clickType != 0){
            stopScrolling();
            return false;
        }

        return initiateScrolling(mouseX, mouseY);
    }

    private void stopScrolling(){
        this.isScrolling = false;
    }

    private boolean initiateScrolling(double f, double g){
        if (!this.isScrolling){
            this.isScrolling = true;
        }
        else{
            scroll(f, g);
        }

        return true;
    }

    public void scroll(double d, double e){
        if (isPositionWithinBound(this.maxX, this.minX, WINDOW_WIDTH)){
            this.scrollX = Mth.clamp(this.scrollX + d, -(this.maxX - WINDOW_WIDTH - this.startX), this.startX);
        }

        if (isPositionWithinBound(this.maxY, this.minY, WINDOW_HEIGHT)){
            this.scrollY = Mth.clamp(this.scrollY + e, -(this.maxY - WINDOW_HEIGHT - this.startY), this.startY);
        }
    }

    private boolean isPositionWithinBound(double max, double min, double boundary){
        return max - min > boundary;
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

    public void openConfirmScreen(ConfirmationMessageType messageType, JobInstance jobInstance, PowerupInstance powerup) {
        Minecraft.getInstance().setScreen(new ConfirmationScreen(this, messageType, jobInstance, powerup));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(previousScreen);
    }

    public void setMinMaxXY(int minX, int minY, int maxX, int maxY) {
        this.minX = Math.min(this.minX, minX);
        this.minY = Math.min(this.minY, minY);
        this.maxX = Math.max(this.maxX, maxX);
        this.maxY = Math.max(this.maxY, maxY);
    }
}
