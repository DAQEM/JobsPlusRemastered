package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.resources.job.action.Actions;

public class JobEvents {

    public static void onJobLevelUp(JobsServerPlayer player, Job job) {
        new ActionDataBuilder(player, Actions.JOB_LEVEL_UP)
                .withSpecification(ActionSpecification.ONLY_FOR_JOB, job)
                .build()
                .sendToAction();
    }

    public static void onJobExperience(JobsServerPlayer player, Job job, int experience) {
        new ActionDataBuilder(player, Actions.JOB_EXP)
                .withSpecification(ActionSpecification.JOB_EXP, experience)
                .withSpecification(ActionSpecification.ONLY_FOR_JOB, job)
                .build()
                .sendToAction();
    }
}
