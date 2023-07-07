package com.daqem.jobsplus.interation.arc.condition.serializer;

import com.daqem.arc.api.action.type.ActionType;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.registry.ArcRegistry;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.condition.conditions.job.JobExperiencePercentageCondition;
import com.daqem.jobsplus.interation.arc.condition.conditions.job.JobLevelCondition;
import com.daqem.jobsplus.interation.arc.condition.conditions.job.powerup.PowerupNoChildrenActiveCondition;
import com.daqem.jobsplus.interation.arc.condition.conditions.job.powerup.PowerupNotActiveCondition;

public interface JobsPlusConditionSerializer<T extends ICondition> extends ConditionSerializer<T> {

    IConditionSerializer<JobExperiencePercentageCondition> JOB_EXPERIENCE_PERCENTAGE = ConditionSerializer.register(JobsPlus.getId("job_experience_percentage"), new JobExperiencePercentageCondition.Serializer());
    IConditionSerializer<JobLevelCondition> JOB_LEVEL = ConditionSerializer.register(JobsPlus.getId("job_level"), new JobLevelCondition.Serializer());
    IConditionSerializer<PowerupNoChildrenActiveCondition> POWERUP_NO_CHILDREN_ACTIVE = ConditionSerializer.register(JobsPlus.getId("powerup_no_children_active"), new PowerupNoChildrenActiveCondition.Serializer());
    IConditionSerializer<PowerupNotActiveCondition> POWERUP_NOT_ACTIVE = ConditionSerializer.register(JobsPlus.getId("powerup_not_active"), new PowerupNotActiveCondition.Serializer());

    static void init() {
        JobsPlus.LOGGER.info("Registered Arc Condition Serializers");
        JobsPlus.LOGGER.info(ArcRegistry.ACTION.getOptional(ActionType.CRAFT_ITEM.getLocation()).get().getLocation().toString());
    }
}
