package com.daqem.jobsplus.player.job.powerup;

import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Powerup {

    private @Nullable Powerup parent;
    private final List<Powerup> children = new ArrayList<>();

    private final PowerupInstance powerupInstance;
    private PowerupState powerupState;

    public Powerup(PowerupInstance powerupInstance, PowerupState powerupState) {
        this.powerupInstance = powerupInstance;
        this.powerupState = powerupState;
    }

    public PowerupInstance getPowerupInstance() {
        return powerupInstance;
    }

    public PowerupState getPowerupState() {
        return powerupState;
    }

    public void setPowerupState(PowerupState powerupState) {
        this.powerupState = powerupState;
    }

    public List<Powerup> getChildren() {
        return children;
    }

    @Nullable
    public Powerup getParent() {
        return parent;
    }

    public void setParent(@Nullable Powerup parent) {
        this.parent = parent;
    }

    public void toggle() {
        if (powerupState == PowerupState.ACTIVE) {
            powerupState = PowerupState.INACTIVE;
        } else if (powerupState == PowerupState.INACTIVE) {
            powerupState = PowerupState.ACTIVE;
        }
    }

    public boolean hasActiveChildren() {
        for (Powerup child : children) {
            if (child.getPowerupState() == PowerupState.ACTIVE || child.hasActiveChildren()) {
                return true;
            }
        }
        return false;
    }
}
