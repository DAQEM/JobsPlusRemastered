package com.daqem.jobsplus.player.job.powerup;

import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Powerup {

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

    public void toggle() {
        if (powerupState == PowerupState.ACTIVE) {
            powerupState = PowerupState.INACTIVE;
        } else if (powerupState == PowerupState.INACTIVE) {
            powerupState = PowerupState.ACTIVE;
        }
    }
}
