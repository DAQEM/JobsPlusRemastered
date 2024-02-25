package com.daqem.jobsplus.integration.arc.condition.type;

import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.type.ConditionType;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.HasJobCondition;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.JobExperiencePercentageCondition;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.JobLevelCondition;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.powerup.PowerupNoChildrenActiveCondition;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.powerup.PowerupNotActiveCondition;
import com.daqem.jobsplus.integration.arc.condition.conditions.powerup.HasPowerupActivatedCondition;

public interface JobsPlusConditionType<T extends ICondition> extends ConditionType<T> {

    ConditionType<JobExperiencePercentageCondition>  JOB_EXPERIENCE_PERCENTAGE = ConditionType.register(JobsPlus.getId("job_experience_percentage"));
    ConditionType<JobLevelCondition> JOB_LEVEL = ConditionType.register(JobsPlus.getId("job_level"));
    ConditionType<PowerupNoChildrenActiveCondition> POWERUP_NO_CHILDREN_ACTIVE = ConditionType.register(JobsPlus.getId("powerup_no_children_active"));
    ConditionType<PowerupNotActiveCondition> POWERUP_NOT_ACTIVE = ConditionType.register(JobsPlus.getId("powerup_not_active"));
    ConditionType<HasJobCondition> HAS_JOB = ConditionType.register(JobsPlus.getId("has_job"));

    ConditionType<HasPowerupActivatedCondition> HAS_POWERUP_ACTIVATED = ConditionType.register(JobsPlus.getId("has_powerup_activated"));

    static void init() {
    }
}
