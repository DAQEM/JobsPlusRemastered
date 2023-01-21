package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.render.RenderColor;
import com.daqem.jobsplus.player.JobsPlayerData;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.daqem.jobsplus.util.chat.ChatColor;
import com.daqem.jobsplus.util.experience.ExperienceHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class JobsScreen extends Screen {

    public static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/jobs_screen.png");
    private final int imageWidth = 326;
    private final int imageHeight = 166;
    private final JobsPlayerData jobsPlayerData;
    private final LinkedList<Job> jobs = new LinkedList<>();
    private final LinkedList<ItemStack> selectedJobCraftableStacks = new LinkedList<>();
    private final LinkedList<Job> shownJobs = new LinkedList<>();
    private int activeRightButton;
    private int activeLeftButton;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private int startX;
    private int startY;
    private Job job;
    private int selectedButton;

    private float scrollOffsRight;
    private boolean scrollingCrafting;
    private int startIndexRight;

    public JobsScreen(CompoundTag dataTag) {
        super(JobsPlus.literal("Jobs"));
        this.job = null;
        this.activeLeftButton = dataTag.getInt("ActiveLeftButton");
        this.activeRightButton = dataTag.getInt("ActiveRightButton");
        this.selectedButton = dataTag.getInt("SelectedButton");
        this.scrollOffs = dataTag.getFloat("ScrollOffset");
        this.startIndex = dataTag.getInt("StartIndex");
        this.jobsPlayerData = JobsPlayerData.fromNBT(dataTag);
        this.jobs.addAll(jobsPlayerData.activeJobs());
        this.jobs.addAll(jobsPlayerData.inactiveJobs());
        this.jobs.sort(Comparator.comparing(o -> o.getJobInstance().getName()));
    }

    public static void drawRightAlignedString(@NotNull PoseStack poseStack, Font font, @NotNull String text, int posX, int posY, int color) {
        font.draw(poseStack, text, (float) posX - font.width(text), (float) posY, color);
    }

    public static void drawCenteredString(@NotNull PoseStack poseStack, Font font, @NotNull String text, int posX, int posY, int color) {
        font.draw(poseStack, text, (float) (posX - font.width(text) / 2), (float) posY, color);
    }

    public static void playClientGUIClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderColor.normal();
        RenderSystem.setShaderTexture(0, BACKGROUND);

        this.startX = (this.width - this.imageWidth) / 2;
        this.startY = (this.height - this.imageHeight) / 2;

        int firstHiddenIndex = this.startIndex + 4;
        int firstHiddenIndexRight = getActiveRightButton() == 1 ? this.startIndexRight + 49 : this.startIndexRight + 4;

        this.setShownJobs(firstHiddenIndex);

        this.renderBackgroundImage(poseStack);
        this.renderScrollWheel(poseStack);
        this.renderButtons(poseStack, mouseX, mouseY, firstHiddenIndex, firstHiddenIndexRight);
        this.renderTooltip(poseStack, mouseX, mouseY);
        this.renderItems(startX, startY, firstHiddenIndex, firstHiddenIndexRight);
        this.renderTexts(poseStack, firstHiddenIndex, firstHiddenIndexRight);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        selectedJobCraftableStacks.clear();
        addPotionsToCraftableStacks();
        addCraftableStacks();
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

    private void addCraftableStacks() {
//        if (Minecraft.getInstance().level == null) return;
//        ArrayList<ConstructionCraftingRecipe> recipes = new ArrayList<>(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ConstructionRecipeType.INSTANCE));
//        recipes.sort(Comparator.comparing(ConstructionCraftingRecipe::getRequiredLevel));
//        recipes.forEach(constructionRecipe -> {
//            if (constructionRecipe.getJob().is(getSelectedJob()))
//                selectedJobCraftableStacks.add(constructionRecipe.getResultItem());
//        });
    }

    private void addPotionsToCraftableStacks() {
//        if (!hasJobSelected() || getSelectedJob() == null || !getSelectedJob().is(Jobs.ALCHEMIST)) return;
//
//        LinkedHashMap<MobEffect, Integer> potionMap = new LinkedHashMap<>();
//        potionMap.put(MobEffects.MOVEMENT_SPEED, 4);
//        potionMap.put(MobEffects.DIG_SPEED, 4);
//        potionMap.put(MobEffects.DAMAGE_BOOST, 4);
//        potionMap.put(MobEffects.REGENERATION, 3);
//        potionMap.put(MobEffects.LUCK, 4);
//        potionMap.put(ModEffects.JESUS.get(), 2);
//        potionMap.put(ModEffects.FLYING.get(), 2);
//        for (Map.Entry<MobEffect, Integer> entry : potionMap.entrySet()) {
//            for (int i = 0; i < entry.getValue(); i++) {
//                selectedJobCraftableStacks.add(PotionUtils.setCustomEffects(Items.POTION.getDefaultInstance(), Sets.newHashSet(new MobEffectInstance(entry.getKey()))));
//            }
//        }
    }

    private void renderBackgroundImage(PoseStack poseStack) {
        blitThis(poseStack, 0, 0, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void renderScrollWheel(PoseStack poseStack) {
        if (!isScrollBarActive()) RenderColor.grayedOut();
        blitThis(poseStack, 127, (int) (17 + (123.0F * getScrollOffs())), 0, 207, 12, 15);
        RenderColor.normal();
    }

    protected void renderTooltip(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        mouseX -= startX;
        mouseY -= startY;
        if (isBetween(mouseX, mouseY, 6, -22, 32, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.all_jobs"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28, -22, 32 + 28, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.performing_jobs"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 28, -22, 32 + 28 + 28, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.not_performing_jobs"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 150, -22, 32 + 150, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.job_info"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 150, -22, 32 + 28 + 150, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.job_crafting_recipes"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 150, -22, 32 + 28 + 28 + 150, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.job_powerups"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 28 + 150, -22, 32 + 28 + 28 + 28 + 150, 0)) {
            super.renderTooltip(poseStack, JobsPlus.translatable("gui.job_how_to_get_exp"), mouseX + startX, mouseY + startY);
        } else if (isBetween(mouseX, mouseY, imageWidth, 6, imageWidth + 21, 31)) {
            if (hasJobSelected() && isJobDisplayEnabled()) {
                if (getSelectedJobLevel() != 0) {
                    List<Component> components;
                    if (getDisplay().isPresent()) {
                        JobInstance jobInstance = getDisplay().get().getJobInstance();

                        Component colorizedJobName = JobsPlus.literal(jobInstance.getName().toUpperCase())
                                .withStyle(Style.EMPTY
                                        .withColor(jobInstance.getColorDecimal())
                                        .withBold(true));

                        components = List.of(JobsPlus.translatable("gui.toggle_prefix"),
                                JobsPlus.translatable("gui.active").append(colorizedJobName));
                    } else {
                        components = List.of(JobsPlus.translatable("gui.toggle_prefix"),
                                JobsPlus.translatable("gui.active", ChatColor.boldBlue()
                                        + JobsPlus.translatable("job.none").getString()));
                    }
                    super.renderTooltip(poseStack, components, Optional.empty(), mouseX + startX, mouseY + startY + 17);
                }
            }
        } else if (isBetween(mouseX, mouseY, imageWidth, 35, imageWidth + 21, 60)) {
            if (hasJobSelected()) {
                if (getSelectedJobLevel() != 0) {
                    List<Component> list = new ArrayList<>();
                    final Job activeBossBarJob = getActiveBossBar();

//                    if (activeBossBarJob != -1 && Jobs.getJobFromInt(activeBossBarJob) != null) {
                    if (activeBossBarJob != null) {
//                        list = List.of(JobsPlus.translatable("gui.toggle_boss_bar"),
//                                JobsPlus.translatable("gui.active", ChatHandler.ColorizedJobName(Objects.requireNonNull(Jobs.getJobFromInt(activeBossBarJob))).replace(" ", "")));
                    } else {
                        list = List.of(JobsPlus.translatable("gui.toggle_boss_bar"),
                                JobsPlus.translatable("gui.active", ChatColor.boldBlue() + JobsPlus.translatable("job.none").getString()));
                    }
                    super.renderTooltip(poseStack, list, Optional.empty(), mouseX + startX, mouseY + startY + 17);
                }
            }
        }
        if (activeRightButton == 1) {
            ItemStack hoveredItemStack = getHoveredItemStack(mouseX, mouseY);
            if (hoveredItemStack != ItemStack.EMPTY) {
                super.renderTooltip(poseStack, hoveredItemStack.copy(), mouseX + startX, mouseY + startY);
            }
        }
    }

    private void drawTopButtons(PoseStack poseStack, int mouseX, int mouseY, int buttonAmount, int posXOffset, int startYOffset, int activeButton) {
        for (int i = 0; i < buttonAmount; ++i) {
            int posX = posXOffset + (28 * i);
            if (activeButton != i && isBetween(mouseX - startX, mouseY - startY, posX, -19, posX + 25, 0)) {
                RenderSystem.setShaderColor(0.7F, 0.7F, 1F, 1);
                drawNotSelectedTopButton(poseStack, activeButton, i, posX);
                RenderColor.normal();
            } else {
                drawNotSelectedTopButton(poseStack, activeButton, i, posX);
            }
            blitThis(poseStack, posX + 3, -21, 0, imageHeight + 56 + startYOffset + (20 * i), 20, 20);
        }
    }

    private void drawNotSelectedTopButton(PoseStack poseStack, int activeButton, int i, int posX) {
        blitThis(poseStack, posX, -22, 142, activeButton == i ? imageHeight + 22 : imageHeight, 26, 22);
    }

    private void drawButtons(PoseStack poseStack, int mouseX, int mouseY, int i, Job job) {
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
        blitThis(poseStack, 7, i1, 26, this.imageHeight, 116, 35);
        RenderColor.normal();

        //BADGES
        int level = job.getLevel();
        int maxLevel = job.getJobInstance().getMaxLevel();
        if (level >= maxLevel * 0.75) blitThis(poseStack, 7 + 5, i1 + 5, 168 + (27 * 3), imageHeight, 27, 27);
        else if (level >= maxLevel * 0.50) blitThis(poseStack, 7 + 5, i1 + 5, 168 + (27 * 2), imageHeight, 27, 27);
        else if (level >= maxLevel * 0.25) blitThis(poseStack, 7 + 5, i1 + 5, 168 + 27, imageHeight, 27, 27);
        else blitThis(poseStack, 7 + 5, i1 + 5, 168, imageHeight, 27, 27);
    }

    public void renderButtons(PoseStack poseStack, int mouseX, int mouseY, int firstHiddenIndex, int firstHiddenIndexRight) {
        //SETTINGS
        if (isBetween(mouseX, mouseY, 3, height - 20, 18, height - 4))
            RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1);
        blit(poseStack, 3, height - 20, 276, 166, 16, 16, 362, 362);
        RenderColor.normal();

        //DISCORD
        if (isBetween(mouseX, mouseY, width - 19, height - 20, width - 4, height - 5))
            RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1);
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        blit(poseStack, width * 2 - 38, height * 2 - 40, 276 + 16, 166, 32, 32, 362, 362);
        RenderColor.normal();
        poseStack.popPose();

        // LEFT BUTTONS
        drawTopButtons(poseStack, mouseX, mouseY, 3, 6, 0, activeLeftButton);
        //RIGHT BUTTONS
        drawTopButtons(poseStack, mouseX, mouseY, 4, 156, 60, activeRightButton);
        //JOB BUTTONS
        drawJobButtons(poseStack, mouseX, mouseY, firstHiddenIndex);
        // JOB START AND STOP BUTTONS
        if (hasJobSelected() && (getActiveRightButton() == 1 || getActiveRightButton() == 2)) {
            //BROWN BACKGROUND
            blitThis(poseStack, 171, 17, 189, 198, 128, 128);

            //SCROLLBAR
            blitThis(poseStack, 302, 17, 317, 198, 9, 128);

            //SCROLL WHEEL
            blitThis(poseStack, 303, (int) (18 + (111.0F * getScrollOffsRight())), 160, 210, 7, 15);
        }

        if (activeRightButton == 0) {
            if (hasJobSelected()) {
                if (isBetween(mouseX - startX, mouseY - startY, 169, 132, 169 + 138, 132 + 17)) {
                    RenderSystem.setShaderColor(0.7F, 0.7F, 1F, 1);
                } else {
                    RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 1);
                }
                blitThis(poseStack, 150 + 19, 132, 26, imageHeight + 105, 139, 18);
                RenderColor.normal();

            }
            // CRAFTING RECIPE BUTTONS
        } else if (activeRightButton == 1) {
            if (hasJobSelected()) {
                //BUTTONS
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < selectedJobCraftableStacks.size(); ++i) {
                    int j = i - this.startIndexRight;
                    int k = 172 + j % 7 * 18;
                    int l = j / 7;
                    int i1 = 18 + l * 18;

                    if (isBetween(mouseX - startX, mouseY - startY, k, i1, k + 17, i1 + 17)) {
                        RenderColor.buttonHover();
                    }
                    blitThis(poseStack, k, i1, 142, 210, 18, 18);
                    RenderColor.normal();
                }

                //CONSTRUCTION TABLE
                blitThis(poseStack, 158, imageHeight, 150, 136, 100, 30);
                blitThis(poseStack, 218, imageHeight, 226, 136, 100, 30);
            }
            //POWERUP BUTTONS
        } else if (activeRightButton == 2) {
            if (hasJobSelected()) {
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < getSelectedJob().getJobInstance().getPowerups().size(); ++i) {
                    int j = i - this.startIndexRight;
                    int i1 = 17 + j * 32;

                    //BUTTONS
                    if (isBetween(mouseX, mouseY, this.startX + 171, i1 + startY, this.startX + 298, i1 + startY + 31)) {
                        RenderColor.normalSelected();
                    }
                    blitThis(poseStack, 171, i1, 26, this.imageHeight, 116, 31);
                    blitThis(poseStack, 184, i1, 27, this.imageHeight, 115, 31);
                    blitThis(poseStack, 171, i1 + 1, 26, this.imageHeight + 4, 116, 31);
                    blitThis(poseStack, 184, i1 + 1, 27, this.imageHeight + 4, 115, 31);
                    RenderColor.normal();
                }
            }
        }
        if (hasJobSelected()) {
            if (getSelectedJobLevel() != 0) {
                //Job Display Background
                if (isJobDisplayEnabled()) {
                    if (getDisplay().isPresent() && getDisplay().get() == job)
                        blitThis(poseStack, imageWidth, 6, 142, 234, 22, 26);
                    else blitThis(poseStack, imageWidth, 6, 164, 234, 19, 26);
                }
                //Boss Bar Background
                if (getActiveBossBar() == job) blitThis(poseStack, imageWidth, 9 + 26, 142, 234, 22, 26);
                else blitThis(poseStack, imageWidth, 9 + 26, 164, 234, 19, 26);
                // Boss Bar Icon
                blitThis(poseStack, imageWidth + 1, 9 + 32, 165, 271, 15, 14);
            }
        }
    }

    private void drawJobButtons(PoseStack poseStack, int mouseX, int mouseY, int firstHiddenIndex) {
        if (activeLeftButton == 0) {
            for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
                Job job = jobs.get(i);
                drawButtons(poseStack, mouseX, mouseY, i, job);
            }
        } else if (activeLeftButton == 1 || activeLeftButton == 2) {
            for (int i = this.startIndex; i < firstHiddenIndex; ++i) {
                if ((activeLeftButton == 1 && getEnabledJobs().size() > i) || (activeLeftButton == 2 && getDisabledJobs().size() > i)) {
                    Job job = activeLeftButton == 1 ? getEnabledJobs().get(i) : getDisabledJobs().get(i);
                    drawButtons(poseStack, mouseX, mouseY, i, job);
                }
            }
        }
    }

    private void drawButtonTexts(PoseStack poseStack, Job job, int i, boolean offset) {
        int j = offset ? i - this.startIndex : i;
        int i1 = (this.startY + 16) + j * 35;
        int level = job.getLevel();
        int exp = getJobExperience(job);
        if (level != 0) {
            int maxExp = ExperienceHandler.getMaxExperienceForLevel(level);
            font.draw(poseStack, ChatColor.boldGreen() + job.getJobInstance().getName(), startX + 7 + 3 + 35, i1 + 3, 16777215);
            font.draw(poseStack, ChatColor.aqua() + JobsPlus.translatable("gui.level", ChatColor.reset(), level).getString(), startX + 7 + 3 + 35, i1 + 14, 16777215);
            if (level != 100)
                font.draw(poseStack, ChatColor.aqua() + JobsPlus.translatable("gui.exp", ChatColor.reset(), (int) ((double) exp / maxExp * 100), "%").getString(), startX + 7 + 3 + 35, i1 + 23, 16777215);
        } else {
            font.draw(poseStack, ChatColor.boldRed() + job.getJobInstance().getName(), startX + 7 + 3 + 35, i1 + 3, 16777215);
            font.draw(poseStack, ChatColor.aqua() + JobsPlus.translatable("gui.want_this_job").getString(), startX + 7 + 3 + 35, i1 + 14, 16777215);
            if (hasFreeClaimableJobs()) {
                font.draw(poseStack, ChatColor.aqua() + JobsPlus.translatable("gui.cost", ChatColor.reset(), 0).getString(), startX + 7 + 3 + 35, i1 + 23, 16777215);
            } else {
                font.draw(poseStack, ChatColor.aqua() + JobsPlus.translatable("gui.cost", ChatColor.reset(), 10).getString(), startX + 7 + 3 + 35, i1 + 23, 16777215);
            }
        }
    }

    private void drawJobButtonsTexts(PoseStack poseStack, int firstHiddenIndex) {
        if (activeLeftButton == 0) {
            for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
                drawButtonTexts(poseStack, jobs.get(i), i, true);
            }
        } else if (activeLeftButton == 1 || activeLeftButton == 2) {
            int count = 0;
            for (int i = this.startIndex; i < firstHiddenIndex; ++i) {
                if ((activeLeftButton == 1 && getEnabledJobs().size() > i) || (activeLeftButton == 2 && getDisabledJobs().size() > i)) {
                    Job job = activeLeftButton == 1 ? getEnabledJobs().get(i) : getDisabledJobs().get(i);
                    drawButtonTexts(poseStack, job, count++, false);
                }
            }
        }
    }

    public void renderTexts(PoseStack poseStack, int firstHiddenIndex, int firstHiddenIndexRight) {
        font.draw(poseStack, ChatColor.darkGray() + JobsPlus.translatable("gui.jobs").getString(), startX + 7, startY + 6, 16777215);
        drawRightAlignedString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.coins.top", getCoins()).getString(), startX + 140, startY + 6, 16777215);

        drawJobButtonsTexts(poseStack, firstHiddenIndex);

        if (!hasJobSelected()) {
            if (activeRightButton == 0) drawNoJobSelected(poseStack, "info");
            else if (activeRightButton == 1) drawNoJobSelected(poseStack, "crafting");
            else if (activeRightButton == 2) drawNoJobSelected(poseStack, "powerups");
            else if (activeRightButton == 3) drawNoJobSelected(poseStack, "how_to_get_exp");
        } else {
            int centerR = startX + (imageWidth + 150) / 2;
            if (activeRightButton == 0) {
                drawnBigJobTitle(poseStack);
                drawJobInfo(poseStack);
                if (getSelectedJobLevel() == 0) {
                    drawCenteredString(poseStack, font, ChatColor.white() + JobsPlus.translatable("gui.job.start").getString(), centerR, startY + 137, 16777215);
                } else {
                    drawCenteredString(poseStack, font, ChatColor.white() + JobsPlus.translatable("gui.job.stop").getString(), centerR, startY + 137, 16777215);
                }
            } else if (activeRightButton == 1) {
                drawCenteredString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.crafting").getString(), centerR, startY + 6, 16777215);
                //CONSTRUCTION TABLE
                font.draw(poseStack, ChatColor.darkGray() + "Craft the items using", startX + 189, startY + 171, 16777215);
                font.draw(poseStack, ChatColor.darkGray() + "the Construction Table.", startX + 189, startY + 181, 16777215);
            } else if (activeRightButton == 2) {
                drawCenteredString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups.powerups").getString(), centerR, startY + 6, 16777215);
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < getSelectedJob().getJobInstance().getPowerups().size(); ++i) {
                    int j = i - this.startIndexRight;
                    int i1 = 17 + j * 32;

                    PowerupInstance powerupInstance = getSelectedJob().getJobInstance().getPowerups().get(i);
                    font.draw(poseStack, powerupInstance.getName(), this.startX + 176, i1 + startY + 5, 16777215);
                    if (getSelectedJob().hasPowerup(powerupInstance)) {
                        PowerupState state = getSelectedJob().getPowerups().get(powerupInstance.getLocation());
                        font.draw(poseStack, "State: " + (state == PowerupState.ACTIVE ? ChatColor.green() + state.getState() : ChatColor.red() + state.getState()), this.startX + 176, i1 + startY + 16, 16777215);
                    } else {
                        font.draw(poseStack, "Cost: " + powerupInstance.getCost(), this.startX + 176, i1 + startY + 16, 16777215);
                    }
                }
//                drawCenteredStringNew(poseStack, font, ChatColor.gray() + JobsPlus.translatable("gui.powerups.cost", getPowerupCost()).getString(), centerR, startY + 16, 16777215);
//                drawCenteredStringNew(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups.superpowers").getString(), centerR, startY + 110, 16777215);
//                drawCenteredStringNew(poseStack, font, ChatColor.gray() + JobsPlus.translatable("gui.powerups.superpowers.cost").getString(), centerR, startY + 120, 16777215);
                poseStack.pushPose();
                poseStack.scale(0.72F, 0.72F, 0.72F);
//                drawCenteredPowerupsString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups." + Jobs.getEnglishString(jobId).toLowerCase() + ".1").getString(), (int) ((startX + (imageWidth + 152) / 2) / 0.72F), (int) ((startY + 33) / 0.72F), 16777215);
//                drawCenteredPowerupsString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups." + Jobs.getEnglishString(jobId).toLowerCase() + ".2").getString(), (int) ((startX + (imageWidth + 152) / 2) / 0.72F), (int) ((startY + 58) / 0.72F), 16777215);
//                drawCenteredPowerupsString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups." + Jobs.getEnglishString(jobId).toLowerCase() + ".3").getString(), (int) ((startX + (imageWidth + 152) / 2) / 0.72F), (int) ((startY + 83) / 0.72F), 16777215);
//                drawCenteredPowerupsString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.powerups." + Jobs.getEnglishString(jobId).toLowerCase() + ".superpower").getString(), (int) ((startX + (imageWidth + 152) / 2) / 0.72F), (int) ((startY + 137) / 0.72F), 16777215);
                poseStack.popPose();
            } else if (activeRightButton == 3) {
                drawnBigJobTitle(poseStack);
                drawCenteredString(poseStack, font, ChatFormatting.STRIKETHROUGH + "             ", centerR, startY + 47, 16777215);
                for (int i = 1; i < 10; ++i) {
                    drawCenteredString(poseStack, font, getTCText(i), centerR, startY + 50 + (i * 10), getTCTextColor(i));
                }
                poseStack.pushPose();
                poseStack.scale(1.2F, 1.2F, 1.2F);
                drawCenteredString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.exp.title").getString(), (int) ((centerR) / 1.2), (int) ((startY + 37) / 1.2), 16777215);
                poseStack.popPose();
            }
        }
    }

    public void renderItems(int x, int y, int firstHiddenIndex, int firstHiddenIndexRight) {
        if (minecraft == null) return;
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        renderJobIcons(firstHiddenIndex);

        if (hasJobSelected()) {
            if (activeRightButton == 1) {
                for (int i = this.startIndexRight; i < firstHiddenIndexRight && i < selectedJobCraftableStacks.size(); i++) {
                    int j = i - this.startIndexRight;
                    int xOffset = x + 173 + j % 7 * 18;
                    int l = j / 7;
                    int yOffset = y + 19 + l * 18;
                    itemRenderer.renderAndDecorateItem(selectedJobCraftableStacks.get(i), xOffset, yOffset);
                }
                //CONSTRUCTION TABLE
//                itemRenderer.renderAndDecorateItem(ModItems.CONSTRUCTION_TABLE.get().getDefaultInstance(), startX + 166, startY + 171);
            }
            if (getSelectedJobLevel() != 0 && isJobDisplayEnabled()) {
                if (getDisplay().isPresent() && getDisplay().get() == job)
                    itemRenderer.renderAndDecorateItem(Items.NAME_TAG.getDefaultInstance(), startX + 328, startY + 11);
                else
                    itemRenderer.renderAndDecorateItem(Items.NAME_TAG.getDefaultInstance(), startX + 327, startY + 11);
            }
        }
    }

    private void renderJobIcons(int firstHiddenIndex) {
        for (int i = this.startIndex; i < firstHiddenIndex && i < jobs.size(); ++i) {
            int originalSpot = i - startIndex;
            if (shownJobs.size() > originalSpot) {
                itemRenderer.renderAndDecorateItem(
                        shownJobs.get(originalSpot).getJobInstance().getIconItem().getDefaultInstance(),
                        startX + 17, startY + 25 + originalSpot * 35);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int clickType) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        this.scrolling = false;
        this.scrollingCrafting = false;

        //SETTINGS
        if (isBetween(mouseX, mouseY, 3, height - 20, 19, height - 4)) {
//            ModPackets.INSTANCE.sendToServer(new PacketUserSettingsServer("MAIN"));
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
                    job = activeLeftButton == 0 ?
                            jobs.get(selectedButton) :
                            activeLeftButton == 1 ?
                                    getEnabledJobs().get(selectedButton) :
                                    getDisabledJobs().get(selectedButton);
                    scrollOffsRight = 0;
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
            this.scrollingCrafting = true;
        }

        //DISPLAY AND BOSSBAR
        if (hasJobSelected()) {
            if (getSelectedJobLevel() != 0) {
                //DISPLAY
                if (isJobDisplayEnabled()) {
                    if (isBetween(mouseX, mouseY, imageWidth, 6, imageWidth + 21, 31)) {
                        String job;
                        if (getDisplay().isPresent() && getDisplay().get() == this.job)
                            job = "NONE";
//                        else
//                            job = Jobs.getEnglishString(this.job);
//                        ModPackets.INSTANCE.sendToServer(new PacketJobDisplay(job));
//                        ModPackets.INSTANCE.sendToServer(new PacketOpenMenu(this.job, activeLeftButton, activeRightButton, selectedButton, scrollOffs, startIndex));
                    }
                }
                //BOSSBAR
                if (isBetween(mouseX, mouseY, imageWidth, 35, imageWidth + 21, 60)) {
//                    if (job == getActiveBossBar()) ModPackets.INSTANCE.sendToServer(new PacketBossBarr("NONE"));
//                    else ModPackets.INSTANCE.sendToServer(new PacketBossBarr(Jobs.getEnglishString(job)));
//                    ModPackets.INSTANCE.sendToServer(new PacketOpenMenu(job, activeLeftButton, activeRightButton, selectedButton, scrollOffs, startIndex));
                }
            }
        }
        //ALL JOBS BUTTON
        if (isBetween(mouseX, mouseY, 6, -22, 32, 0)) {
            playClientGUIClick();
            if (activeLeftButton != 0) {
                selectedButton = -1;
                job = null;
                startIndex = 0;
                scrollOffs = 0;
                scrollOffsRight = 0;
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
                scrollOffs = 0;
                scrollOffsRight = 0;
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
                scrollOffs = 0;
                scrollOffsRight = 0;
                startIndexRight = 0;
            }
            activeLeftButton = 2;
        }
        //JOB INFO BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 150, -22, 32 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 0;
            scrollOffsRight = 0;
            startIndexRight = 0;
        }
        //CRAFTING RECIPES BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 150, -22, 32 + 28 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 1;
            scrollOffsRight = 0;
            startIndexRight = 0;
        }
        // POWERUPS BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 150, -22, 32 + 28 + 28 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 2;
            scrollOffsRight = 0;
            startIndexRight = 0;
        }
        // HOW TO GET EXP BUTTON
        else if (isBetween(mouseX, mouseY, 6 + 28 + 28 + 28 + 150, -22, 32 + 28 + 28 + 28 + 150, 0)) {
            playClientGUIClick();
            activeRightButton = 3;
            scrollOffsRight = 0;
            startIndexRight = 0;
        }
        // RIGHT BUTTONS
        if (hasJobSelected()) {
            // JOB START STOP BUTTON
            if (activeRightButton == 0) {
                if (isBetween(mouseX, mouseY, 169, 132, 169 + 138, 132 + 18)) {
                    playClientGUIClick();
                    if (getSelectedJobLevel() == 0) {
                        if (hasFreeClaimableJobs()) {
                            openConfirmScreen(JobsPlus.translatable("confirm.start"), "start", 0);
                        } else {
                            if (getCoins() >= getJobStartCost())
                                openConfirmScreen(JobsPlus.translatable("confirm.start_paid", getJobStartCost()), "start_paid", 0);
                            else
                                openConfirmScreen(JobsPlus.translatable("confirm.not_enough_coins_start"), "not_enough_coins_start", 0);

                        }
                    } else if (getSelectedJobLevel() == getJobLevelToStopJobForFree()) {
                        openConfirmScreen(JobsPlus.translatable("confirm.stop_free"), "stop_free", 0);
                    } else {
                        if (getCoins() >= getJobStopCost())
                            openConfirmScreen(JobsPlus.translatable("confirm.stop", getJobStopCost()), "stop", 0);
                        else
                            openConfirmScreen(JobsPlus.translatable("confirm.not_enough_coins_stop"), "not_enough_coins_stop", 0);

                    }
                }
            }
            // CRAFTING RECIPES
            if (activeRightButton == 1) {
                ItemStack itemStack = getHoveredItemStack(mouseX, mouseY);
                if (itemStack != ItemStack.EMPTY) {
                    playClientGUIClick();
//                    JobsPlusJeiPlugin.showJEIPage(itemStack);
                }
            }
            // POWERUPS
            if (activeRightButton == 2) {
//                int clickedPowerupID = 0;
//                if (isBetween(mouseX, mouseY, 169, 27, 169 + 139, 27 + 18)) clickedPowerupID = 1;
//                if (isBetween(mouseX, mouseY, 169, 27 + 25, 169 + 139, 27 + 18 + 25)) clickedPowerupID = 2;
//                if (isBetween(mouseX, mouseY, 169, 27 + 50, 169 + 139, 27 + 18 + 50)) clickedPowerupID = 3;
//                if (clickedPowerupID != 0) {
//                    if (hasBoughtPowerup(clickedPowerupID)) {
////                        ModPackets.INSTANCE.sendToServer(new PacketMenuPowerUp(true, Jobs.getJobFromInt(job), clickedPowerupID + 1));
////                        ModPackets.INSTANCE.sendToServer(new PacketOpenMenu(job, activeLeftButton, activeRightButton, selectedButton, scrollOffs, startIndex));
//                    } else {
//                        //Check if player has enough coins to buy a power-up
//                        if (getCoins() >= getPowerupCost()) {
//                            //Check is the player has the job.
//                            if (getSelectedJobLevel() != 0)
//                                openConfirmScreen(JobsPlus.translatable("confirm.powerup", getPowerupCost()), "powerup", clickedPowerupID);
//                            else {
//                                openConfirmScreen(JobsPlus.translatable("confirm.job_not_enabled"), "job_not_enabled", clickedPowerupID);
//                            }
//                        } else {
//                            openConfirmScreen(JobsPlus.translatable("confirm.not_enough_coins_powerup"), "not_enough_coins_powerup", clickedPowerupID);
//                        }
//                    }
//                    playClientGUIClick();
//                }
//                if (isBetween(mouseX, mouseY, 169, 27 + 103, 169 + 139, 27 + 18 + 103)) {
//                    if (getSelectedJobLevel() == 100) {
////                        ModPackets.INSTANCE.sendToServer(new PacketSwitchSuperpower(Jobs.getJobFromInt(job)));
////                        ModPackets.INSTANCE.sendToServer(new PacketOpenMenu(job, activeLeftButton, activeRightButton, selectedButton, scrollOffs, startIndex));
//                    } else {
//                        openConfirmScreen(JobsPlus.translatable("error.level.must_be_100"), "must_be_level_100", clickedPowerupID);
//                    }
//                    playClientGUIClick();
//                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, clickType);
    }

    private boolean hasJobSelected() {
        return job != null;
    }

    private ItemStack getHoveredItemStack(double mouseX, double mouseY) {
        if (!hasJobSelected()) return ItemStack.EMPTY;

        mouseX -= 150D;

        for (int i = this.startIndexRight; i < selectedJobCraftableStacks.size(); i++) {
            int j = i - this.startIndexRight;
            if (isBetween(mouseX, mouseY,
                    22 + (j % 7) * 18, 18 + (j / 7) * 18,
                    22 + 17 + (j % 7) * 18, 18 + 17 + (j / 7) * 18)) {
                return selectedJobCraftableStacks.get(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickType, double speedX, double speedY) {
        if (isBetween(mouseX, mouseY, 0, 0, width / 2, height)) {
            if (this.scrolling && this.isScrollBarActive()) {
                int i = startY + 14;
                int j = i + 140;
                this.scrollOffs = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
                this.startIndex = (int) ((double) (this.scrollOffs * (float) this.getOffscreenRows()) + 0.5D);
                return true;
            } else {
                return super.mouseDragged(mouseX, mouseY, clickType, speedX, speedY);
            }
        } else {
            if (this.scrollingCrafting && this.isScrollBarRightActive()) {
                int i = startY + 14;
                int j = i + 128;
                this.scrollOffsRight = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                this.scrollOffsRight = Mth.clamp(this.scrollOffsRight, 0.0F, 1.0F);
                int count = getActiveRightButton() == 1 ? 7 : 1;
                this.startIndexRight = (int) ((double) (this.scrollOffsRight * (float) this.getOffscreenRowsRight()) + 0.5D) * count;
                return true;
            } else {
                return super.mouseDragged(mouseX, mouseY, clickType, speedX, speedY);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double p_99316_) {
        if (isBetween(mouseX, mouseY, 0, 0, width / 2 - 14, height) || getActiveRightButton() != 1 && getActiveRightButton() != 2) {
            if (this.isScrollBarActive()) {
                int i = this.getOffscreenRows();
                float f = (float) p_99316_ / (float) i;
                this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
                this.startIndex = (int) ((double) (this.scrollOffs * (float) i) + 0.5D);
            }
        } else {
            if (this.isScrollBarRightActive()) {
                int i = this.getOffscreenRowsRight();
                JobsPlus.LOGGER.info("i: " + i);
                float f = (float) p_99316_ / (float) i;
                this.scrollOffsRight = Mth.clamp(this.scrollOffsRight - f, 0.0F, 1.0F);
                int count = getActiveRightButton() == 1 ? 7 : 1;
                this.startIndexRight = (int) ((double) (this.scrollOffsRight * (float) i) + 0.5D) * count;
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
        return getActiveRightButton() == 1 ? (this.selectedJobCraftableStacks.size() + 7 - 1) / 7 - 7 :
                getActiveRightButton() == 2 ? getSelectedJob().getJobInstance().getPowerups().size() - 4 :
                        0;
    }

    private boolean isScrollBarRightActive() {
        if (getSelectedJob() == null) return false;
        return getActiveRightButton() == 1 ? this.selectedJobCraftableStacks.size() > 49 :
                getActiveRightButton() == 2 && getSelectedJob().getJobInstance().getPowerups().size() > 4;
    }

    public void blitThis(PoseStack poseStack, int posX, int posY, int startX, int startY, int stopX, int stopY) {
        blit(poseStack, this.startX + posX, this.startY + posY, startX, startY, stopX, stopY, 362, 362);
    }

    public boolean isBetween(double mouseX, double mouseY, int mouseXTop, int mouseYTop, int mouseXBottom, int mouseYBottom) {
        return mouseX >= mouseXTop && mouseY >= mouseYTop && mouseX <= mouseXBottom && mouseY <= mouseYBottom;
    }

    private @Nullable Job getSelectedJob() {
        return job;
    }

    private CompoundTag getSelectedJobData() {
        if (getSelectedJob() == null) return new CompoundTag();
        return new CompoundTag();
//        return this.dataTag.getCompound(getSelectedJob().name());
    }

    private CompoundTag getJobData(Job job) {
        return new CompoundTag();
//        return this.dataTag.getCompound(job.name());
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

    public void openConfirmScreen(Component component, String string, int powerUp) {
//        Minecraft.getInstance().setScreen(new ConfirmationScreen(component, string, Jobs.getJobFromInt(jobId), powerUp, this));

    }

    private void drawNoJobSelected(PoseStack poseStack, String string) {
        for (int i = 1; i < 6; i++) {
            drawCenteredString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.no_job_selected." + string + "." + i).getString(), startX + (imageWidth + 150) / 2, startY + 42 + (i * 9), 16777215);
        }
    }

    private void drawJobInfo(PoseStack poseStack) {
        for (int i = 1; i < 10; i++) {
            drawCenteredString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.info." + job.getJobInstance().getName() + "." + i, "" + ChatColor.boldWhite(), "" + ChatColor.darkGray(), "" + ChatColor.boldWhite(), "" + ChatColor.darkGray()).getString(), startX + (imageWidth + 150) / 2, startY + 35 + (i * 9), 16777215);
        }
    }

    private String getTCText(int num) {
//        String[] split = (JobsPlus.translatable("gui.exp." + Jobs.getEnglishString(job).toLowerCase() + "." + num).getString()).split(">text:");
//        return split.length > 1 ? split[1] : "";
        return "";
    }

    private int getTCTextColor(int num) {
//        String replace = (JobsPlus.translatable("gui.exp." + Jobs.getEnglishString(job).toLowerCase() + "." + num).getString()).split(">text:")[0].replace("color:<", "");
//        return StringUtils.isNumeric(replace) ? Integer.parseInt(replace) : 16777215;
        return 0;
    }

    public void drawnBigJobTitle(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(2F, 2F, 2F);
        font.draw(poseStack, getSelectedJobLevel() != 0 ? ChatColor.boldGreen() + job.getJobInstance().getName() : ChatColor.boldRed() + job.getJobInstance().getName(), (startX + 156) / 2F, (startY + 5) / 2F, 16777215);
        poseStack.popPose();
        if (getSelectedJobLevel() != 0) {
            font.draw(poseStack, ChatColor.darkGray() + JobsPlus.translatable("gui.level", ChatColor.reset(), getSelectedJobLevel()).getString(), startX + 156, startY + 22, 16777215);
            if (getSelectedJobLevel() != 100)
                font.draw(poseStack, ChatColor.darkGray() + JobsPlus.translatable("gui.exp", ChatColor.reset(), "[" + getSelectedJobExperience() + "/" + getSelectedJobMaxEXP() + "]").getString(), startX + 216, startY + 22, 16777215);
        } else {
            font.draw(poseStack, ChatColor.darkGray() + JobsPlus.translatable("gui.want_this_job").getString(), startX + 156, startY + 22, 16777215);
            if (hasFreeClaimableJobs()) {
                drawRightAlignedString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.cost", ChatColor.reset(), 0).getString(), startX + imageWidth - 10, startY + 22, 16777215);
            } else {
                drawRightAlignedString(poseStack, font, ChatColor.darkGray() + JobsPlus.translatable("gui.cost", ChatColor.reset(), 10).getString(), startX + imageWidth - 10, startY + 22, 16777215);
            }
        }
        font.draw(poseStack, ChatFormatting.STRIKETHROUGH + "                                        ", startX + 156, startY + 28, 16777215);
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

    public float getScrollOffs() {
        return scrollOffs;
    }

    public float getScrollOffsRight() {
        return scrollOffsRight;
    }

    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getCoins() {
        return this.jobsPlayerData.coins();
    }

    private int getActiveJobsAmount() {
        return this.jobsPlayerData.activeJobs().size();
    }

    private int getAmountOfFreeJobs() { //TODO config
        return 2;
//        return this.dataTag.getInt("AmountOfFreeJobs");
    }

    private boolean hasFreeClaimableJobs() {
        return getActiveJobsAmount() < getAmountOfFreeJobs();
    }

    private int getJobStartCost() { //TODO config
        return 10;
//        return this.dataTag.getInt("JobStartCost");
    }

    private int getJobStopCost() { //TODO config
        return 5;
//        return this.dataTag.getInt("JobStopCost");
    }

    private int getJobLevelToStopJobForFree() { //TODO config
        return 0;
//        return this.dataTag.getInt("JobLevelToStopJobForFree");
    }

    private int getPowerupCost() { //TODO config
        return 10;
//        return this.dataTag.getInt("PowerupCost");
    }

    private boolean isJobDisplayEnabled() { //TODO config
        return true;
//        return this.dataTag.getBoolean("DisplayJobConfig");
    }

    private Optional<Job> getDisplay() {
        if (jobsPlayerData.jobDisplay() == null) return Optional.empty();
        return this.jobs.stream()
                .filter(job1 -> job1.getJobInstance().getLocation().equals(jobsPlayerData.jobDisplay().jobInstance().getLocation()))
                .findFirst();
//        return this.dataTag.getInt("Display");
    }

    private Job getActiveBossBar() {
        return null;
//        return this.dataTag.getInt("ActiveBossBar");
    }

    private PowerupState getSelectedJobPowerupState(PowerupInstance powerup) {
        return getSelectedJob().hasPowerup(powerup) ? getSelectedJob().getPowerups().get(powerup.getLocation()) : PowerupState.NOT_OWNED;
    }

    private boolean hasBoughtPowerup(PowerupInstance powerup) {
        return getSelectedJob().hasPowerup(powerup);
    }

    private boolean hasPowerupActive(PowerupInstance powerup) {
        return getSelectedJobPowerupState(powerup) == PowerupState.ACTIVE;
    }

    private boolean hasPowerupDisabled(PowerupInstance powerup) {
        return getSelectedJobPowerupState(powerup) == PowerupState.INACTIVE;
    }

    private int getSelectedJobSuperpowerState() {
        return getSelectedJobData().getInt("Superpower");
    }

    private boolean hasSelectedJobSuperpowerEnabled() {
        return getSelectedJobSuperpowerState() == 0;
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
