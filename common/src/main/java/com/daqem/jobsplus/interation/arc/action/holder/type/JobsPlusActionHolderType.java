package com.daqem.jobsplus.interation.arc.action.holder.type;

import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.action.holder.type.ActionHolderType;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;

public interface JobsPlusActionHolderType<T extends IActionHolder> extends ActionHolderType<T> {

    ActionHolderType<JobInstance> JOB_INSTANCE = ActionHolderType.register(JobsPlus.getId("job"));
    ActionHolderType<PowerupInstance> POWERUP_INSTANCE = ActionHolderType.register(JobsPlus.getId("powerup"));

    static void init() {
    }
}
