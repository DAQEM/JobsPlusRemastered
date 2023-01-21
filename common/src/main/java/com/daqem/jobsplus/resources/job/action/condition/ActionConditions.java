package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.condition.type.CropAgeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.type.JobLevelActionCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ActionConditions {

    public static ActionConditionType CROP_AGE = register(JobsPlus.getId("crop_age"), CropAgeActionCondition.class);
    public static ActionConditionType JOB_LEVEL = register(JobsPlus.getId("job_level"), JobLevelActionCondition.class);


    private static ActionConditionType register(ResourceLocation location, Class<? extends ActionCondition> clazz) {
        return Registry.register(JobsPlusRegistry.ACTION_CONDITION, location, new ActionConditionType(clazz, location));
    }

    public static Class<? extends ActionCondition> getClass(ResourceLocation location) {
        ActionConditionType actionConditionType = JobsPlusRegistry.ACTION_CONDITION.get(location);
        if (actionConditionType == null)
            JobManager.LOGGER.error("Unknown action condition type: {}", location.toString());
        return actionConditionType.clazz();
    }
}
