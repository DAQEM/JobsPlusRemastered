package com.daqem.jobsplus.player.job.powerup;

import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;

public class Powerup {

    private final PowerupState powerupState;
    private final PowerupInstance powerupInstance;

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
}
