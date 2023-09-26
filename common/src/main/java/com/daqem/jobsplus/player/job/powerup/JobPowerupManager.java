package com.daqem.jobsplus.player.job.powerup;

import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class JobPowerupManager {

    private final JobInstance job;
    private List<Powerup> powerups;

    public JobPowerupManager(JobInstance job, @NotNull List<Powerup> powerups) {
        this.job = job;
        this.powerups = sortPowerups(powerups);
    }

    public JobInstance getJob() {
        return job;
    }

    public List<Powerup> getPowerups() {
        return powerups;
    }

    public void setPowerups(List<Powerup> powerups) {
        this.powerups = powerups;
    }

    public @Nullable Powerup getPowerup(PowerupInstance powerupInstance) {
        if (powerupInstance == null) {
            return null;
        }
        return getPowerupRecursive(powerups, powerupInstance);
    }

    public Powerup getPowerupRecursive(List<Powerup> powerups, PowerupInstance powerupInstance) {
        for (Powerup powerup : powerups) {
            if (powerup.getPowerupInstance().getLocation().equals(powerupInstance.getLocation())) {
                return powerup;
            }
            Powerup found = getPowerupRecursive(powerup.getChildren(), powerupInstance);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public List<Powerup> getAllPowerups() {
        return getAllPowerupsRecursive(powerups);
    }

    private List<Powerup> getAllPowerupsRecursive(List<Powerup> powerups) {
        List<Powerup> allPowerups = new ArrayList<>();
        for (Powerup powerup : powerups) {
            allPowerups.add(powerup);
            allPowerups.addAll(getAllPowerupsRecursive(powerup.getChildren()));
        }
        return allPowerups;
    }

    public boolean addPowerup(JobsPlayer player, Job job, PowerupInstance powerupInstance) {
        return addPowerup(player, job, powerupInstance, PowerupState.ACTIVE);
    }

    public boolean addPowerup(JobsPlayer player, Job job, PowerupInstance powerupInstance, PowerupState powerupState) {
        if (canAddPowerup(powerupInstance)) {
            if (powerupInstance.getParent() == null) {
                powerups.add(new Powerup(powerupInstance, powerupState));
                this.sendJobUpdatePacket(job, player);
                return true;
            } else {
                Powerup parent = getPowerup(powerupInstance.getParent());
                if (parent != null) {
                    parent.getChildren().add(new Powerup(powerupInstance, powerupState));
                    this.sendJobUpdatePacket(job, player);
                    return true;
                }
            }
        }
        return false;
    }

    private void sendJobUpdatePacket(Job job, JobsPlayer player) {
        if (player instanceof JobsServerPlayer jobsServerPlayer) {
            jobsServerPlayer.jobsplus$updateJob(job);
        }
    }

    public boolean canAddPowerup(PowerupInstance powerupInstance) {
        if (powerupInstance.getParent() == null) {
            return true;
        }
        Powerup parent = getPowerup(powerupInstance.getParent());
        return parent != null;
    }

    public void forceAddPowerup(PowerupInstance powerupInstance, PowerupState powerupState) {
        Powerup powerup = getPowerup(powerupInstance);
        if (powerup == null) {
            powerup = new Powerup(powerupInstance, powerupState);
            forceAddPowerupRecursive(powerupInstance, powerup, powerupState);
        } else {
            if (powerupState == PowerupState.NOT_OWNED) {
                removePowerup(powerupInstance);
            } else {
                powerup.setPowerupState(powerupState);
            }
        }
    }

    private void forceAddPowerupRecursive(PowerupInstance powerupInstance, Powerup powerup, PowerupState powerupState) {
        if (powerupInstance.getParent() == null) {
            powerups.add(powerup);
        } else {
            Powerup parent = getPowerup(powerupInstance.getParent());
            if (parent == null) {
                parent = new Powerup(powerupInstance.getParent(), powerupState);
                forceAddPowerupRecursive(powerupInstance.getParent(), parent, powerupState);
            }
            parent.getChildren().add(powerup);
            powerup.setParent(parent);
        }
    }


    public void removePowerup(PowerupInstance powerupInstance) {
        Powerup powerup = getPowerup(powerupInstance);
        if (powerup != null) {
            Powerup parent = powerup.getParent();
            if (parent != null) {
                parent.getChildren().remove(powerup);
            } else {
                powerups.remove(powerup);
            }
        }
    }

    public void setPowerupState(@NotNull Powerup powerup, @NotNull PowerupState powerupState) {
        if (powerupState == PowerupState.NOT_OWNED) {
            removePowerup(powerup.getPowerupInstance());
        } else {
            powerup.setPowerupState(powerupState);
        }
    }

    public List<Powerup> sortPowerups(List<Powerup> powerups) {
        List<Powerup> rootPowerups = new ArrayList<>();

        Map<PowerupInstance, Powerup> powerupMap = new HashMap<>();
        for (Powerup powerup : powerups) {
            powerupMap.put(powerup.getPowerupInstance(), powerup);
        }

        for (Powerup powerup : powerups) {
            PowerupInstance powerupInstance = powerup.getPowerupInstance();
            if (powerupInstance != null) {
                PowerupInstance parentInstance = powerupInstance.getParent();
                if (parentInstance != null) {
                    Powerup parentPowerup = powerupMap.get(parentInstance);
                    if (parentPowerup != null) {
                        parentPowerup.getChildren().add(powerup);
                        powerup.setParent(parentPowerup);
                    }
                } else {
                    rootPowerups.add(powerup);
                }
            }
        }

        rootPowerups.sort(Comparator.comparing(powerup -> powerup.getPowerupInstance().getLocation()));
        return rootPowerups;
    }

    public void clearPowerups() {
        powerups.clear();
    }
}
