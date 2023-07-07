package com.daqem.jobsplus.interation.arc.action.serializer;

import com.daqem.arc.api.action.IAction;
import com.daqem.arc.api.action.serializer.ActionSerializer;
import com.daqem.arc.api.action.serializer.IActionSerializer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.actions.job.JobExpAction;
import com.daqem.jobsplus.interation.arc.action.actions.job.JobLevelUpAction;

public interface JobsPlusActionSerializer<T extends IAction> extends ActionSerializer<T> {

    IActionSerializer<JobExpAction> JOB_EXP = ActionSerializer.register(JobsPlus.getId("on_job_exp"), new JobExpAction.Serializer());
    IActionSerializer<JobLevelUpAction> JOB_LEVEL_UP = ActionSerializer.register(JobsPlus.getId("on_job_level_up"), new JobLevelUpAction.Serializer());

    static void init() {
    }
}
