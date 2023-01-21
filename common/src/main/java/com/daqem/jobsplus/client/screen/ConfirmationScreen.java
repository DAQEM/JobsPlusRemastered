package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfirmationScreen extends Screen {

    private static final ResourceLocation BACKGROUND = JobsPlus.getId("textures/gui/confirmation_screen.png");

    protected ConfirmationScreen() {
        super(JobsPlus.literal("Confirmation"));
    }

    public enum MessageType {
        START_JOB_FREE(JobsPlus.translatable("confirmation.start_job_free"), ButtonType.YES_NO),
        STOP_JOB_FREE(JobsPlus.translatable("confirmation.stop_job_free"), ButtonType.YES_NO),
        POWER_UP(JobsPlus.translatable("confirmation.power_up"), ButtonType.YES_NO),
        START_JOB_PAID(JobsPlus.translatable("confirmation.start_job_paid"), ButtonType.YES_NO),
        STOP_JOB_PAID(JobsPlus.translatable("confirmation.stop_job_paid"), ButtonType.YES_NO),
        NOT_ENOUGH_COINS_STOP(JobsPlus.translatable("confirmation.error.not_enough_coins_stop"), ButtonType.BACK),
        NOT_ENOUGH_COINS_START(JobsPlus.translatable("confirmation.error.not_enough_coins_start"), ButtonType.BACK),
        NOT_ENOUGH_COINS_POWERUP(JobsPlus.translatable("confirmation.error.not_enough_coins_powerup"), ButtonType.BACK),
        JOB_NOT_ENABLED(JobsPlus.translatable("confirmation.error.job_not_enabled"), ButtonType.BACK),
        MUST_BE_LEVEL_100(JobsPlus.translatable("confirmation.error.must_be_level_100"), ButtonType.BACK);

        private final Component message;

        MessageType(Component message, ButtonType buttonType) {
            this.message = message;
        }

        public Component getMessage() {
            return this.message;
        }
    }

    public enum ButtonType {
        YES_NO,
        BACK
    }
}
