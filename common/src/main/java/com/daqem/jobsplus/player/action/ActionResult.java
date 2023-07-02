package com.daqem.jobsplus.player.action;

public class ActionResult {

    private boolean cancelAction = false;
    private float destroySpeedModifier = 1.0F;

    public ActionResult combine(ActionResult other) {
        this.cancelAction = this.cancelAction || other.cancelAction;
        this.destroySpeedModifier = this.destroySpeedModifier * other.destroySpeedModifier;
        return this;
    }

    public boolean shouldCancelAction() {
        return cancelAction;
    }

    public ActionResult withCancelAction(boolean cancelAction) {
        this.cancelAction = cancelAction;
        return this;
    }

    public float getDestroySpeedModifier() {
        return destroySpeedModifier;
    }

    public ActionResult withDestroySpeedModifier(float destroySpeedModifier) {
        this.destroySpeedModifier = destroySpeedModifier;
        return this;
    }
}