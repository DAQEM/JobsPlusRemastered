package com.daqem.jobsplus.integration.arc.action.type;

import com.daqem.arc.api.action.IAction;
import com.daqem.arc.api.action.type.ActionType;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.integration.arc.action.actions.job.JobExpAction;
import com.daqem.jobsplus.integration.arc.action.actions.job.JobLevelUpAction;

public interface JobsPlusActionType<T extends IAction> extends ActionType<T> {

    ActionType<JobExpAction> JOB_EXP = ActionType.register(JobsPlus.getId("on_job_exp"), new JobExpAction.Serializer());
    ActionType<JobLevelUpAction> JOB_LEVEL_UP = ActionType.register(JobsPlus.getId("on_job_level_up"), new JobLevelUpAction.Serializer());

    static void init() {
    }
}
