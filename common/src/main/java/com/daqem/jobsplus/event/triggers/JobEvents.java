package com.daqem.jobsplus.event.triggers;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.integration.arc.data.type.JobsPlusActionDataType;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.action.type.JobsPlusActionType;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import net.minecraft.server.level.ServerPlayer;

public class JobEvents {

    public static void onJobLevelUp(JobsPlayer player, Job job) {
        if (player instanceof ArcPlayer arcPlayer) {
            new ActionDataBuilder(arcPlayer, JobsPlusActionType.JOB_LEVEL_UP)
                    .withData(JobsPlusActionDataType.ONLY_FOR_JOB, job)
                    .build()
                    .sendToAction();
        }
        player.jobsplus$addCoins(JobsPlusCommonConfig.coinsPerLevelUp.get());
        if (player.jobsplus$getPlayer() instanceof ServerPlayer serverPlayer) {

            JobInstance jobInstance = job.getJobInstance();
            serverPlayer.sendSystemMessage(JobsPlus.translatable("job.level_up", serverPlayer.getName().copy().withStyle(style -> style.withColor(jobInstance.getColorDecimal())), JobsPlus.literal(String.valueOf(job.getLevel())).withStyle(style -> style.withColor(jobInstance.getColorDecimal())), jobInstance.getNameComponent()), false);
        }
    }

    public static void onJobExperience(JobsPlayer player, Job job, int experience) {
        if (player instanceof ArcPlayer arcPlayer) {
            new ActionDataBuilder(arcPlayer, JobsPlusActionType.JOB_EXP)
                    .withData(JobsPlusActionDataType.JOB_EXP, experience)
                    .withData(JobsPlusActionDataType.ONLY_FOR_JOB, job)
                    .build()
                    .sendToAction();
        }
    }
}
