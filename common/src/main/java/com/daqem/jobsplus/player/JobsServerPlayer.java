package com.daqem.jobsplus.player;

import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.player.job.Job;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface JobsServerPlayer extends JobsPlayer {

    ServerPlayer jobsplus$getServerPlayer();

    ListTag jobsplus$inactiveJobsToNBT();

    void jobsplus$removeAndRefundJob(@NotNull JobInstance jobInstance);

    void jobsplus$refundJob(@NotNull JobInstance jobInstance);

    CraftingResult jobsplus$canCraft(CraftingType crafting, ItemStack itemStack);

    boolean jobsplus$getUpdatedFromOldJobsPlus();

    void jobsplus$setUpdatedFromOldJobsPlus(boolean b);

    void jobsplus$updateJob(Job job);

    void jobsplus$updateActionHolders(Job job);

    void jobsplus$updateJobOnClient(Job job);
}
