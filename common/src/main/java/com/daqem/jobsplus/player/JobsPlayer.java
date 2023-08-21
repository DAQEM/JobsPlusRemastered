package com.daqem.jobsplus.player;

import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface JobsPlayer {

    List<Job> jobsplus$getJobs();

    List<JobInstance> jobsplus$getJobInstances();

    List<Job> jobsplus$getInactiveJobs();

    @Nullable
    Job jobsplus$addNewJob(JobInstance job);

    void jobsplus$removeJob(JobInstance job);

    void jobsplus$removeActionHolders(Job job);

    Job jobsplus$getJob(@Nullable JobInstance jobLocation);

    int jobsplus$getCoins();

    void jobsplus$addCoins(int coins);

    void jobsplus$setCoins(int coins);

    String jobsplus$getName();

    double jobsplus$nextRandomDouble();

    Player jobsplus$getPlayer();

    Level jobsplus$getLevel();

    UUID jobsplus$getUUID();

    List<IActionHolder> jobsplus$getActionHolders();
}
