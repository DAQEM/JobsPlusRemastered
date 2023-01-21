package com.daqem.jobsplus.player.job.powerup;

import net.minecraft.resources.ResourceLocation;

public class Powerup {

    private final PowerupState powerupState;

    public Powerup(ResourceLocation location, PowerupState powerupState) {
        this.powerupState = powerupState;
    }

    public PowerupState getPowerupState() {
        return powerupState;
    }
}
