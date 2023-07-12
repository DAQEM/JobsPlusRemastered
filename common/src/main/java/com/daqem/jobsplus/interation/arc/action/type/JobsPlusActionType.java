package com.daqem.jobsplus.interation.arc.action.type;

import com.daqem.arc.api.action.IAction;
import com.daqem.arc.api.action.type.ActionType;
import com.daqem.arc.registry.ArcRegistry;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.actions.job.JobExpAction;
import com.daqem.jobsplus.interation.arc.action.actions.job.JobLevelUpAction;

public interface JobsPlusActionType<T extends IAction> extends ActionType<T> {

    ActionType<JobExpAction> JOB_EXP = ActionType.register(JobsPlus.getId("on_job_exp"));
    ActionType<JobLevelUpAction> JOB_LEVEL_UP = ActionType.register(JobsPlus.getId("on_job_level_up"));

    static void init() {
    }
}
