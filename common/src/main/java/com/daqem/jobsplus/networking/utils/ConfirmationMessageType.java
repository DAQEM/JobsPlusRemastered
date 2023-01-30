package com.daqem.jobsplus.networking.utils;

import net.minecraft.network.chat.Component;

public enum ConfirmationMessageType {
    START_JOB_FREE(new ConfirmationMessage("start_job_free"), ConfirmationButtonType.YES_NO, RequireCoinsType.NONE),
    STOP_JOB_FREE(new ConfirmationMessage("stop_job_free"), ConfirmationButtonType.YES_NO, RequireCoinsType.NONE),
    BUY_POWER_UP(new ConfirmationMessage("buy_power_up"), ConfirmationButtonType.YES_NO, RequireCoinsType.POWER_UP),
    START_JOB_PAID(new ConfirmationMessage("start_job_paid"), ConfirmationButtonType.YES_NO, RequireCoinsType.START_JOB),
    STOP_JOB_PAID(new ConfirmationMessage("stop_job_paid"), ConfirmationButtonType.YES_NO, RequireCoinsType.STOP_JOB),
    NOT_ENOUGH_COINS_STOP(new ConfirmationMessage("error.not_enough_coins_stop"), ConfirmationButtonType.BACK, RequireCoinsType.STOP_JOB),
    NOT_ENOUGH_COINS_START(new ConfirmationMessage("error.not_enough_coins_start"), ConfirmationButtonType.BACK, RequireCoinsType.START_JOB),
    NOT_ENOUGH_COINS_POWERUP(new ConfirmationMessage("error.not_enough_coins_powerup"), ConfirmationButtonType.BACK, RequireCoinsType.POWER_UP),
    JOB_NOT_ENABLED(new ConfirmationMessage("error.job_not_enabled"), ConfirmationButtonType.BACK, RequireCoinsType.NONE);

    private final ConfirmationMessage confirmationMessage;
    private final ConfirmationButtonType confirmationButtonType;
    private final RequireCoinsType requireCoinsType;

    ConfirmationMessageType(ConfirmationMessage confirmationMessage, ConfirmationButtonType buttonType, RequireCoinsType requireCoinsType) {
        this.confirmationMessage = confirmationMessage;
        this.confirmationButtonType = buttonType;
        this.requireCoinsType = requireCoinsType;
    }

    public ConfirmationButtonType getButtonType() {
        return confirmationButtonType;
    }

    public ConfirmationMessage getConfirmationMessage() {
        return this.confirmationMessage;
    }

    public ConfirmationMessageType withObjects(Object... objects) {
        this.confirmationMessage.withObjects(objects);
        return this;
    }

    public RequireCoinsType getRequireCoinsType() {
        return requireCoinsType;
    }

    public Component getMessage() {
        return this.confirmationMessage.getComponent();
    }

    public enum RequireCoinsType {
        START_JOB,
        STOP_JOB,
        POWER_UP,
        NONE
    }
}
