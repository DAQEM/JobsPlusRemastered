package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.action.ActionDataBuilder;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.resources.job.action.Actions;

public class JobEvents {

    public static void onJobLevelUp(JobsPlayer player, Job job) {
        new ActionDataBuilder(player, Actions.JOB_LEVEL_UP)
                .withSpecification(ActionSpecification.ONLY_FOR_JOB, job)
                .build()
                .sendToAction();
    }

    public static void onJobExperience(JobsPlayer player, Job job, int experience) {
        new ActionDataBuilder(player, Actions.JOB_EXP)
                .withSpecification(ActionSpecification.JOB_EXP, experience)
                .withSpecification(ActionSpecification.ONLY_FOR_JOB, job)
                .build()
                .sendToAction();
    }
}
