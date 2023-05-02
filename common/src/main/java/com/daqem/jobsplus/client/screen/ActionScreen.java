package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.util.chat.ChatColor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;

public class ActionScreen extends Screen {

    private static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/action_screen.png");
    private static final int IMAGE_WIDTH = 326;
    private static final int IMAGE_HEIGHT = 166;

    private static final int SCROLL_BAR_HEIGHT = 123;
    private static final int SCROLL_WHEEL_START_X = 0;
    private static final int SCROLL_WHEEL_START_Y = 201;
    private static final int SCROLL_WHEEL_WIDTH = 12;
    private static final int SCROLL_WHEEL_HEIGHT = 15;
    private static final int SCROLL_WHEEL_POS_X = 306;
    private static final int SCROLL_WHEEL_POS_Y = 16;

    private static final int BOTTOM_BUTTON_OFFSET_X = 5;
    private static final int BOTTOM_BUTTON_OFFSET_Y = 1;
    private static final int BOTTOM_BUTTON_WIDTH = 18;
    private static final int BOTTOM_BUTTON_HEIGHT = 18;

    private static final int BACK_BUTTON_START_X = 60;
    private static final int BACK_BUTTON_START_Y = IMAGE_HEIGHT + 35;
    private static final int BACK_BUTTON_POS_X = BOTTOM_BUTTON_OFFSET_X;
    private static final int BACK_BUTTON_POS_Y = IMAGE_HEIGHT + BOTTOM_BUTTON_OFFSET_Y;

    private static final int FORWARD_BUTTON_START_X = 78;
    private static final int FORWARD_BUTTON_START_Y = IMAGE_HEIGHT + 35;
    private static final int FORWARD_BUTTON_POS_X = IMAGE_WIDTH - BOTTOM_BUTTON_WIDTH - BOTTOM_BUTTON_OFFSET_X;
    private static final int FORWARD_BUTTON_POS_Y = IMAGE_HEIGHT + BOTTOM_BUTTON_OFFSET_Y;

    private static final int TOP_BUTTON_START_X = 120;
    private static final int TOP_BUTTON_START_Y = IMAGE_HEIGHT + 35;
    private static final int TOP_BUTTON_WIDTH = 26;
    private static final int TOP_BUTTON_HEIGHT = 22;
    private static final int TOP_BUTTON_POS_X = 156;
    private static final int TOP_BUTTON_POS_Y = -TOP_BUTTON_HEIGHT;
    private static final int TOP_BUTTON_SPACE_X = 3;
    private static final int TOP_BUTTON_OFFSET_Y = 3;

    private static final int REWARDS_ICON_START_X = 108;
    private static final int REWARDS_ICON_START_Y = IMAGE_HEIGHT + 35;
    private static final int REWARDS_ICON_POS_X = 163;
    private static final int REWARDS_ICON_POS_Y = -17;
    private static final int REWARDS_ICON_WIDTH = 12;
    private static final int REWARDS_ICON_HEIGHT = 16;

    private static final int CONDITIONS_ICON_START_X = 96;
    private static final int CONDITIONS_ICON_START_Y = IMAGE_HEIGHT + 35;
    private static final int CONDITIONS_ICON_POS_X = 192;
    private static final int CONDITIONS_ICON_POS_Y = -17;
    private static final int CONDITIONS_ICON_WIDTH = 12;
    private static final int CONDITIONS_ICON_HEIGHT = 16;

    private final Screen lastScreen;
    private final JobInstance jobInstance;
    private final Action action;
    private final int actionIndex;
    private final Keyboard keyboard;
    private Mouse mouse;
    private TopButtonType activeTopButton;
    private int startX;
    private int startY;

    private final float scrollOffset;

    public ActionScreen(Screen lastScreen, JobInstance jobInstance, Action action) {
        super(JobsPlus.literal("Action"));

        this.lastScreen = lastScreen;
        this.jobInstance = jobInstance;
        this.action = action;
        this.actionIndex = jobInstance.getActions().indexOf(action);
        this.scrollOffset = 0;
        this.keyboard = new Keyboard(this);
        this.mouse = new ActionScreen.Mouse(this, 0, 0);
        this.activeTopButton = TopButtonType.REWARDS;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();
        RenderSystem.setShaderTexture(0, BACKGROUND);

        this.startX = (this.width - IMAGE_WIDTH) / 2;
        this.startY = (this.height - IMAGE_HEIGHT) / 2;

        this.mouse = new ActionScreen.Mouse(this, mouseX, mouseY);

        drawTopButtons(poseStack);

        this.blit(poseStack, 0, 0, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        this.blit(poseStack, SCROLL_WHEEL_POS_X, (int) (SCROLL_WHEEL_POS_Y + SCROLL_BAR_HEIGHT * scrollOffset), SCROLL_WHEEL_START_X, SCROLL_WHEEL_START_Y, SCROLL_WHEEL_WIDTH, SCROLL_WHEEL_HEIGHT);

        drawBottomButtons(poseStack);

        this.blit(poseStack, REWARDS_ICON_POS_X, REWARDS_ICON_POS_Y, REWARDS_ICON_START_X, REWARDS_ICON_START_Y, REWARDS_ICON_WIDTH, REWARDS_ICON_HEIGHT);
        this.blit(poseStack, CONDITIONS_ICON_POS_X, CONDITIONS_ICON_POS_Y, CONDITIONS_ICON_START_X, CONDITIONS_ICON_START_Y, CONDITIONS_ICON_WIDTH, CONDITIONS_ICON_HEIGHT);

        font.draw(poseStack, String.valueOf(actionIndex + 1), startX + 140 - font.width(String.valueOf(actionIndex + 1)), startY + 13, 0xA9A9A9);

        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);
        font.draw(poseStack, ChatColor.bold() + jobInstance.getName(), (startX + 7) / 1.5F, (startY + 10) / 1.5F, jobInstance.getColorDecimal());
        poseStack.popPose();

        font.draw(poseStack, ChatFormatting.STRIKETHROUGH + "                                 ", startX + 7, startY + 20, 0xFFFFFF);

        poseStack.pushPose();
        poseStack.scale(1.2F, 1.2F, 1.2F);
        font.draw(poseStack, ChatColor.bold() + action.getName().getString(), (startX + 7) / 1.2F, (startY + 30) / 1.2F, new Color(jobInstance.getColorDecimal()).darker().getRGB());
        poseStack.popPose();

        try {
            drawActionDescription(poseStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawActionDescription(PoseStack poseStack) {
        float scale = 1.0F;
        int tries = 0;
        while (getTextHeightForDescription(poseStack, scale) > 112) {
            if (tries > 1000) break;
            tries++;
            scale -= 0.025F;
        }
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        String description = action.getDescription().getString();
        String[] descriptionSplit = description.split(" ");
        int lineCount = 0;
        tries = 0;
        while (descriptionSplit.length > 0) {
            if (tries > 1000) break;
            tries++;
            StringBuilder line = new StringBuilder();
            int tries2 = 0;
            while (font.width(line + descriptionSplit[0]) < (130 / scale)) {
                if (tries2 > 1000) break;
                tries2++;
                line.append(descriptionSplit[0]).append(" ");
                descriptionSplit = Arrays.copyOfRange(descriptionSplit, 1, descriptionSplit.length);
                if (descriptionSplit.length == 0) break;
            }
            font.draw(poseStack, line.toString(), (startX + 7) / scale, (startY + 44 + (lineCount * (9 * scale))) / scale, 0x555555);
            lineCount++;
        }
        poseStack.popPose();
    }

    private float getTextHeightForDescription(PoseStack poseStack, float scale) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        String description = action.getDescription().getString();
        String[] descriptionSplit = description.split(" ");
        int lineCount = 0;
        int tries = 0;
        while (descriptionSplit.length > 0) {
            if (tries > 1000) break;
            tries++;
            StringBuilder line = new StringBuilder();
            int tries2 = 0;
            while (font.width(line + descriptionSplit[0]) < (130 / scale)) {
                if (tries2 > 1000) break;
                tries2++;
                line.append(descriptionSplit[0]).append(" ");
                descriptionSplit = Arrays.copyOfRange(descriptionSplit, 1, descriptionSplit.length);
                if (descriptionSplit.length == 0) break;
            }
            lineCount++;
        }
        poseStack.popPose();
        return lineCount * (9 * scale);
    }

    private void drawTopButtons(@NotNull PoseStack poseStack) {
        int offset = 0;
        if (activeTopButton != TopButtonType.REWARDS) {
            RenderColor.grayedOut();
            if (mouse.isHoveringTopButton(TopButtonType.REWARDS)) {
                RenderColor.buttonHover();
            }
            offset = TOP_BUTTON_OFFSET_Y;
        }
        this.blit(poseStack, TOP_BUTTON_POS_X, TOP_BUTTON_POS_Y + offset, TOP_BUTTON_START_X, TOP_BUTTON_START_Y, TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT);
        RenderColor.normal();

        offset = 0;
        if (activeTopButton != TopButtonType.CONDITIONS) {
            RenderColor.grayedOut();
            if (mouse.isHoveringTopButton(TopButtonType.CONDITIONS)) {
                RenderColor.buttonHover();
            }
            offset = TOP_BUTTON_OFFSET_Y;
        }
        this.blit(poseStack, TOP_BUTTON_POS_X + TOP_BUTTON_WIDTH + TOP_BUTTON_SPACE_X, TOP_BUTTON_POS_Y + offset, TOP_BUTTON_START_X, TOP_BUTTON_START_Y, TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT);
        RenderColor.normal();
    }

    private void drawBottomButtons(@NotNull PoseStack poseStack) {
        drawBackButton(poseStack);
        drawForwardButton(poseStack);
        RenderColor.normal();
    }

    private void drawForwardButton(@NotNull PoseStack poseStack) {
        if (!hasMultipleActions()) {
            RenderColor.grayedOut();
        }
        checkForBottomButtonHover(BottomButtonType.FORWARD);
        this.blit(poseStack, FORWARD_BUTTON_POS_X, FORWARD_BUTTON_POS_Y, FORWARD_BUTTON_START_X, FORWARD_BUTTON_START_Y, BOTTOM_BUTTON_WIDTH, BOTTOM_BUTTON_HEIGHT);
        RenderColor.normal();
    }

    private void drawBackButton(@NotNull PoseStack poseStack) {
        if (!hasMultipleActions()) {
            RenderColor.grayedOut();
        }
        checkForBottomButtonHover(BottomButtonType.BACK);
        this.blit(poseStack, BACK_BUTTON_POS_X, BACK_BUTTON_POS_Y, BACK_BUTTON_START_X, BACK_BUTTON_START_Y, BOTTOM_BUTTON_WIDTH, BOTTOM_BUTTON_HEIGHT);
        RenderColor.normal();
    }

    private void checkForBottomButtonHover(BottomButtonType bottomButtonType) {
        if (mouse.isHoveringBottomButton(bottomButtonType)) {
            if (hasMultipleActions()) {
                RenderColor.buttonHover();
            } else {
                RenderColor.grayedOutSelected();
            }
        }
    }

    private void previousPage() {
        if (this.hasMultipleActions()) {
            switchActionScreen(-1, jobInstance.getActions().size() - 1, actionIndex - 1);
        }
    }

    private void nextPage() {
        if (this.hasMultipleActions()) {
            switchActionScreen(jobInstance.getActions().size(), 0, actionIndex + 1);
        }
    }

    private void showRewards() {
        ScreenHelper.playClientGUIClick();
        this.activeTopButton = TopButtonType.REWARDS;

    }

    private void showConditions() {
        ScreenHelper.playClientGUIClick();
        this.activeTopButton = TopButtonType.CONDITIONS;
    }

    private void toggleTopButtons(int offset) {
        ScreenHelper.playClientGUIClick();
        int nextOrdinal = (activeTopButton.ordinal() + offset) % TopButtonType.values().length;
        activeTopButton = TopButtonType.values()[nextOrdinal];
    }

    private void switchActionScreen(int lastIndex, int fistIndex, int nextIndex) {
        ScreenHelper.playClientGUIClick();
        Action action;
        if (nextIndex == lastIndex) {
            action = this.jobInstance.getActions().get(fistIndex);
        } else {
            action = this.jobInstance.getActions().get(nextIndex);
        }
        refreshScreen(action);
    }

    private boolean hasMultipleActions() {
        return jobInstance.getActions().size() > 1;
    }

    private void refreshScreen(Action action) {
        Minecraft.getInstance().setScreen(new ActionScreen(this.lastScreen, this.jobInstance, action));
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

    @Override
    public void blit(@NotNull PoseStack poseStack, int posX, int posY, int startX, int startY, int width, int height) {
        blit(poseStack, this.startX + posX, this.startY + posY, startX, startY, width, height, 362, 362);
    }

    private enum TopButtonType {
        REWARDS(JobsPlus.translatable("gui.action_screen.rewards"), TOP_BUTTON_POS_X, TOP_BUTTON_POS_Y),
        CONDITIONS(JobsPlus.translatable("gui.action_screen.conditions"), TOP_BUTTON_POS_X + TOP_BUTTON_WIDTH + TOP_BUTTON_SPACE_X, TOP_BUTTON_POS_Y);

        private final Component component;
        private final int x;
        private final int y;

        TopButtonType(Component component, int x, int y) {
            this.component = component;
            this.x = x;
            this.y = y;
        }
    }

    private enum BottomButtonType {
        BACK(BACK_BUTTON_POS_X, BACK_BUTTON_POS_Y),
        FORWARD(FORWARD_BUTTON_POS_X, FORWARD_BUTTON_POS_Y);

        private final int x;
        private final int y;

        BottomButtonType(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickType) {
        this.mouse.onClick();
        return super.mouseClicked(mouseX, mouseY, clickType);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.keyboard.onKeyPressed(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private record Mouse(ActionScreen actionScreen, int x, int y) {

        public boolean isHoveringBottomButton(ActionScreen.BottomButtonType bottomButtonType) {
            return isBetween(this.actionScreen.startX + bottomButtonType.x,
                    this.actionScreen.startY + bottomButtonType.y,
                    BOTTOM_BUTTON_WIDTH, BOTTOM_BUTTON_HEIGHT);
        }

        public boolean isHoveringTopButton(ActionScreen.TopButtonType topButtonType) {
            return isBetween(this.actionScreen.startX + topButtonType.x,
                    this.actionScreen.startY + topButtonType.y,
                    TOP_BUTTON_WIDTH, TOP_BUTTON_HEIGHT);
        }

        public boolean isBetween(int x, int y, int width, int height) {
            return this.x >= x && this.x < x + width && this.y > y && this.y < y + height;
        }

        public void onClick() {
            if (isHoveringBottomButton(BottomButtonType.BACK)) {
                this.actionScreen.previousPage();
            } else if (isHoveringBottomButton(BottomButtonType.FORWARD)) {
                this.actionScreen.nextPage();
            }

            if (isHoveringTopButton(TopButtonType.REWARDS)) {
                this.actionScreen.showRewards();
            } else if (isHoveringTopButton(TopButtonType.CONDITIONS)) {
                this.actionScreen.showConditions();
            }
        }
    }

    private record Keyboard(ActionScreen actionScreen) {

        private static final int KEY_BACK = GLFW.GLFW_KEY_LEFT;
        private static final int KEY_FORWARD = GLFW.GLFW_KEY_RIGHT;

        public void onKeyPressed(int keyCode) {
            if (keyCode == KEY_BACK) {
                if (Screen.hasControlDown() || Screen.hasShiftDown()) {
                    this.actionScreen.toggleTopButtons(-1);
                } else {
                    pressedBack();
                }
            } else if (keyCode == KEY_FORWARD) {
                if (Screen.hasControlDown() || Screen.hasShiftDown()) {
                    this.actionScreen.toggleTopButtons(+1);
                } else {
                    pressedForward();
                }
            }
        }

        private void pressedBack() {
            this.actionScreen.previousPage();
        }

        private void pressedForward() {
            this.actionScreen.nextPage();
        }
    }
}
