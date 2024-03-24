package com.daqem.jobsplus.player.job.powerup;

import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class JobPowerupManager {

    private final List<Powerup> powerups;

    public JobPowerupManager(@NotNull List<Powerup> powerups) {
        this.powerups = powerups;
    }

    public Optional<Powerup> getPowerup(PowerupInstance powerupInstance) {
        return powerups.stream()
                .filter(powerup -> powerup.getPowerupInstance().getLocation().equals(powerupInstance.getLocation()))
                .findFirst();
    }

    public List<Powerup> getAllPowerups() {
        return powerups;
    }

    public boolean addPowerup(JobsPlayer player, Job job, PowerupInstance powerupInstance) {
        return addPowerup(player, job, powerupInstance, PowerupState.ACTIVE);
    }

    public boolean addPowerup(JobsPlayer player, Job job, PowerupInstance powerupInstance, PowerupState powerupState) {
        if (canAddPowerup(powerupInstance)) {
            powerups.add(new Powerup(powerupInstance, powerupState));
            this.sendJobUpdatePacket(job, player);
            return true;
        }
        return false;
    }

    private void sendJobUpdatePacket(Job job, JobsPlayer player) {
        if (player instanceof JobsServerPlayer jobsServerPlayer) {
            jobsServerPlayer.jobsplus$updateJob(job);
        }
    }

    public boolean canAddPowerup(PowerupInstance powerupInstance) {
        if (powerupInstance.getParent() == null) return true;
        return getPowerup(powerupInstance.getParent()).isPresent();
    }

    public void forceAddPowerup(JobsPlayer player, Job job, PowerupInstance powerupInstance, PowerupState powerupState) {
        powerups.add(new Powerup(powerupInstance, powerupState));
        this.sendJobUpdatePacket(job, player);
    }

    public void clearPowerups() {
        powerups.clear();
    }

    public Optional<Powerup> getParent(PowerupInstance powerupInstance) {
        PowerupInstance parentPowerupInstance = powerupInstance.getParent();
        if (parentPowerupInstance == null) return Optional.empty();
        return getPowerup(parentPowerupInstance);
    }

    public Optional<Powerup> getParent(Powerup powerup) {
        PowerupInstance parentPowerupInstance = powerup.getPowerupInstance().getParent();
        if (parentPowerupInstance == null) return Optional.empty();
        return getPowerup(parentPowerupInstance);
    }

    public List<Powerup> getChildren(PowerupInstance powerupInstance) {
        return getChildren(powerupInstance, powerups);
    }

    public List<Powerup> getChildren(Powerup powerup) {
        return getChildren(powerup, powerups);
    }

    public static List<Powerup> getChildren(PowerupInstance powerupInstance, List<Powerup> powerups) {
        return powerups.stream()
                .filter(powerup -> powerup.getPowerupInstance().getParentLocation() != null
                        && powerup.getPowerupInstance().getParentLocation().equals(powerupInstance.getLocation()))
                .toList();
    }

    public static List<Powerup> getChildren(Powerup powerup, List<Powerup> powerups) {
        return getChildren(powerup.getPowerupInstance(), powerups);
    }
}
