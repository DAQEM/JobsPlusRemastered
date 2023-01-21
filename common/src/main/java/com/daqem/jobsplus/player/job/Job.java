package com.daqem.jobsplus.player.job;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Job {

    private final JobInstance jobInstance;
    private int level;
    private int experience;
    private final Map<ResourceLocation, PowerupState> powerups;

    public Job(JobInstance jobInstance) {
        this(jobInstance, 0, 0, new HashMap<>());
    }

    public Job(ResourceLocation jobInstanceLocation, int level, int experience, Map<ResourceLocation, PowerupState> powerups) {
        this(JobManager.getInstance().getJobs().get(jobInstanceLocation), level, experience, powerups);
    }

    public Job(JobInstance jobInstance, int level, int experience, Map<ResourceLocation, PowerupState> powerups) {
        this.jobInstance = jobInstance;
        this.level = level;
        this.experience = experience;
        this.powerups = powerups;
    }

    public JobInstance getJobInstance() {
        return jobInstance;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Map<ResourceLocation, PowerupState> getPowerups() {
        return powerups;
    }

    public void addPowerup(PowerupInstance powerupInstance) {
        powerups.put(powerupInstance.getLocation(), PowerupState.ACTIVE);
    }

    public void removePowerup(PowerupInstance powerupInstance) {
        powerups.remove(powerupInstance.getLocation());
    }

    public boolean hasPowerup(PowerupInstance powerupInstance) {
        return powerups.containsKey(powerupInstance.getLocation());
    }

    public void setPowerupState(PowerupInstance powerupInstance, PowerupState powerupState) {
        powerups.put(powerupInstance.getLocation(), powerupState);
    }

    public CompoundTag toNBT() {
        CompoundTag jobTag = new CompoundTag();

        jobTag.putString(Constants.JOB_INSTANCE_LOCATION, getJobInstance().getLocation().toString());
        jobTag.putInt(Constants.LEVEL, getLevel());
        jobTag.putInt(Constants.EXPERIENCE, getExperience());

        ListTag powerupsTag = new ListTag();

        for (Map.Entry<ResourceLocation, PowerupState> powerup : getPowerups().entrySet()) {
            CompoundTag powerupTag = new CompoundTag();

            powerupTag.putString(Constants.POWERUP_LOCATION, powerup.getKey().toString());
            powerupTag.putString(Constants.POWERUP_STATE, powerup.getValue().name());

            powerupsTag.add(powerupTag);
        }

        jobTag.put(Constants.POWERUPS, powerupsTag);

        return jobTag;
    }

    public static Job fromNBT(CompoundTag tag) {

        ResourceLocation jobInstanceLocation = new ResourceLocation(tag.getString(Constants.JOB_INSTANCE_LOCATION));
        int level = tag.getInt(Constants.LEVEL);
        int exp = tag.getInt(Constants.EXPERIENCE);
        ListTag powerupsTag = tag.getList(Constants.POWERUPS, Tag.TAG_COMPOUND);

        Map<ResourceLocation, PowerupState> powerups = new HashMap<>();

        for (int i = 0; i < powerupsTag.size(); i++) {
            CompoundTag powerupTag = powerupsTag.getCompound(i);
            ResourceLocation powerupLocation = new ResourceLocation(powerupTag.getString(Constants.POWERUP_LOCATION));
            PowerupState state = PowerupState.valueOf(powerupTag.getString(Constants.POWERUP_STATE));

            powerups.put(powerupLocation, state);
        }

        return new Job(jobInstanceLocation, level, exp, powerups);
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobInstance=" + jobInstance +
                ", level=" + level +
                ", experience=" + experience +
                ", powerups=" + powerups +
                '}';
    }

    public String toShortString() {
        return """
                Job {
                    job: %s,
                    level: %d,
                    experience: %d,
                    powerups: %s
                }
                """.formatted(jobInstance.getLocation(), level, experience, powerups.toString().replace(", ", ", \n"));
    }
}
