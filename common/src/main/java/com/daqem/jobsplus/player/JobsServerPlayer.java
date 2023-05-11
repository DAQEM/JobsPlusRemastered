package com.daqem.jobsplus.player;

import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.stat.StatData;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.job.JobInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface JobsServerPlayer {

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

    ServerPlayer getServerPlayer();

    String name();

    ListTag inactiveJobsToNBT();

    NonNullList<StatData> getStatData();

    void addStatData(StatData statData);

    void setSwimmingDistanceInCm(int swimmingDistanceInCm);

    void setElytraFlyingDistanceInCm(float flyingDistanceInCm);

    void removeAndRefundJob(@NotNull JobInstance jobInstance);

    void refundJob(@NotNull JobInstance jobInstance);

    CraftingResult canCraft(CraftingType crafting, ItemStack itemStack);

    boolean getUpdatedFromOldJobsPlus();

    void setUpdatedFromOldJobsPlus(boolean b);
}
