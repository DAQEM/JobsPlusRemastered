package com.daqem.jobsplus.player.job;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.event.triggers.JobEvents;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.powerup.JobPowerupManager;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Job {

    private final JobsPlayer player;
    private final JobInstance jobInstance;
    private final JobPowerupManager powerupManager;
    private int level;
    private int experience;

    public Job(JobsPlayer player, JobInstance jobInstance) {
        this(player, jobInstance, 0, 0, new ArrayList<>());
    }

    public Job(JobsPlayer player, JobInstance jobInstance, int level, int experience) {
        this(player, jobInstance, level, experience, new ArrayList<>());
    }

    public Job(JobsPlayer player, ResourceLocation jobInstanceLocation, int level, int experience, @NotNull List<Powerup> powerups) {
        this(player, JobManager.getInstance().getJobs().get(jobInstanceLocation), level, experience, powerups);
    }

    public Job(JobsPlayer player, JobInstance jobInstance, int level, int experience, @NotNull List<Powerup> powerups) {
        this.player = player;
        this.jobInstance = jobInstance;
        this.powerupManager = new JobPowerupManager(jobInstance, powerups);
        this.level = level;
        this.experience = experience;
    }

    public JobInstance getJobInstance() {
        return jobInstance;
    }

    public JobPowerupManager getPowerupManager() {
        return powerupManager;
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
        while (this.experience >= getExperienceToLevelUp(level)) {
            checkForLevelUp();
        }
    }

    public void addExperience(int experience) {
        setExperience(getExperience() + experience);
        JobEvents.onJobExperience(player, this, experience);
    }

    public void addExperienceWithoutEvent(int experience) {
        setExperience(getExperience() + experience);
    }

    private void checkForLevelUp() {
        int experienceToLevelUp = getExperienceToLevelUp(level);
        if (experience >= experienceToLevelUp) {
            level++;
            experience -= experienceToLevelUp;
            JobEvents.onJobLevelUp(player, this);
            if (this.player instanceof JobsServerPlayer serverPlayer) {
                serverPlayer.jobsplus$updateJobOnClient(this);
            }
        }
    }

    public static int getExperienceToLevelUp(int level) {
        if (level == 0) return 0;
        return (int) (100 + level * level * 0.5791);
    }

    public CompoundTag toNBT() {
        CompoundTag jobTag = new CompoundTag();

        jobTag.putString(Constants.JOB_INSTANCE_LOCATION, getJobInstance().getLocation().toString());
        jobTag.putInt(Constants.LEVEL, getLevel());
        jobTag.putInt(Constants.EXPERIENCE, getExperience());

        ListTag powerupsTag = new ListTag();

        for (Powerup powerup : powerupManager.getAllPowerups()) {
            CompoundTag powerupTag = new CompoundTag();

            powerupTag.putString(Constants.POWERUP_LOCATION, powerup.getPowerupInstance().getLocation().toString());
            powerupTag.putString(Constants.POWERUP_STATE, powerup.getPowerupState().name());

            powerupsTag.add(powerupTag);
        }

        jobTag.put(Constants.POWERUPS, powerupsTag);

        return jobTag;
    }

    public static Job fromNBT(JobsPlayer player, CompoundTag tag) {

        ResourceLocation jobInstanceLocation = new ResourceLocation(tag.getString(Constants.JOB_INSTANCE_LOCATION));
        int level = tag.getInt(Constants.LEVEL);
        int exp = tag.getInt(Constants.EXPERIENCE);
        ListTag powerupsTag = tag.getList(Constants.POWERUPS, Tag.TAG_COMPOUND);

        List<Powerup> powerups = new ArrayList<>();

        for (int i = 0; i < powerupsTag.size(); i++) {
            CompoundTag powerupTag = powerupsTag.getCompound(i);
            ResourceLocation powerupLocation = new ResourceLocation(powerupTag.getString(Constants.POWERUP_LOCATION));
            PowerupState state = PowerupState.valueOf(powerupTag.getString(Constants.POWERUP_STATE));

            powerups.add(new Powerup(PowerupInstance.of(powerupLocation), state));
        }

        return new Job(player, jobInstanceLocation, level, exp, powerups);
    }

    public double getExperiencePercentage() {
        return (double) experience / (double) getExperienceToLevelUp(level) * 100;
    }

    public static class Serializer {

        public static Job fromNetwork(FriendlyByteBuf friendlyByteBuf, JobsPlayer player) {
            ResourceLocation jobInstanceLocation = friendlyByteBuf.readResourceLocation();
            int level = friendlyByteBuf.readInt();
            int experience = friendlyByteBuf.readInt();
            int powerupCount = friendlyByteBuf.readVarInt();
            List<Powerup> powerups = new ArrayList<>();
            for (int i = 0; i < powerupCount; i++) {
                ResourceLocation powerupLocation = friendlyByteBuf.readResourceLocation();
                PowerupState state = friendlyByteBuf.readEnum(PowerupState.class);
                powerups.add(new Powerup(PowerupInstance.of(powerupLocation), state));
            }
            return new Job(player, jobInstanceLocation, level, experience, powerups);
        }

        public static void toNetwork(FriendlyByteBuf friendlyByteBuf, Job job) {
            friendlyByteBuf.writeResourceLocation(job.getJobInstance().getLocation());
            friendlyByteBuf.writeInt(job.getLevel());
            friendlyByteBuf.writeInt(job.getExperience());
            friendlyByteBuf.writeVarInt(job.getPowerupManager().getAllPowerups().size());
            for (Powerup powerup : job.getPowerupManager().getAllPowerups()) {
                friendlyByteBuf.writeResourceLocation(powerup.getPowerupInstance().getLocation());
                friendlyByteBuf.writeEnum(powerup.getPowerupState());
            }
        }
    }
}
