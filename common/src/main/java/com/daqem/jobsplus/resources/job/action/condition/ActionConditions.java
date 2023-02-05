package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BannedBlocksActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BlocksActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop.CropAgeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop.CropFullyGrownActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.JobLevelActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.swim.SwimmingDistanceActionCondition;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ActionConditions {

    public static final List<ActionConditionType> ACTION_CONDITION_TYPES = new ArrayList<>();

    public static final ActionConditionType CROP_FULLY_GROWN = register(JobsPlus.getId("crop_fully_grown"), CropFullyGrownActionCondition.class, new CropFullyGrownActionCondition.Serializer());
    public static final ActionConditionType CROP_AGE = register(JobsPlus.getId("crop_age"), CropAgeActionCondition.class, new CropAgeActionCondition.Serializer());
    public static final ActionConditionType JOB_LEVEL = register(JobsPlus.getId("job_level"), JobLevelActionCondition.class, new JobLevelActionCondition.Serializer());
    public static final ActionConditionType BLOCKS = register(JobsPlus.getId("blocks"), BlocksActionCondition.class, new BlocksActionCondition.Serializer());
    public static final ActionConditionType BANNED_BLOCKS = register(JobsPlus.getId("banned_blocks"), BannedBlocksActionCondition.class, new BannedBlocksActionCondition.Serializer());
    public static final ActionConditionType SWIMMING_DISTANCE = register(JobsPlus.getId("swimming_distance"), SwimmingDistanceActionCondition.class, new SwimmingDistanceActionCondition.Serializer());

    private static ActionConditionType register(ResourceLocation location, Class<? extends ActionCondition> clazz, JsonDeserializer<? extends ActionCondition> deserializer) {
        ActionConditionType actionConditionType = new ActionConditionType(clazz, location, deserializer);
        ACTION_CONDITION_TYPES.add(actionConditionType);
        return Registry.register(JobsPlusRegistry.ACTION_CONDITION, location, actionConditionType);
    }

    public static Class<? extends ActionCondition> getClass(ResourceLocation location) {
        ActionConditionType actionConditionType = JobsPlusRegistry.ACTION_CONDITION.get(location);
        if (actionConditionType == null)
            JobManager.LOGGER.error("Unknown action condition type: {}", location.toString());
        return actionConditionType.clazz();
    }
}
