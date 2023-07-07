package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.render.RenderColor;

import com.daqem.jobsplus.networking.c2s.PacketApprovedUpdateC2S;
import com.daqem.jobsplus.networking.c2s.PacketConfirmationC2S;
import com.daqem.jobsplus.networking.c2s.PacketOpenMenuC2S;
import com.daqem.jobsplus.networking.c2s.PacketOpenPowerupsMenuC2S;
import com.daqem.jobsplus.networking.utils.ConfirmationButtonType;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;

public class ConfirmationScreen extends AbstractScreen {

    private static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/confirmation_screen.png");
    private static final int BUTTON_WIDTH = 75;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_TOP_OFFSET = 1;
    private static final int BUTTON_SIDE_OFFSET = 3;
    private static final int BUTTON_START_POS = 11;
    private static final int LEFT_START_POS = 0;
    private static final int LEFT_WIDTH = 4;
    private static final int MIDDLE_START_POS = 5;
    private static final int MIDDLE_WIDTH = 1;
    private static final int RIGHT_START_POS = 7;
    private static final int RIGHT_WIDTH = 4;
    private static final int IMAGE_HEIGHT = 28;
    private static final int TEXT_COLOR = 0x333333;
    private int imageWidth;
    private final ConfirmationMessageType messageType;
    private final Screen lastScreen;
    private int startX;
    private int startY;

    private Mouse mouse;

    private @Nullable JobInstance job;
    private @Nullable PowerupInstance powerup;

    public ConfirmationScreen(Screen lastScreen, ConfirmationMessageType messageType, @Nullable JobInstance jobInstance, @Nullable PowerupInstance powerup) {
        this(lastScreen, messageType);
        this.job = jobInstance;
        this.powerup = powerup;
    }

    public ConfirmationScreen(Screen lastScreen, ConfirmationMessageType messageType, @Nullable JobInstance job) {
        this(lastScreen, messageType);
        this.job = job;
    }

    protected ConfirmationScreen(Screen lastScreen, ConfirmationMessageType messageType) {
        super(JobsPlus.literal("Confirmation"));
        this.lastScreen = lastScreen;
        this.messageType = messageType;
    }

    @Override
    protected void init() {
        int requiredCoins = getRequiredCoins();
        if (requiredCoins > 0) {
            messageType.withObjects(requiredCoins);
        }
        this.imageWidth = font.width(messageType.getMessage());
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();
        RenderSystem.setShaderTexture(0, BACKGROUND);

        mouse = new Mouse(this, mouseX, mouseY);

        startX = (this.width - imageWidth) / 2;
        startY = (this.height - IMAGE_HEIGHT) / 2;

        drawBackground(poseStack);
        drawButtons(poseStack);

        drawMessage(poseStack);
        drawButtonMessages(poseStack);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void drawButtons(@NotNull PoseStack poseStack) {
        if (messageType.getButtonType() == ConfirmationButtonType.BACK) {
            drawButton(poseStack, ButtonType.BACK);
        } else {
            drawButton(poseStack, ButtonType.YES);
            drawButton(poseStack, ButtonType.CANCEL);
        }
    }

    private void drawButtonMessages(@NotNull PoseStack poseStack) {
        if (messageType.getButtonType() == ConfirmationButtonType.BACK) {
            drawButtonMessage(poseStack, JobsPlus.translatable("gui.confirmation.back"), ButtonType.BACK);
        } else {
            drawButtonMessage(poseStack, JobsPlus.translatable("gui.confirmation.yes"), ButtonType.YES);
            drawButtonMessage(poseStack, JobsPlus.translatable("gui.confirmation.cancel"), ButtonType.CANCEL);
        }
    }

    private void drawButtonMessage(@NotNull PoseStack poseStack, Component component, ButtonType buttonType) {
        font.draw(poseStack, component, width / 2F - font.width(component) / 2F + buttonType.offset, startY + 34, TEXT_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickType) {
        if ((mouse.isHoveringButton(ButtonType.BACK) && messageType.getButtonType() == ConfirmationButtonType.BACK)
                || mouse.isHoveringButton(ButtonType.CANCEL)) {
            if (messageType == ConfirmationMessageType.JOBS_PLUS_UPDATE) {
                new PacketApprovedUpdateC2S().sendToServer();
            }
            closeWithClick(false);
            return true;
        }
        if (mouse.isHoveringButton(ButtonType.YES)) {
            if (messageType == ConfirmationMessageType.BUY_POWER_UP) {
                if (powerup != null && job != null) {
                    new PacketConfirmationC2S(messageType, job, powerup).sendToServer();
                }
            } else if (messageType == ConfirmationMessageType.START_JOB_FREE
                    || messageType == ConfirmationMessageType.START_JOB_PAID
                    || messageType == ConfirmationMessageType.STOP_JOB) {
                if (job != null) {
                    new PacketConfirmationC2S(messageType, job).sendToServer();
                }
            } else if (messageType == ConfirmationMessageType.JOBS_PLUS_UPDATE) {
                try {
                    Util.getPlatform().openUri(new URI("https://daqem.com/discord"));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            closeWithClick(true);
        }
        return super.mouseClicked(mouseX, mouseY, clickType);
    }

    private void drawMessage(@NotNull PoseStack poseStack) {
        if (messageType.getMessage().getString().contains("\n")) {
            String[] lines = messageType.getMessage().getString().split("\n");
            for (int i = 0; i < lines.length; i++) {
                drawCenteredString(poseStack, lines[i], (int) (width / 2F), (int) (height / 2F - (font.lineHeight * lines.length) / 2F + (font.lineHeight * i)), TEXT_COLOR);
            }
            imageWidth = font.width(Arrays.stream(lines).max(Comparator.comparingInt(String::length)).orElse(""));
        } else {
            font.draw(poseStack, messageType.getMessage(), startX, height / 2F - (font.lineHeight) / 2F, TEXT_COLOR);
        }
    }

    private void drawBackground(@NotNull PoseStack poseStack) {
        final int offset = 5;
        blit(poseStack, startX - LEFT_WIDTH - offset, startY, LEFT_START_POS, 0, LEFT_WIDTH, IMAGE_HEIGHT);
        for (int i = -offset; i < imageWidth + offset; i++) {
            blit(poseStack, startX + i, startY, MIDDLE_START_POS, 0, MIDDLE_WIDTH, IMAGE_HEIGHT);
        }
        blit(poseStack, startX + imageWidth + offset, startY, RIGHT_START_POS, 0, RIGHT_WIDTH, IMAGE_HEIGHT);
    }

    private void drawButton(PoseStack poseStack, ButtonType buttonType) {
        if (mouse.isHoveringButton(buttonType)) {
            RenderColor.buttonHover();
        }
        blit(poseStack,
                (int) ((width / 2F) - (BUTTON_WIDTH / 2F) + buttonType.offset), startY + IMAGE_HEIGHT + BUTTON_TOP_OFFSET,
                BUTTON_START_POS, 0,
                BUTTON_WIDTH, BUTTON_HEIGHT);
        RenderColor.normal();
    }

    private void closeWithClick(boolean withPacket) {
        playClientGUIClick();
        if (withPacket) {
            if (lastScreen instanceof JobsScreen jobsScreen) {
                new PacketOpenMenuC2S(jobsScreen).sendToServer();
            } else if (lastScreen instanceof PowerUpsScreen powerUpsScreen) {
                new PacketOpenPowerupsMenuC2S(job).sendToServer();
            } else {
                new PacketOpenMenuC2S().sendToServer();
            }
        } else {
            this.onClose();
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(lastScreen);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getRequiredCoins() {
        int requiredCoins = 0;
        switch (messageType.getRequireCoinsType()) {
            case START_JOB -> {
                if (job != null) {
                    requiredCoins = job.getPrice();
                }
            }
            case POWER_UP -> {
                if (powerup != null) {
                    requiredCoins = powerup.getPrice();
                }
            }
        }
        return requiredCoins;
    }

    private enum ButtonType {
        YES((-BUTTON_WIDTH / 2) - BUTTON_SIDE_OFFSET),
        CANCEL((BUTTON_WIDTH / 2) + BUTTON_SIDE_OFFSET),
        BACK(0);

        final int offset;

        ButtonType(int offset) {
            this.offset = offset;
        }
    }

    private record Mouse(ConfirmationScreen confirmationScreen, int x, int y) {

        public boolean isHoveringButton(ButtonType buttonType) {
            return isBetween((int) ((confirmationScreen.width / 2F) - (BUTTON_WIDTH / 2F) + buttonType.offset),
                    confirmationScreen.startY + IMAGE_HEIGHT + BUTTON_TOP_OFFSET,
                    BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        public boolean isBetween(int x, int y, int width, int height) {
            return this.x >= x && this.x < x + width && this.y >= y && this.y < y + height;
        }
    }
}
