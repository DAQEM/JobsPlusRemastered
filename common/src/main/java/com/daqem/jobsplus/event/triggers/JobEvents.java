package com.daqem.jobsplus.event.triggers;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.data.type.JobsPlusActionDataType;
import com.daqem.jobsplus.interation.arc.action.type.JobsPlusActionType;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;

public class JobEvents {

    public static void onJobLevelUp(JobsPlayer player, Job job) {
        if (player instanceof ArcPlayer arcPlayer) {
            new ActionDataBuilder(arcPlayer, JobsPlusActionType.JOB_LEVEL_UP)
                    .withData(JobsPlusActionDataType.ONLY_FOR_JOB, job)
                    .build()
                    .sendToAction();
        } else {
            JobsPlus.LOGGER.error("Player is not an ArcPlayer");
        }
    }

    public static void onJobExperience(JobsPlayer player, Job job, int experience) {
        if (player instanceof ArcPlayer arcPlayer) {
            new ActionDataBuilder(arcPlayer, JobsPlusActionType.JOB_EXP)
                    .withData(JobsPlusActionDataType.JOB_EXP, experience)
                    .withData(JobsPlusActionDataType.ONLY_FOR_JOB, job)
                    .build()
                    .sendToAction();
        } else {
            JobsPlus.LOGGER.error("Player is not an ArcPlayer");
        }
    }
}
