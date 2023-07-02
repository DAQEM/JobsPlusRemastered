package com.daqem.jobsplus.player;

import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.resources.job.JobInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface JobsPlayer {

    List<Job> getJobs();

    List<JobInstance> getJobInstances();

    List<Job> getInactiveJobs();

    @Nullable
    Job addNewJob(JobInstance job);

    void removeJob(JobInstance job);

    Job getJob(@Nullable JobInstance jobLocation);

    int getCoins();

    void addCoins(int coins);

    void setCoins(int coins);

    UUID getUUID();

    String name();

    double nextRandomDouble();

    Level level();

    Player getPlayer();
}
