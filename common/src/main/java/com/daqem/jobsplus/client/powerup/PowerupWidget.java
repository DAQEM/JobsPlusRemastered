package com.daqem.jobsplus.client.powerup;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.client.screen.PowerUpsScreen;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PowerupWidget extends GuiComponent {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    public final static int HEIGHT = 29;
    public final static int WIDTH = 28;

    private final Job job;
    private final JobInstance jobInstance;
    private final PowerupInstance powerupInstance;
    private final Powerup powerup;
    private final List<Powerup> allPowerups;
    @Nullable
    private final PowerupWidget parent;
    @Nullable
    private final PowerupWidget previousSibling;
    private final int childIndex;
    private final List<PowerupWidget> children = Lists.newArrayList();
    private PowerupWidget ancestor;
    @Nullable
    private PowerupWidget thread;
    private int x;
    private float y;
    private float mod;
    private float change;
    private float shift;

    public PowerupWidget(Job job, PowerupInstance powerupInstance, @Nullable Powerup powerup, List<Powerup> allPowerups, @Nullable PowerupWidget treeNodePosition, @Nullable PowerupWidget treeNodePosition2, int i, int j) {
        this.job = job;
        this.jobInstance = job.getJobInstance();
        this.powerupInstance = powerupInstance;
        this.powerup = powerup;
        this.allPowerups = allPowerups;
        this.parent = treeNodePosition;
        this.previousSibling = treeNodePosition2;
        this.childIndex = i;
        this.ancestor = this;
        this.x = j;
        this.y = -1.0F;
        PowerupWidget treeNodePosition3 = null;

        PowerupInstance powerupInstance2;
        for (Iterator<PowerupInstance> var7 = powerupInstance.getChildren().iterator(); var7.hasNext(); treeNodePosition3 = this.addChild(powerupInstance2, treeNodePosition3)) {
            powerupInstance2 = var7.next();
        }
    }

    public PowerupState getPowerupState() {
        if (this.powerup != null) {
            return powerup.getPowerupState();
        } else {
            if (this.parent == null) {
                return PowerupState.ACTIVE;
            } else {
                if (this.parent.getPowerupState() == PowerupState.ACTIVE || this.parent.getPowerupState() == PowerupState.INACTIVE) {
                    return job.getLevel() >= powerupInstance.getRequiredLevel() ? PowerupState.NOT_OWNED : PowerupState.LOCKED;
                } else {
                    return PowerupState.LOCKED;
                }
            }
        }
    }

    private static Powerup getPowerupForPowerupInstance(PowerupInstance powerupInstance, List<Powerup> allPowerups) {
        return getPowerupForPowerupInstanceRecursive(powerupInstance, allPowerups);
    }

    private static Powerup getPowerupForPowerupInstanceRecursive(PowerupInstance powerupInstance, List<Powerup> allPowerups) {
        for (Powerup powerup : allPowerups) {
            if (powerup.getPowerupInstance().equals(powerupInstance)) {
                return powerup;
            }
            Powerup powerupForPowerupInstance = getPowerupForPowerupInstanceRecursive(powerupInstance, powerup.getChildren());
            if (powerupForPowerupInstance != null) {
                return powerupForPowerupInstance;
            }
        }
        return null;
    }

    @Nullable
    private PowerupWidget addChild(PowerupInstance powerupInstance, @Nullable PowerupWidget treeNodePosition) {

        if (this.powerupInstance.getChildren().contains(powerupInstance)) {
            Powerup powerupForPowerupInstance = getPowerupForPowerupInstance(powerupInstance, allPowerups);
            treeNodePosition = new PowerupWidget(job, powerupInstance, powerupForPowerupInstance, allPowerups, this, treeNodePosition, this.children.size() + 1, this.x + 1);
            this.children.add(treeNodePosition);
        }


        PowerupInstance powerupInstance2;
        for (Iterator<PowerupInstance> var3 = powerupInstance.getChildren().iterator(); var3.hasNext(); treeNodePosition = this.addChild(powerupInstance2, treeNodePosition)) {
            powerupInstance2 = var3.next();
        }

        return treeNodePosition;
    }

    private void firstWalk() {
        if (this.children.isEmpty()) {
            if (this.previousSibling != null) {
                this.y = this.previousSibling.y + 1.0F;
            } else {
                this.y = 0.0F;
            }

        } else {
            PowerupWidget treeNodePosition = null;

            PowerupWidget treeNodePosition2;
            for(Iterator<PowerupWidget> var2 = this.children.iterator(); var2.hasNext(); treeNodePosition = treeNodePosition2.apportion(treeNodePosition == null ? treeNodePosition2 : treeNodePosition)) {
                treeNodePosition2 = var2.next();
                treeNodePosition2.firstWalk();
            }

            this.executeShifts();
            float f = (this.children.get(0).y + this.children.get(this.children.size() - 1).y) / 2.0F;
            if (this.previousSibling != null) {
                this.y = this.previousSibling.y + 1.0F;
                this.mod = this.y - f;
            } else {
                this.y = f;
            }

        }
    }

    private float secondWalk(float f, int i, float g) {
        this.y += f;
        this.x = i;
        if (this.y < g) {
            g = this.y;
        }

        PowerupWidget treeNodePosition;
        for(Iterator<PowerupWidget> var4 = this.children.iterator(); var4.hasNext(); g = treeNodePosition.secondWalk(f + this.mod, i + 1, g)) {
            treeNodePosition = var4.next();
        }

        return g;
    }

    private void thirdWalk(float f) {
        this.y += f;

        for (PowerupWidget treeNodePosition : this.children) {
            treeNodePosition.thirdWalk(f);
        }

    }

    private void executeShifts() {
        float f = 0.0F;
        float g = 0.0F;

        for(int i = this.children.size() - 1; i >= 0; --i) {
            PowerupWidget treeNodePosition = this.children.get(i);
            treeNodePosition.y += f;
            treeNodePosition.mod += f;
            g += treeNodePosition.change;
            f += treeNodePosition.shift + g;
        }

    }

    @Nullable
    private PowerupWidget previousOrThread() {
        if (this.thread != null) {
            return this.thread;
        } else {
            return !this.children.isEmpty() ? this.children.get(0) : null;
        }
    }

    @Nullable
    private PowerupWidget nextOrThread() {
        if (this.thread != null) {
            return this.thread;
        } else {
            return !this.children.isEmpty() ? this.children.get(this.children.size() - 1) : null;
        }
    }

    private PowerupWidget apportion(PowerupWidget treeNodePosition) {
        if (this.previousSibling != null) {
            PowerupWidget treeNodePosition2 = this;
            PowerupWidget treeNodePosition3 = this;
            PowerupWidget treeNodePosition4 = this.previousSibling;
            PowerupWidget treeNodePosition5 = this.parent.children.get(0);
            float f = this.mod;
            float g = this.mod;
            float h = treeNodePosition4.mod;

            float i;
            for (i = treeNodePosition5.mod; treeNodePosition4.nextOrThread() != null && treeNodePosition2.previousOrThread() != null; g += treeNodePosition3.mod) {
                treeNodePosition4 = treeNodePosition4.nextOrThread();
                treeNodePosition2 = treeNodePosition2.previousOrThread();
                treeNodePosition5 = treeNodePosition5.previousOrThread();
                treeNodePosition3 = treeNodePosition3.nextOrThread();
                treeNodePosition3.ancestor = this;
                float j = treeNodePosition4.y + h - (treeNodePosition2.y + f) + 1.0F;
                if (j > 0.0F) {
                    treeNodePosition4.getAncestor(this, treeNodePosition).moveSubtree(this, j);
                    f += j;
                    g += j;
                }

                h += treeNodePosition4.mod;
                f += treeNodePosition2.mod;
                i += treeNodePosition5.mod;
            }

            if (treeNodePosition4.nextOrThread() != null && treeNodePosition3.nextOrThread() == null) {
                treeNodePosition3.thread = treeNodePosition4.nextOrThread();
                treeNodePosition3.mod += h - g;
            } else {
                if (treeNodePosition2.previousOrThread() != null && treeNodePosition5.previousOrThread() == null) {
                    treeNodePosition5.thread = treeNodePosition2.previousOrThread();
                    treeNodePosition5.mod += f - i;
                }

                treeNodePosition = this;
            }

        }
        return treeNodePosition;
    }

    private void moveSubtree(PowerupWidget treeNodePosition, float f) {
        float g = (float)(treeNodePosition.childIndex - this.childIndex);
        if (g != 0.0F) {
            treeNodePosition.change -= f / g;
            this.change += f / g;
        }

        treeNodePosition.shift += f;
        treeNodePosition.y += f;
        treeNodePosition.mod += f;
    }

    private PowerupWidget getAncestor(PowerupWidget treeNodePosition, PowerupWidget treeNodePosition2) {
        return this.ancestor != null && treeNodePosition.parent.children.contains(this.ancestor) ? this.ancestor : treeNodePosition2;
    }

    private void finalizePosition() {
        if (!this.children.isEmpty()) {

            for (PowerupWidget treeNodePosition : this.children) {
                treeNodePosition.finalizePosition();
            }
        }
    }
    public static PowerupWidget run(Job job, PowerupInstance powerupInstance, List<Powerup> allPowerups) {
        PowerupWidget treeNodePosition = new PowerupWidget(job, powerupInstance, null, allPowerups, null, null, 1, 0);
        treeNodePosition.firstWalk();
        float f = treeNodePosition.secondWalk(0.0F, 0, treeNodePosition.y);
        if (f < 0.0F) {
            treeNodePosition.thirdWalk(-f);
        }

        treeNodePosition.finalizePosition();
        return treeNodePosition;
    }

    public List<PowerupWidget> getChildren() {
        return this.children;
    }

    public @Nullable PowerupWidget getParent() {
        return this.parent;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public PowerupInstance getPowerupInstance() {
        return this.powerupInstance;
    }

    public boolean isHovered(int mouseX, int mouseY, int offsetX, int offsetY) {
        int xPosition = Mth.floor(this.x * WIDTH) + offsetX;
        int yPosition = Mth.floor(this.y * HEIGHT) + offsetY;
        int iconWidth = 23;
        int iconHeight = 23;

        return mouseX >= xPosition && mouseX <= xPosition + iconWidth
                && mouseY >= yPosition && mouseY <= yPosition + iconHeight;
    }
    
    public void draw(PoseStack poseStack, int offsetX, int offsetY, double startX, double startY) {
        this.getChildren().forEach(child -> {
            if (child.getParent() != null) {
                if (child.getPowerupState() == PowerupState.LOCKED) {
                    RenderColor.grayedOut();
                }
                drawConnectivity(poseStack, Mth.floor(child.getParent().getX() * WIDTH) + offsetX, Mth.floor(child.getParent().getY() * HEIGHT) + offsetY, Mth.floor(child.getX() * WIDTH) + offsetX, Mth.floor(child.getY() * HEIGHT) + offsetY, false);
                if (child.getPowerupState() == PowerupState.LOCKED) {
                    RenderColor.normal();
                }
            }
        });
        this.getChildren().forEach(child -> {
            if (child.getParent() != null) {
                if (child.getPowerupState() == PowerupState.LOCKED) {
                    RenderColor.grayedOut();
                }
                drawConnectivity(poseStack, Mth.floor(child.getParent().getX() * WIDTH) + offsetX, Mth.floor(child.getParent().getY() * HEIGHT) + offsetY, Mth.floor(child.getX() * WIDTH) + offsetX, Mth.floor(child.getY() * HEIGHT) + offsetY, true);
                if (child.getPowerupState() == PowerupState.LOCKED) {
                    RenderColor.normal();
                }
            }
        });
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        drawPowerupIcon(poseStack, null, this.getPowerupState(), Mth.floor(this.x * WIDTH) + offsetX, Mth.floor(this.y * HEIGHT) + offsetY);

        if (Mth.floor(this.x * WIDTH) + offsetX < startX + 246
                && Mth.floor(this.x * WIDTH) + offsetX > startX - 31
                && Mth.floor(this.y * HEIGHT) + offsetY < startY + 134
                && Mth.floor(this.y * HEIGHT) + offsetY > startY - 40
        ) {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(powerupInstance.getIcon(), Mth.floor(this.x * WIDTH) + offsetX + 4, Mth.floor(this.y * HEIGHT) + offsetY + 4);
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, powerupInstance.getIcon(), Mth.floor(this.x * WIDTH) + offsetX + 3, Mth.floor(this.y * HEIGHT) + offsetY + 2);
        }

        this.getChildren().forEach(child -> child.draw(poseStack, offsetX, offsetY, startX, startY));
    }

    public void drawHovered(PoseStack poseStack, int offsetX, int offsetY) {
        int x = Mth.floor(this.x * WIDTH) + offsetX;
        int y = Mth.floor(this.y * HEIGHT) + offsetY;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        Font minecraftFont = Minecraft.getInstance().font;

        int i = 0;
        int stringLength = String.valueOf(i).length();
        int extraWidthCondition = i > 1 ? minecraftFont.width("  ") + minecraftFont.width("0") * stringLength * 2 + minecraftFont.width("/") : 0;
        int totalWidth = 29 + minecraftFont.width(powerupInstance.getName()) + extraWidthCondition + 3 + 5;

        List<FormattedCharSequence> descriptions = Language.getInstance().getVisualOrder(findOptimalLines(JobsPlus.literal(powerupInstance.getDescription()).copy(), totalWidth));


        for(FormattedCharSequence descriptionLine : descriptions) {
            totalWidth = Math.max(totalWidth, minecraftFont.width(descriptionLine) + 7);
        }

        totalWidth = Math.max(totalWidth, minecraftFont.width("Required level: " + powerupInstance.getRequiredLevel()) + 7);
        totalWidth = Math.max(totalWidth, minecraftFont.width("Price: " + powerupInstance.getPrice()) + 7);

        int var10001 = descriptions.size();
        int textHeight = 7;
        int heightBetweenText = 3;
        int priceExtraHeight = 7;
        if (this.getPowerupState() == PowerupState.ACTIVE || this.getPowerupState() == PowerupState.INACTIVE) {
            textHeight = 0;
            heightBetweenText = 0;
            priceExtraHeight = 0;
        }
        int lines = 32 + var10001 * 9 + priceExtraHeight + textHeight + heightBetweenText + textHeight;

        this.render9Sprite(poseStack, x - 4, y + 2, totalWidth, lines, 10, 200, 26, 0, 52);
        if (this.getPowerupState() == PowerupState.LOCKED || this.getPowerupState() == PowerupState.NOT_OWNED) {
            blit(poseStack, x - 4, y + 2, 0, 29, totalWidth - 4, 20);
            blit(poseStack, x, y + 2, 200 - totalWidth + 4, 29, totalWidth - 4, 20);
        } else {
            blit(poseStack, x - 4, y + 2, 0, 3, totalWidth - 4, 20);
            blit(poseStack, x, y + 2, 200 - totalWidth + 4, 3, totalWidth - 4, 20);
        }

        drawPowerupIcon(poseStack, powerupInstance, this.getPowerupState(), x, y);

        Minecraft.getInstance().font.draw(poseStack, powerupInstance.getName(), x + 28, y + 8, 0xFFFFFFFF);
        for (int i1 = 0; i1 < descriptions.size(); i1++) {
            FormattedCharSequence line = descriptions.get(i1);
            Minecraft.getInstance().font.draw(poseStack, line, x, y + 27 + i1 * 9, 0xFFAAAAAA);
        }
        if (this.getPowerupState() == PowerupState.NOT_OWNED || this.getPowerupState() == PowerupState.LOCKED) {
            Minecraft.getInstance().font.draw(poseStack, "Required level: " + powerupInstance.getRequiredLevel(), x, y + 27 + descriptions.size() * 9 + priceExtraHeight, 0xFFAAAAAA);
            Minecraft.getInstance().font.draw(poseStack, "Price: " + powerupInstance.getPrice(), x, y + 27 + descriptions.size() * 9 + textHeight + heightBetweenText + priceExtraHeight, 0xFFAAAAAA);
        }

        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(powerupInstance.getIcon(), x + 4, y + 4);
        Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, powerupInstance.getIcon(), x + 3, y + 2);
    }

    protected void render9Sprite(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        this.blit(poseStack, i, j, p, q, m, m);
        this.renderRepeating(poseStack, i + m, j, k - m - m, m, p + m, q, n - m - m, o);
        this.blit(poseStack, i + k - m, j, p + n - m, q, m, m);
        this.blit(poseStack, i, j + l - m, p, q + o - m, m, m);
        this.renderRepeating(poseStack, i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
        this.blit(poseStack, i + k - m, j + l - m, p + n - m, q + o - m, m, m);
        this.renderRepeating(poseStack, i, j + m, m, l - m - m, p, q + m, n, o - m - m);
        this.renderRepeating(poseStack, i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
        this.renderRepeating(poseStack, i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);

        if (this.getPowerupState() == PowerupState.NOT_OWNED || this.getPowerupState() == PowerupState.LOCKED) {
            this.blit(poseStack, i + 2, j + l - 28, p + 2, q + o - m + 5, k - 4, 1);
        }
    }

    protected void renderRepeating(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, int p) {
        for(int q = 0; q < k; q += o) {
            int r = i + q;
            int s = Math.min(o, k - q);

            for(int t = 0; t < l; t += p) {
                int u = j + t;
                int v = Math.min(p, l - t);
                this.blit(poseStack, r, u, m, n, s, v);
            }
        }

    }

    private void drawConnectivity(PoseStack poseStack, int xFrom, int yFrom, int xTo, int yTo, boolean isWhite) {
        int spacing = 2;
        int centerHeight = 24 / 2;
        int xOffset = 24;
        int xFromOffset = xFrom + xOffset;
        int xToOffset = xTo - 2;
        int yFromCenter = yFrom + centerHeight;
        int yToCenter = yTo + centerHeight;
        int color = isWhite ? 0xFFFFFFFF : 0xFF000000;

        if (isWhite) {
            hLine(poseStack, xFromOffset, xFromOffset + spacing, yFromCenter, color);
            vLine(poseStack, xFromOffset + spacing, yFromCenter, yToCenter, color);
            hLine(poseStack, xToOffset, xToOffset + spacing, yToCenter, color);
        } else {
            hLine(poseStack, xFromOffset, xToOffset + 1, yFromCenter - 1, color);
            hLine(poseStack, xFromOffset, xToOffset + 1, yFromCenter, color);
            hLine(poseStack, xFromOffset, xToOffset + 1, yFromCenter + 1, color);

            vLine(poseStack, xToOffset - 1, yToCenter, yFromCenter, color);
            vLine(poseStack, xToOffset + 1, yToCenter, yFromCenter, color);

            hLine(poseStack, xToOffset - 1, xToOffset + spacing, yToCenter - 1, color);
            hLine(poseStack, xToOffset - 1, xToOffset + spacing, yToCenter, color);
            hLine(poseStack, xToOffset - 1, xToOffset + spacing, yToCenter + 1, color);
        }
    }

    private void drawPowerupIcon(PoseStack poseStack, PowerupInstance powerup, PowerupState state, int x, int y) {
        switch (state) {
            case ACTIVE -> drawActivePowerupIcon(poseStack, powerup, x, y, 24, 24);
            case INACTIVE -> drawInactivePowerupIcon(poseStack, powerup, x, y, 24, 24);
            case NOT_OWNED -> drawNotOwnedPowerupIcon(poseStack, powerup, x, y, 24, 24);
            case LOCKED -> drawLockedPowerupIcon(poseStack, powerup, x, y, 24, 24);
        }
    }

    private void drawActivePowerupIcon(PoseStack poseStack, PowerupInstance powerup, int x, int y, int width, int height) {
        blit(poseStack, x, y, 1, 129, width, height);
    }

    private void drawInactivePowerupIcon(PoseStack poseStack, PowerupInstance powerup, int x, int y, int width, int height) {
        RenderColor.red();
        blit(poseStack, x, y, 1, 129, width, height);
        RenderColor.normal();
    }

    private void drawNotOwnedPowerupIcon(PoseStack poseStack, PowerupInstance powerup, int x, int y, int width, int height) {
        blit(poseStack, x, y, 1, 155, width, height);
    }

    private void drawLockedPowerupIcon(PoseStack poseStack, PowerupInstance powerup, int x, int y, int width, int height) {
        RenderColor.grayedOut();
        blit(poseStack, x, y, 1, 155, width, height);
        RenderColor.normal();
    }

    public PowerupWidget getHoveredWidget(int mouseX, int mouseY, int offsetX, int offsetY) {
        if (this.isHovered(mouseX, mouseY, offsetX, offsetY)) {
            return this;
        } else {
            for (PowerupWidget child : this.getChildren()) {
                PowerupWidget hoveredWidget = child.getHoveredWidget(mouseX, mouseY, offsetX, offsetY);
                if (hoveredWidget != null) {
                    return hoveredWidget;
                }
            }
        }
        return null;
    }

    private List<FormattedText> findOptimalLines(Component component, int i) {
        StringSplitter stringSplitter = Minecraft.getInstance().font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;
        int[] var6 = new int[]{0, 10, -10, 25, -25};

        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            int j = var6[var8];
            List<FormattedText> list2 = stringSplitter.splitLines(component, i - j, Style.EMPTY);
            float g = Math.abs(getMaxWidth(stringSplitter, list2) - (float)i);
            if (g <= 10.0F) {
                return list2;
            }

            if (g < f) {
                f = g;
                list = list2;
            }
        }

        return list;
    }

    private static float getMaxWidth(StringSplitter stringSplitter, List<FormattedText> list) {
        Stream<FormattedText> var10000 = list.stream();
        Objects.requireNonNull(stringSplitter);
        return (float)var10000.mapToDouble(stringSplitter::stringWidth).max().orElse(0.0);
    }

    public void setMinMaxXY(PowerUpsScreen screen) {
        this.children.forEach(child -> child.setMinMaxXY(screen));
        screen.setMinMaxXY(Mth.floor(this.x * WIDTH), Mth.floor(this.y * HEIGHT), Mth.floor(this.x * WIDTH) + 24, Mth.floor(this.y * HEIGHT) + 24);
    }
}
