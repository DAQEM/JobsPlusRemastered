package com.daqem.jobsplus.player;

import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.player.job.Job;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface JobsServerPlayer extends JobsPlayer {

    ServerPlayer jobsplus$getServerPlayer();

    ListTag jobsplus$inactiveJobsToNBT();

    void jobsplus$removeAndRefundJob(@NotNull JobInstance jobInstance);

    void jobsplus$refundJob(@NotNull JobInstance jobInstance);

    boolean jobsplus$getUpdatedFromOldJobsPlus();

    void jobsplus$setUpdatedFromOldJobsPlus(boolean b);

    void jobsplus$updateJob(Job job);

    void jobsplus$updateActionHolders(Job job);

    void jobsplus$updateJobOnClient(Job job);

    void jobsplus$removeJobOnClient(Job job);
}
