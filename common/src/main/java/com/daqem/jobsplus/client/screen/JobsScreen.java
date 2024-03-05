package com.daqem.jobsplus.client.screen;

import com.daqem.arc.api.action.IAction;
import com.daqem.arc.client.gui.action.ActionScreen;
import com.daqem.itemrestrictions.data.ItemRestriction;
import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.config.JobsPlusConfig;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.player.JobsPlayerData;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.util.chat.ChatColor;
import com.daqem.jobsplus.util.experience.ExperienceHandler;
import com.google.common.collect.ImmutableCollection;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;

public class JobsScreen extends AbstractScreen {

    public static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/jobs_screen.png");
    private final int imageWidth = 326;
    private final int imageHeight = 166;
    private final JobsPlayerData jobsPlayerData;
    private final LinkedList<Job> jobs = new LinkedList<>();
    private final Map<ItemRestriction, Integer> jobItemRestrictions = new HashMap<>();
    private final LinkedList<Job> shownJobs = new LinkedList<>();
    private int activeRightButton;
    private int activeLeftButton;
    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;
    private int startX;
    private int startY;
    private int firstHiddenIndex;
    private int firstHiddenIndexRight;
    private Job job;
    private int selectedButton;
    private float scrollOffsetRight;
    private boolean scrollingRight;
    private int startIndexRight;

    public JobsScreen(CompoundTag dataTag) {
        super(JobsPlus.translatable("gui.title.jobs"));
        this.activeLeftButton = dataTag.getInt(Constants.ACTIVE_LEFT_BUTTON);
        this.activeRightButton = dataTag.getInt(Constants.ACTIVE_RIGHT_BUTTON);
        this.selectedButton = dataTag.getInt(Constants.SELECTED_BUTTON);
        this.scrollOffset = dataTag.getFloat(Constants.SCROLL_OFFSET);
        this.scrollOffsetRight = dataTag.getFloat(Constants.SCROLL_OFFSET_RIGHT);
        this.startIndex = dataTag.getInt(Constants.START_INDEX);
        this.startIndexRight = dataTag.getInt(Constants.START_INDEX_RIGHT);
        this.jobsPlayerData = JobsPlayerData.fromNBT(dataTag);
        this.jobs.addAll(jobsPlayerData.activeJobs());
        this.jobs.addAll(jobsPlayerData.inactiveJobs());
        this.jobs.sort(Comparator.comparing(Job::getLevel).reversed().thenComparing(job -> job.getJobInstance().getName().getString()));

        if (selectedButton < 0) {
            this.job = null;
        } else {
            setJob();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();
        RenderSystem.setShaderTexture(0, BACKGROUND);

        this.startX = (this.width - this.imageWidth) / 2;
        this.startY = (this.height - this.imageHeight) / 2;

        this.firstHiddenIndex = this.startIndex + 4;
        this.firstHiddenIndexRight = getActiveRightButton() == 1 ? this.startIndexRight + 21 : this.startIndexRight + 4;

        this.setShownJobs(firstHiddenIndex);

        this.renderBackgroundImage(guiGraphics);
        this.renderScrollWheel(guiGraphics);
        this.renderButtons(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderItems(guiGraphics);
        this.renderTexts(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();

        jobItemRestrictions.clear();
        if (hasJobSelected()) {
            Map<ItemRestriction, Integer> itemRestrictions = getSelectedJob().getJobInstance().getItemRestrictions();
            //sort them by value
            itemRestrictions.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> jobItemRestrictions.put(x.getKey(), x.getValue()));

            jobItemRestrictions.putAll(itemRestrictions);
        }
    }

    private void setShownJobs(int firstHiddenIndex) {
        shownJobs.clear();
        if (activeLeftButton == 0) {
            for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
                shownJobs.add(jobs.get(i));
            }
        } else if (activeLeftButton == 1 || activeLeftButton == 2) {
            for (int i = this.startIndex; i < firstHiddenIndex; ++i) {
                if ((activeLeftButton == 1 && getEnabledJobs().size() > i)
                        || (activeLeftButton == 2 && getDisabledJobs().size() > i)) {
                    shownJobs.add(activeLeftButton == 1 ? getEnabledJobs().get(i) : getDisabledJobs().get(i));
                }
            }
        }
    }

    private void renderBackgroundImage(GuiGraphics guiGraphics) {
        blitThis(guiGraphics, 0, 0, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void renderScrollWheel(GuiGraphics guiGraphics) {
        if (!isScrollBarActive()) RenderColor.grayedOut();
        blitThis(guiGraphics, 127, (int) (16 + (123.0F * getScrollOffset())), 0, 207, 12, 15);
        RenderColor.normal();
    }

    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        mouseX -= startX;
        mouseY -= startY;
        if (isBetween(mouseX, mouseY, 6, -22, 32, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.all_jobs"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28, -22, 32 + 28, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.performing_jobs"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 28, -22, 32 + 28 + 28, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.not_performing_jobs"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 150, -22, 32 + 150, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.job_info"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 150, -22, 32 + 28 + 150, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.job_crafting_recipes"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 150, -22, 32 + 28 + 28 + 150, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.job_powerups"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 28 + 150, -22, 32 + 28 + 28 + 28 + 150, 0)) {
            guiGraphics.renderTooltip(font, JobsPlus.translatable("gui.job_how_to_get_exp"), mouseX + startX, mouseY + startY);
        }
        if (activeRightButton == 1) {
            if (hasJobSelected()) {
                ItemRestriction itemRestriction = getHoveredItemRestriction(mouseX, mouseY);
                if (itemRestriction != null) {
                    List<Component> literal = new ArrayList<>(List.of(JobsPlus.translatable("restrictions")));

                    String str = itemRestriction.getRestrictionTypes().stream()
                            .map(restrictionType -> JobsPlus.translatable(restrictionType.getTranslationKey()).getString())
                            .reduce((s, s2) -> s + ", " + s2).orElse("");

                    List<MutableComponent> components = Arrays.stream(str.split(", "))
                            .map(s -> JobsPlus.literal(s).withStyle(ChatFormatting.GRAY))
                            .toList();

                    literal.addAll(components);

                    guiGraphics.renderTooltip(font, literal, Optional.empty(), mouseX + startX, mouseY + startY);
                }
            }
        }
    }

    private void drawTopButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, int buttonAmount, int posXOffset, int startYOffset, int activeButton) {
        for (int i = 0; i < buttonAmount; ++i) {
            int posX = posXOffset + (28 * i);
            if (activeButton != i && isBetween(mouseX - startX, mouseY - startY, posX, -19, posX + 25, 0)) {
                RenderSystem.setShaderColor(0.7F, 0.7F, 1F, 1);
                drawNotSelectedTopButton(guiGraphics, activeButton, i, posX);
                RenderColor.normal();
            } else {
                drawNotSelectedTopButton(guiGraphics, activeButton, i, posX);
            }
            blitThis(guiGraphics, posX + 3, -21, 0, imageHeight + 56 + startYOffset + (20 * i), 20, 20);
        }
    }

    private void drawNotSelectedTopButton(GuiGraphics guiGraphics, int activeButton, int i, int posX) {
        blitThis(guiGraphics, posX, -22, 142, activeButton == i ? imageHeight + 22 : imageHeight, 26, 22);
    }

    private void drawButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, int i, Job job) {
        int j = i - this.startIndex;
        int i1 = (this.startY + 16) - startY + j * 35 - 1;

        //BUTTONS
        if (i == this.selectedButton) {
            RenderColor.grayedOutSelected();
        } else {
            if (isBetween(mouseX, mouseY, this.startX + 7, i1 + startY, this.startX + 121, i1 + startY + 34)) {
                RenderColor.normalSelected();
            }
        }
        blitThis(guiGraphics, 7, i1, 26, this.imageHeight, 116, 35);
        RenderColor.normal();

        //BADGES
        int level = job.getLevel();
        int maxLevel = job.getJobInstance().getMaxLevel();
        if (level >= maxLevel * 0.75) blitThis(guiGraphics, 7 + 5, i1 + 5, 168 + (27 * 3), imageHeight, 27, 27);
        else if (level >= maxLevel * 0.50) blitThis(guiGraphics, 7 + 5, i1 + 5, 168 + (27 * 2), imageHeight, 27, 27);
        else if (level >= maxLevel * 0.25) blitThis(guiGraphics, 7 + 5, i1 + 5, 168 + 27, imageHeight, 27, 27);
        else blitThis(guiGraphics, 7 + 5, i1 + 5, 168, imageHeight, 27, 27);
    }

    public void renderButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        //SETTINGS
        if (isBetween(mouseX, mouseY, 3, height - 20, 18, height - 4))
            RenderColor.grayedOut();
        guiGraphics.blit(BACKGROUND, 3, height - 20, 276, 166, 16, 16, 362, 362);
        RenderColor.normal();

        //DISCORD
        if (isBetween(mouseX, mouseY, width - 19, height - 20, width - 4, height - 5))
            RenderColor.grayedOut();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
        guiGraphics.blit(BACKGROUND, width * 2 - 38, height * 2 - 40, 276 + 16, 166, 32, 32, 362, 362);
        RenderColor.normal();
        guiGraphics.pose().popPose();

        // LEFT BUTTONS
        drawTopButtons(guiGraphics, mouseX, mouseY, 3, 6, 0, activeLeftButton);
        //RIGHT BUTTONS
        drawTopButtons(guiGraphics, mouseX, mouseY, 4, 156, 60, activeRightButton);
        //JOB BUTTONS
        drawJobButtons(guiGraphics, mouseX, mouseY, firstHiddenIndex);

        if (hasJobSelected() && (getActiveRightButton() == 1 || getActiveRightButton() == 3)) {
            //BACKGROUND
            blitThis(guiGraphics, 158, 15, 183, 198, 144, 140);

            //SCROLLBAR
            blitThis(guiGraphics, 305, 15, 126, 15, 14, 140);

            //SCROLL WHEEL
            blitThis(guiGraphics, 306, (int) (16 + (123.0F * getScrollOffsetRight())), 0, 207, 12, 15);
        }
        if (hasJobSelected()) {
            if (activeRightButton == 0 && (getSelectedJobLevel() == 0 || JobsPlusConfig.allowJobStopRefund.get())) {
                drawBigButton(guiGraphics, mouseX, mouseY);
                // CRAFTING RECIPE BUTTONS
            } else if (activeRightButton == 1) {
                //BUTTONS
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < jobItemRestrictions.size(); ++i) {
                    int j = i - this.startIndexRight;
                    int k = 158 + j % 3 * 48;
                    int l = j / 3;
                    int i1 = 15 + l * 20;

                    if (isBetween(mouseX - startX, mouseY - startY, k, i1, k + 47, i1 + 19)) {
                        RenderColor.buttonHover();
                    }
                    blitThis(guiGraphics, k, i1, 26, 201, 48, 20);
                    RenderColor.normal();
                }
                //POWERUP BUTTONS
            } else if (activeRightButton == 2) {
                drawBigButton(guiGraphics, mouseX, mouseY);
            } else if (activeRightButton == 3) {
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < getSelectedJob().getJobInstance().getActions().size(); ++i) {
                    int j = i - this.startIndexRight;
                    int i1 = 15 + j * 35;

                    //BUTTONS
                    if (isBetween(mouseX, mouseY, this.startX + 158, i1 + startY, this.startX + 158 + 144 - 1, i1 + startY + 34)) {
                        RenderColor.normalSelected();
                    }
                    blitThis(guiGraphics, 158, i1, 26, this.imageHeight, 116, 31);
                    blitThis(guiGraphics, 187, i1, 27, this.imageHeight, 115, 31);
                    blitThis(guiGraphics, 158, i1 + 4, 26, this.imageHeight + 4, 116, 31);
                    blitThis(guiGraphics, 187, i1 + 4, 27, this.imageHeight + 4, 115, 31);
                    RenderColor.normal();
                }
            }
        }
    }

    private void drawBigButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isBetween(mouseX - startX, mouseY - startY, 169, 132, 169 + 138, 132 + 17)) {
            RenderSystem.setShaderColor(0.7F, 0.7F, 1F, 1);
        } else {
            RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 1);
        }
        blitThis(guiGraphics, 150 + 19, 132, 26, imageHeight + 105, 139, 18);
        RenderColor.normal();
    }

    private void drawJobButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, int firstHiddenIndex) {
        if (activeLeftButton == 0) {
            for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
                Job job = jobs.get(i);
                drawButtons(guiGraphics, mouseX, mouseY, i, job);
            }
        } else if (activeLeftButton == 1 || activeLeftButton == 2) {
            for (int i = this.startIndex; i < firstHiddenIndex; ++i) {
                if ((activeLeftButton == 1 && getEnabledJobs().size() > i) || (activeLeftButton == 2 && getDisabledJobs().size() > i)) {
                    Job job = activeLeftButton == 1 ? getEnabledJobs().get(i) : getDisabledJobs().get(i);
                    drawButtons(guiGraphics, mouseX, mouseY, i, job);
                }
            }
        }
    }

    private void drawButtonTexts(GuiGraphics guiGraphics, Job job, int i, boolean offset) {
        int j = offset ? i - this.startIndex : i;
        int i1 = (this.startY + 16) + j * 35;
        int level = job.getLevel();
        int exp = getJobExperience(job);
        if (level != 0) {
            int maxExp = ExperienceHandler.getMaxExperienceForLevel(level);
            guiGraphics.drawString(font, job.getJobInstance().getName().withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)), startX + 7 + 3 + 35, i1 + 3, 16777215, false);
            guiGraphics.drawString(font, ChatColor.aqua() + JobsPlus.translatable("gui.level", ChatColor.reset(), level).getString(), startX + 7 + 3 + 35, i1 + 14, 16777215, false);
            if (level != 100)
                guiGraphics.drawString(font, ChatColor.aqua() + JobsPlus.translatable("gui.exp", ChatColor.reset(), (int) ((double) exp / maxExp * 100), "%").getString(), startX + 7 + 3 + 35, i1 + 23, 16777215, false);
        } else {
            guiGraphics.drawString(font, job.getJobInstance().getName().withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true)), startX + 7 + 3 + 35, i1 + 3, 16777215, false);
            guiGraphics.drawString(font, ChatColor.aqua() + JobsPlus.translatable("gui.want_this_job").getString(), startX + 7 + 3 + 35, i1 + 14, 16777215, false);
            guiGraphics.drawString(font, ChatColor.aqua() + JobsPlus.translatable("gui.price", ChatColor.reset(), hasFreeClaimableJobs() ? 0 : job.getJobInstance().getPrice()).getString(), startX + 7 + 3 + 35, i1 + 23, 16777215, false);
        }
    }

    private void drawJobButtonsTexts(GuiGraphics guiGraphics, int firstHiddenIndex) {
        if (activeLeftButton == 0) {
            for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
                drawButtonTexts(guiGraphics, jobs.get(i), i, true);
            }
        } else if (activeLeftButton == 1 || activeLeftButton == 2) {
            int count = 0;
            for (int i = this.startIndex; i < firstHiddenIndex; ++i) {
                if ((activeLeftButton == 1 && getEnabledJobs().size() > i) || (activeLeftButton == 2 && getDisabledJobs().size() > i)) {
                    Job job = activeLeftButton == 1 ? getEnabledJobs().get(i) : getDisabledJobs().get(i);
                    drawButtonTexts(guiGraphics, job, count++, false);
                }
            }
        }
    }

    public void renderTexts(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.jobs").getString(), startX + 7, startY + 6, 16777215, false);
        drawRightAlignedString(guiGraphics, ChatColor.darkGray() + JobsPlus.translatable("gui.coins.top", getCoins()).getString(), startX + 140, startY + 6, 16777215);

        drawJobButtonsTexts(guiGraphics, firstHiddenIndex);

        if (!hasJobSelected()) {
            if (activeRightButton == 0) drawNoJobSelected(guiGraphics, "info");
            else if (activeRightButton == 1) drawNoJobSelected(guiGraphics, "crafting");
            else if (activeRightButton == 2) drawNoJobSelected(guiGraphics, "powerups");
            else if (activeRightButton == 3) drawNoJobSelected(guiGraphics, "how_to_get_exp");
        } else {
            int centerR = startX + (imageWidth + 150) / 2;
            if (activeRightButton == 0) {
                drawnBigJobTitle(guiGraphics);
                drawJobInfo(guiGraphics);
                if (getSelectedJobLevel() == 0) {
                    drawCenteredString(guiGraphics, ChatColor.white() + JobsPlus.translatable("gui.job.start").getString(), centerR, startY + 137, 16777215);
                } else if (JobsPlusConfig.allowJobStopRefund.get()) {
                    drawCenteredString(guiGraphics, ChatColor.white() + JobsPlus.translatable("gui.job.stop").getString(), centerR, startY + 137, 16777215);
                }
            } else if (activeRightButton == 1) {
                drawCenteredString(guiGraphics, ChatColor.darkGray() + JobsPlus.translatable("gui.restrictions").getString(), centerR, startY + 6, 16777215);
            } else if (activeRightButton == 2) {
                drawCenteredString(guiGraphics, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups.powerups").getString(), centerR, startY + 20, 16777215);

                List<Powerup> allPowerups = getSelectedJob().getPowerupManager().getAllPowerups();
                guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups.available", getSelectedJob().getJobInstance().getAllPowerups().stream().filter(p -> !allPowerups.stream().map(Powerup::getPowerupInstance).toList().contains(p)).count()).getString(), startX + 150 + 34, startY + 6 + 14 + 36, 16777215, false);
                guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups.active", allPowerups.stream().filter(p -> p.getPowerupState() == PowerupState.ACTIVE).count()).getString(), startX + 150 + 34, startY + 6 + 14 + 60, 16777215, false);
                guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups.inactive", allPowerups.stream().filter(p -> p.getPowerupState() == PowerupState.INACTIVE).count()).getString(), startX + 150 + 34, startY + 6 + 14 + 70, 16777215, false);

                drawCenteredString(guiGraphics, ChatColor.white() + JobsPlus.translatable("gui.powerups.open_menu").getString(), centerR, startY + 137, 16777215);
            } else if (activeRightButton == 3) {
                drawCenteredString(guiGraphics, ChatColor.darkGray() + JobsPlus.translatable("gui.job_how_to_get_exp").append(" ").append(JobsPlus.translatable("gui.click_for_details")).getString(), centerR, startY + 6, 16777215);
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < getSelectedJob().getJobInstance().getActions().size(); ++i) {
                    int j = i - this.startIndexRight;
                    int i1 = 18 + j * 35;

                    IAction action = getSelectedJob().getJobInstance().getActions().get(i);
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(1.2F, 1.2F, 1.2F);
                    drawDynamicComponent(guiGraphics, action.getShortDescription(), (this.startX + 164) / 1.2F, (i1 + startY + 3) / 1.2F, 103, 0x55FFFF);
                    guiGraphics.pose().popPose();
                    drawRightAlignedString(guiGraphics, String.valueOf(i + 1), this.startX + 298, i1 + startY + 5, 0xA9A9A9);
                    drawDynamicComponent(guiGraphics, action.getName(), this.startX + 164, i1 + startY + 17, 103, 0xDDFFFFFF);
                }
            }
        }
    }

    public void renderItems(GuiGraphics guiGraphics) {
        if (minecraft == null) return;

        renderJobIcons(guiGraphics, firstHiddenIndex);

        if (hasJobSelected()) {
            if (activeRightButton == 1) {
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < jobItemRestrictions.size(); i++) {
                    int j = i - this.startIndexRight;
                    int xOffset = startX + 160 + j % 3 * 48;
                    int l = j / 3;
                    int yOffset = startY + 17 + l * 20;
                    ItemRestriction itemRestriction = jobItemRestrictions.keySet().stream().toList().get(i);
                    guiGraphics.renderFakeItem(itemRestriction.getIcon(), xOffset, yOffset);
                    int level = jobItemRestrictions.get(itemRestriction);
                    guiGraphics.drawString(font, String.valueOf(level), xOffset + 22, yOffset + 4, job.getLevel() >= level ? 0x55FF55 : 0xFF5555, false);
                }
            }
        }
    }

    private void renderJobIcons(GuiGraphics guiGraphics, int firstHiddenIndex) {
        for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
            int originalSpot = i - startIndex;
            if (shownJobs.size() > originalSpot) {
                guiGraphics.renderFakeItem(
                        shownJobs.get(originalSpot).getJobInstance().getIconItem(),
                        startX + 17, startY + 25 + originalSpot * 35);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int clickType) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        this.scrolling = false;
        this.scrollingRight = false;

        //SETTINGS
        //noinspection StatementWithEmptyBody
        if (isBetween(mouseX, mouseY, 3, height - 20, 19, height - 4)) {
            //TODO: SETTINGS
        }

        //DISCORD
        if (isBetween(mouseX, mouseY, width - 19, height - 20, width - 4, height - 5)) {
            try {
                Util.getPlatform().openUri(new URI("https://daqem.com/discord"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        //CONSTRUCTION TABLE
        if (activeRightButton == 1) {
            if (isBetween(mouseX, mouseY, startX + 162, startY + 168, startX + 162 + 24, startY + 168 + 23)) {
                playClientGUIClick();
//                JobsPlusJeiPlugin.showJEIPage(ModItems.CONSTRUCTION_TABLE.get().getDefaultInstance());
            }
        }

        //JOBS LIST
        for (int l = this.startIndex; l < this.startIndex + 4; ++l) {
            if (isBetween(mouseX - startX, mouseY - startY, 7, 16, 122, 35 * (l - startIndex + 1) + 15)) {
                try {
                    selectedButton = l;
                    setJob();
                    scrollOffsetRight = 0;
                    startIndexRight = 0;
                    playClientGUIClick();
                    return true;
                } catch (IndexOutOfBoundsException ignore) {
                }
            }
        }

        mouseX = mouseX - startX;
        mouseY = mouseY - startY;

        //SCROLL BAR
        if (isBetween(mouseX, mouseY, 127, 17, 139, 155)) {
            this.scrolling = true;
        }

        //SCROLL BAR CRAFTING
        if (isBetween(mouseX, mouseY, 302, 17, 311, 155)) {
            this.scrollingRight = true;
        }

        //ALL JOBS BUTTON
        if (isBetween(mouseX, mouseY, 6, -22, 32, 0)) {
            playClientGUIClick();
            if (activeLeftButton != 0) {
                selectedButton = -1;
                job = null;
                startIndex = 0;
                scrollOffset = 0;
                scrollOffsetRight = 0;
                startIndexRight = 0;
            }
            activeLeftButton = 0;
        }
        //PERFORMING JOBS BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28, -22, 32 + 28, 0)) {
            playClientGUIClick();
            if (activeLeftButton != 1) {
                selectedButton = -1;
                job = null;
                startIndex = 0;
                scrollOffset = 0;
                scrollOffsetRight = 0;
                startIndexRight = 0;
            }
            activeLeftButton = 1;
        }
        //NOT PERFORMING JOBS BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 28, -22, 32 + 28 + 28, 0)) {
            playClientGUIClick();
            if (activeLeftButton != 2) {
                selectedButton = -1;
                job = null;
                startIndex = 0;
                scrollOffset = 0;
                scrollOffsetRight = 0;
                startIndexRight = 0;
            }
            activeLeftButton = 2;
        }
        //JOB INFO BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 150, -22, 32 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 0;
            scrollOffsetRight = 0;
            startIndexRight = 0;
        }
        //CRAFTING RECIPES BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 150, -22, 32 + 28 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 1;
            scrollOffsetRight = 0;
            startIndexRight = 0;
        }
        // POWERUPS BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 150, -22, 32 + 28 + 28 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 2;
            scrollOffsetRight = 0;
            startIndexRight = 0;
        }
        // HOW TO GET EXP BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 28 + 150, -22, 32 + 28 + 28 + 28 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 3;
            scrollOffsetRight = 0;
            startIndexRight = 0;
        }
        // RIGHT BUTTONS
        if (hasJobSelected()) {
            // JOB START STOP BUTTON
            if (activeRightButton == 0) {
                if (isBetween(mouseX, mouseY, 169, 132, 169 + 138, 132 + 18)) {
                    if (getSelectedJobLevel() == 0) {
                        playClientGUIClick();
                        if (hasFreeClaimableJobs()) {
                            openConfirmScreen(ConfirmationMessageType.START_JOB_FREE, job.getJobInstance());
                        } else {
                            if (getCoins() >= getJobStartPrice(job.getJobInstance()))
                                openConfirmScreen(ConfirmationMessageType.START_JOB_PAID, job.getJobInstance());
                            else
                                openConfirmScreen(ConfirmationMessageType.NOT_ENOUGH_COINS_START, job.getJobInstance());
                        }
                    } else if (JobsPlusConfig.allowJobStopRefund.get()) {
                        playClientGUIClick();
                        openConfirmScreen(ConfirmationMessageType.STOP_JOB, job.getJobInstance());
                    }
                }
            }
            // POWERUPS
            JobInstance jobInstance = getSelectedJob().getJobInstance();
            if (activeRightButton == 2) {
                if (isBetween(mouseX, mouseY, 169, 132, 169 + 138, 132 + 18)) {
                    playClientGUIClick();
                    openPowerupsScreenForJobInstance(jobInstance);
                }
            }
            if (activeRightButton == 3) {
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < jobInstance.getActions().size(); ++i) {
                    int j = i - this.startIndexRight;
                    int i1 = 15 + j * 35;

                    //BUTTONS
                    if (isBetween(mouseX, mouseY, 158, i1, 158 + 144, i1 + 35)) {
                        playClientGUIClick();
                        IAction action = jobInstance.getActions().get(i);
                        openActionScreen(jobInstance, action);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, clickType);
    }

    public void openPowerupsScreenForJobInstance(JobInstance jobInstance) {
        ImmutableCollection<PowerupInstance> rootPowerups = PowerupManager.getInstance().getRootPowerups().values();
        ResourceLocation jobLocation = jobInstance.getLocation();
        jobs.stream().filter(j -> j.getJobInstance() == jobInstance).findFirst()
                .ifPresent(job -> Minecraft.getInstance().setScreen(new PowerUpsScreen(this, job,
                        rootPowerups.stream()
                                .filter(
                                        p -> p.getJobLocation().equals(jobLocation))
                                .toList(),
                        job.getPowerupManager().getAllPowerups(),
                        getCoins())));
    }

    private void openActionScreen(JobInstance jobInstance, IAction action) {
        Minecraft.getInstance().setScreen(new ActionScreen(jobInstance.getActions(), action));
    }

    private void setJob() {
        job = activeLeftButton == 0 ?
                jobs.get(selectedButton) :
                activeLeftButton == 1 ?
                        getEnabledJobs().get(selectedButton) :
                        getDisabledJobs().get(selectedButton);
    }

    private boolean hasJobSelected() {
        return job != null;
    }

    @Nullable
    private ItemRestriction getHoveredItemRestriction(double mouseX, double mouseY) {
        if (!hasJobSelected()) return null;

        mouseX -= 150D;

        for (int i = this.startIndexRight; i < jobItemRestrictions.size(); i++) {
            int j = i - this.startIndexRight;

            int amountX = 3;
            int amountY = 3;

            int startX = 8;
            int startY = 15;
            int buttonWidth = 48;
            int buttonHeight = 20;

            if (isBetween(mouseX, mouseY,
                    startX + (j % amountX) * buttonWidth, startY + (j / amountY) * buttonHeight,
                    startX + buttonWidth + (j % amountX) * buttonWidth - 1, startY + buttonHeight + (j / amountY) * buttonHeight - 1)) {
                return jobItemRestrictions.keySet().stream().toList().get(i);
            }
        }
        return null;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickType, double speedX, double speedY) {
        if (isBetween(mouseX, mouseY, 0, 0, width / 2, height)) {
            if (this.scrolling && this.isScrollBarActive()) {
                int i = startY + 14;
                int j = i + 140;
                this.scrollOffset = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                this.scrollOffset = Mth.clamp(this.scrollOffset, 0.0F, 1.0F);
                this.startIndex = (int) ((double) (this.scrollOffset * (float) this.getOffscreenRows()) + 0.5D);
                return true;
            } else {
                return super.mouseDragged(mouseX, mouseY, clickType, speedX, speedY);
            }
        } else {
            if (this.scrollingRight && this.isScrollBarRightActive()) {
                int i = startY + 14;
                int j = i + 128;
                this.scrollOffsetRight = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                this.scrollOffsetRight = Mth.clamp(this.scrollOffsetRight, 0.0F, 1.0F);
                int count = getActiveRightButton() == 1 ? 3 : 1;
                this.startIndexRight = (int) ((double) (this.scrollOffsetRight * (float) this.getOffscreenRowsRight()) + 0.5D) * count;
                return true;
            } else {
                return super.mouseDragged(mouseX, mouseY, clickType, speedX, speedY);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double p_99316_) {
        if (isBetween(mouseX, mouseY, 0, 0, width / 2 - 14, height) || (getActiveRightButton() != 1 && getActiveRightButton() != 2 && getActiveRightButton() != 3)) {
            if (this.isScrollBarActive()) {
                int i = this.getOffscreenRows();
                float f = (float) p_99316_ / (float) i;
                this.scrollOffset = Mth.clamp(this.scrollOffset - f, 0.0F, 1.0F);
                this.startIndex = (int) ((double) (this.scrollOffset * (float) i) + 0.5D);
            }
        } else {
            if (this.isScrollBarRightActive()) {
                int i = this.getOffscreenRowsRight();
                float f = (float) p_99316_ / (float) i;
                this.scrollOffsetRight = Mth.clamp(this.scrollOffsetRight - f, 0.0F, 1.0F);
                int count = getActiveRightButton() == 1 ? 3 : 1;
                this.startIndexRight = (int) ((double) (this.scrollOffsetRight * (float) i) + 0.5D) * count;
            }
        }
        return true;
    }

    protected int getOffscreenRows() {
        return getActiveLeftButton() == 0 ? this.jobs.size() - 4 :
                getActiveLeftButton() == 1 ? getEnabledJobs().size() - 4 :
                        getDisabledJobs().size() - 4;
    }

    private boolean isScrollBarActive() {
        return getActiveLeftButton() == 0 ? this.jobs.size() > 4 :
                getActiveLeftButton() == 1 ? getEnabledJobs().size() > 4 :
                        getDisabledJobs().size() > 4;
    }

    protected int getOffscreenRowsRight() {
        if (getSelectedJob() == null) return 0;
        return getActiveRightButton() == 1 ? (jobItemRestrictions.size() + 3 - 1) / 3 - 7 :
                getActiveRightButton() == 3 ? getSelectedJob().getJobInstance().getActions().size() - 4 : 0;
    }

    private boolean isScrollBarRightActive() {
        if (getSelectedJob() == null) return false;
        return getActiveRightButton() == 1 ? this.jobItemRestrictions.size() > 21 :
                getActiveRightButton() == 3 && getSelectedJob().getJobInstance().getActions().size() > 4;
    }

    public void blitThis(GuiGraphics guiGraphics, int posX, int posY, int startX, int startY, int stopX, int stopY) {
        guiGraphics.blit(BACKGROUND, this.startX + posX, this.startY + posY, startX, startY, stopX, stopY, 362, 362);
    }

    public boolean isBetween(double mouseX, double mouseY, int mouseXTop, int mouseYTop, int mouseXBottom, int mouseYBottom) {
        return mouseX >= mouseXTop && mouseY >= mouseYTop && mouseX <= mouseXBottom && mouseY <= mouseYBottom;
    }

    private @Nullable Job getSelectedJob() {
        return job;
    }

    private int getSelectedJobLevel() {
        return getSelectedJob().getLevel();
    }

    private int getSelectedJobExperience() {
        return getSelectedJob().getExperience();
    }

    private int getJobExperience(Job job) {
        return job.getExperience();
    }

    private int getSelectedJobMaxEXP() {
        return ExperienceHandler.getMaxExperienceForLevel(getSelectedJobLevel());
    }

    public void openConfirmScreen(ConfirmationMessageType messageType, JobInstance job) {
        Minecraft.getInstance().setScreen(new ConfirmationScreen(this, messageType, job));
    }

    private void drawNoJobSelected(GuiGraphics guiGraphics, String string) {
        for (int i = 1; i < 6; i++) {
            drawCenteredString(guiGraphics, ChatColor.darkGray() + JobsPlus.translatable("gui.no_job_selected." + string + "." + i).getString(), startX + (imageWidth + 150) / 2, startY + 42 + (i * 9), 16777215);
        }
    }

    private void drawJobInfo(GuiGraphics guiGraphics) {
        float scale = 1.0F;
        while (getTextHeightForDescription(guiGraphics, scale) > 94) {
            scale -= 0.025F;
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        String description = JobsPlus.translatable("job." + job.getJobInstance().getLocation().toString().replace(":", ".") + ".description").getString();
        String[] descriptionSplit = description.split(" ");
        int lineCount = 0;
        int loopCount = 0;
        while (descriptionSplit.length > 0 && loopCount < 100) {
            loopCount++;
            //noinspection DuplicatedCode
            StringBuilder line = new StringBuilder();
            int loopCount2 = 0;
            while (font.width(line + descriptionSplit[0]) < (150 / scale) && loopCount2 < 100) {
                loopCount2++;
                line.append(descriptionSplit[0]).append(" ");
                descriptionSplit = Arrays.copyOfRange(descriptionSplit, 1, descriptionSplit.length);
                if (descriptionSplit.length == 0) break;
            }
            guiGraphics.drawString(font, line.toString(), (int) ((startX + 163) / scale), (int) ((startY + 36 + (lineCount * (9 * scale))) / scale), 5592405, false);
            lineCount++;
        }
        guiGraphics.pose().popPose();
    }

    private float getTextHeightForDescription(GuiGraphics guiGraphics, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        String description = JobsPlus.translatable("job." + job.getJobInstance().getLocation().toString().split(":")[1] + ".description").getString();
        String[] descriptionSplit = description.split(" ");
        int lineCount = 0;
        int loopCount = 0;
        while (descriptionSplit.length > 0 && loopCount < 100) {
            loopCount++;
            //noinspection DuplicatedCode
            StringBuilder line = new StringBuilder();
            int loopCount2 = 0;
            while (font.width(line + descriptionSplit[0]) < (150 / scale) && loopCount2 < 100) {
                loopCount2++;
                line.append(descriptionSplit[0]).append(" ");
                descriptionSplit = Arrays.copyOfRange(descriptionSplit, 1, descriptionSplit.length);
                if (descriptionSplit.length == 0) break;
            }
            lineCount++;
        }
        guiGraphics.pose().popPose();
        return lineCount * (9 * scale);
    }

    public void drawnBigJobTitle(GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(2F, 2F, 2F);
        guiGraphics.drawString(font, job.getJobInstance().getName().withStyle(Style.EMPTY.withBold(true)), (int) ((startX + 156) / 2F), (int) ((startY + 5) / 2F), job.getJobInstance().getColorDecimal(), false);
        guiGraphics.pose().popPose();
        if (getSelectedJobLevel() != 0) {
            guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.level", ChatColor.reset(), getSelectedJobLevel()).getString(), startX + 156, startY + 22, 16777215, false);
            if (getSelectedJobLevel() != 100)
                guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.exp", ChatColor.reset(), "[" + getSelectedJobExperience() + "/" + getSelectedJobMaxEXP() + "]").getString(), startX + 216, startY + 22, 16777215, false);
        } else {
            guiGraphics.drawString(font, ChatColor.darkGray() + JobsPlus.translatable("gui.want_this_job").getString(), startX + 156, startY + 22, 16777215, false);
            drawRightAlignedString(guiGraphics, ChatColor.darkGray() + JobsPlus.translatable("gui.price", ChatColor.reset(), hasFreeClaimableJobs() ? 0 : getSelectedJob().getJobInstance().getPrice()).getString(), startX + imageWidth - 10, startY + 22, 16777215);
        }
        guiGraphics.drawString(font, ChatFormatting.STRIKETHROUGH + "                                        ", startX + 156, startY + 28, 16777215, false);
    }

    @Override
    protected void init() {
        startX = (this.width - imageWidth) / 2;
        startY = (this.height - imageHeight) / 2;
        super.init();
    }

    public int getActiveLeftButton() {
        return activeLeftButton;
    }

    public int getActiveRightButton() {
        return activeRightButton;
    }

    public int getSelectedButton() {
        return selectedButton;
    }

    public float getScrollOffset() {
        return scrollOffset;
    }

    public float getScrollOffsetRight() {
        return scrollOffsetRight;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getStartIndexRight() {
        return startIndexRight;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public int getCoins() {
        return this.jobsPlayerData.coins();
    }

    private int getActiveJobsAmount() {
        return this.jobsPlayerData.activeJobs().size();
    }

    private int getAmountOfFreeJobs() {
        return JobsPlusConfig.amountOfFreeJobs.get();
    }

    private boolean hasFreeClaimableJobs() {
        return getActiveJobsAmount() < getAmountOfFreeJobs();
    }

    private int getJobStartPrice(JobInstance jobInstance) {
        return jobInstance.getPrice();
    }

    private ArrayList<Job> getEnabledJobs() {
        ArrayList<Job> enabledJobs = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getLevel() > 0) {
                enabledJobs.add(job);
            }
        }
        return enabledJobs;
    }

    private ArrayList<Job> getDisabledJobs() {
        ArrayList<Job> disabledJobs = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getLevel() == 0) {
                disabledJobs.add(job);
            }
        }
        return disabledJobs;
    }
}
