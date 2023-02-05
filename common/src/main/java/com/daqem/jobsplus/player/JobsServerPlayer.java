package com.daqem.jobsplus.player;

import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.display.JobDisplay;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.player.stat.StatData;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobsServerPlayer {

    List<Job> getJobs();

    List<JobInstance> getJobInstances();

    List<Job> getInactiveJobs();

    List<JobInstance> getInactiveJobInstances();

    void addNewJob(ResourceLocation jobLocation);

    void addNewJob(JobInstance job);

    void removeJob(JobInstance job);

    boolean hasJob(JobInstance jobLocation);

    Job getJob(JobInstance jobLocation);

    int getCoins();

    void addCoins(int coins);

    void setCoins(int coins);

    Optional<JobDisplay> getDisplay();

    void setDisplay(JobDisplay jobDisplay);

    void addPowerup(PowerupInstance powerupInstance);

    void removePowerup(PowerupInstance powerupInstance);

    void setPowerup(PowerupInstance powerupInstance, PowerupState powerupState);

    boolean hasPowerup(PowerupInstance powerupInstance);

    UUID getUUID();

    ServerPlayer getServerPlayer();

    String name();

    ListTag inactiveJobsToNBT();

    void togglePowerup(PowerupInstance powerupInstance);

    NonNullList<StatData> getStatData();

    void addStatData(StatData statData);

    int getSwimmingDistanceInCm();

    void setSwimmingDistanceInCm(int swimmingDistanceInCm);
}
