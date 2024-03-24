package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.powerup.PowerupWidget;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.networking.c2s.PacketOpenPowerupsMenuC2S;
import com.daqem.jobsplus.networking.c2s.PacketTogglePowerUpC2S;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PowerUpsScreen extends AbstractScreen {

    private static final int WINDOW_WIDTH = 16 * 15;
    private static final int WINDOW_HEIGHT = 16 * 8;

    private final JobsScreen previousScreen;
    private final Job job;
    private final JobInstance jobInstance;
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
        super(JobsPlus.translatable("gui.title.powerups"));
        this.previousScreen = previousScreen;
        this.job = job;
        this.jobInstance = job.getJobInstance();
        this.coins = coins;

        PowerupInstance rootInstance = new PowerupInstance(null, null, null, jobInstance.getName().getString() + " Power-ups", "Choose a power-up you want to buy.", jobInstance.getIconItem(), 0, 0) {

            @Override
            public List<PowerupInstance> getChildren() {
                return rootPowerups;
            }
        };

        this.rootWidget = PowerupWidget.run(job, rootInstance, allPowerups);
        this.rootWidget.setMinMaxXY(this);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
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

        this.renderBackground(guiGraphics);
        guiGraphics.fill((int) (WINDOW_WIDTH + startX) + 21, (int) (WINDOW_HEIGHT + startY) + 24, (int) this.startX - 16 - 6, (int) this.startY - 16 - 18, 0x80000000);


        int windowX = Mth.floor(this.scrollX);
        int windowY = Mth.floor(this.scrollY);


        if (getHoveredWidget(mouseX, mouseY, windowX, windowY) != null && isHoveringInWindow(mouseX, mouseY)) {
            RenderColor.grayedOut();
            this.renderBackground(guiGraphics, windowX, windowY);
            RenderColor.normal();
        } else {
            this.renderBackground(guiGraphics, windowX, windowY);
        }

        if (jobInstance != null) {
            guiGraphics.drawString(font, jobInstance.getName().getString() + " (level " + job.getLevel() + ") Power-ups", (int) this.startX - 16, (int) this.startY - 16 - 12, 0x404040, false);
            drawRightAlignedString(guiGraphics, "Coins: " + coins, (int) ((float) this.startX + WINDOW_WIDTH + 16), (int) ((float) this.startY - 16 - 12), 0x404040);
        }

        if (isHoveringInWindow(mouseX, mouseY)) {
            PoseStack hoverPoseStack = RenderSystem.getModelViewStack();
            hoverPoseStack.pushPose();
            hoverPoseStack.translate(0, 0, 400);
            RenderSystem.applyModelViewMatrix();
            this.renderTooltip(guiGraphics, mouseX, mouseY, windowX, windowY);
            hoverPoseStack.popPose();
        }

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

    public void renderBackground(@NotNull GuiGraphics guiGraphics, int windowX, int windowY) {
        for(int m = -1; m <= WINDOW_WIDTH / 16; ++m) {
            for(int n = -1; n <= WINDOW_HEIGHT / 16; ++n) {
                guiGraphics.blit(jobInstance.getPowerupBackground(), (int) (16 * m + this.startX), (int) (16 * n + this.startY), 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }
        guiGraphics.enableScissor((int) this.startX - 16, (int) this.startY - 16, (int) this.startX - 16 + WINDOW_WIDTH + 32, (int) this.startY - 16 + WINDOW_HEIGHT + 32);
        this.rootWidget.draw(guiGraphics, windowX, windowY, this.startX, this.startY);
        guiGraphics.disableScissor();

        //Border
        PoseStack borderPoseStack = RenderSystem.getModelViewStack();
        borderPoseStack.pushPose();
        borderPoseStack.translate(0, 0, 300);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int width = WINDOW_WIDTH + 32;
        int height = WINDOW_HEIGHT + 32;
        int textureWidth = 252;
        int textureHeight = 140;
        int borderSize = 9;
        int borderSizeTop = 18;
        guiGraphics.blit(new ResourceLocation("textures/gui/advancements/window.png"), (int) this.startX - 16 - borderSize, (int) this.startY - 16 - borderSizeTop, 0.0F, 0.0F, width / 2 + borderSize, textureHeight / 2 + borderSizeTop + borderSize + 1, 256, 256);
        guiGraphics.blit(new ResourceLocation("textures/gui/advancements/window.png"), (int) this.startX - 16 - borderSize + (width / 2 + borderSize), (int) this.startY - 16 - borderSizeTop, (float) textureWidth / 2 - borderSize * 2, 0.0F, width / 2 + borderSize, textureHeight / 2 + borderSizeTop + borderSize + 1, 256, 256);

        guiGraphics.blit(new ResourceLocation("textures/gui/advancements/window.png"), (int) this.startX - 16 - borderSize, (int) this.startY - 16 - borderSize + height / 2 + borderSize, 0.0F, (float) textureHeight / 2 - borderSize * 2, width / 2 + borderSize, textureHeight / 2 + borderSizeTop, 256, 256);
        guiGraphics.blit(new ResourceLocation("textures/gui/advancements/window.png"), (int) this.startX - 16 - borderSize + (width / 2 + borderSize), (int) this.startY - 16 - borderSize + height / 2 + borderSize, (float) textureWidth / 2 - borderSize * 2, (float) textureHeight / 2 - borderSize * 2, width / 2 + borderSize, textureHeight / 2 + borderSizeTop, 256, 256);
        borderPoseStack.popPose();
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

    @SuppressWarnings("SameReturnValue")
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

    private void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int windowX, int windowY) {
        PowerupWidget widget = getHoveredWidget(mouseX, mouseY, windowX, windowY);

        if (widget != null) {
            widget.drawHovered(guiGraphics, windowX, windowY);
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
