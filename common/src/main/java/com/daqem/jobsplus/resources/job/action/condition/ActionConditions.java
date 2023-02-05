package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.condition.conditions.OrActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BannedBlocksActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BlocksActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.ExpDropActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop.CropAgeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop.CropFullyGrownActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.entity.EntityTypeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.item.ItemEquippedActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.item.ItemInHandActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.item.ItemInInventoryActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.JobExperiencePercentageActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.JobLevelActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.movement.DistanceActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.scoreboard.ScoreboardActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.team.TeamActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.world.DimensionActionCondition;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ActionConditions {

    public static final List<ActionConditionType> ACTION_CONDITION_TYPES = new ArrayList<>();

    public static final ActionConditionType OR = register(JobsPlus.getId("or"), OrActionCondition.class, new OrActionCondition.Deserializer());

    public static final ActionConditionType CROP_FULLY_GROWN = register(JobsPlus.getId("crop_fully_grown"), CropFullyGrownActionCondition.class, new CropFullyGrownActionCondition.Deserializer());
    public static final ActionConditionType CROP_AGE = register(JobsPlus.getId("crop_age"), CropAgeActionCondition.class, new CropAgeActionCondition.Deserializer());
    public static final ActionConditionType JOB_LEVEL = register(JobsPlus.getId("job_level"), JobLevelActionCondition.class, new JobLevelActionCondition.Deserializer());
    public static final ActionConditionType BLOCKS = register(JobsPlus.getId("blocks"), BlocksActionCondition.class, new BlocksActionCondition.Deserializer());
    public static final ActionConditionType BANNED_BLOCKS = register(JobsPlus.getId("banned_blocks"), BannedBlocksActionCondition.class, new BannedBlocksActionCondition.Deserializer());
    public static final ActionConditionType DISTANCE = register(JobsPlus.getId("swimming_distance"), DistanceActionCondition.class, new DistanceActionCondition.Deserializer());
    public static final ActionConditionType JOB_EXPERIENCE_PERCENTAGE = register(JobsPlus.getId("job_experience_percentage"), JobExperiencePercentageActionCondition.class, new JobExperiencePercentageActionCondition.Deserializer());
    public static final ActionConditionType ENTITY_TYPE = register(JobsPlus.getId("entity_type"), EntityTypeActionCondition.class, new EntityTypeActionCondition.Deserializer());
    public static final ActionConditionType DIMENSION = register(JobsPlus.getId("dimension"), DimensionActionCondition.class, new DimensionActionCondition.Deserializer());
    public static final ActionConditionType EXP_DROP = register(JobsPlus.getId("exp_drop"), ExpDropActionCondition.class, new ExpDropActionCondition.Deserializer());
    public static final ActionConditionType SCOREBOARD = register(JobsPlus.getId("scoreboard"), ScoreboardActionCondition.class, new ScoreboardActionCondition.Deserializer());
    public static final ActionConditionType TEAM = register(JobsPlus.getId("team"), TeamActionCondition.class, new TeamActionCondition.Deserializer());
    public static final ActionConditionType ITEM_IN_HAND = register(JobsPlus.getId("item_in_hand"), ItemInHandActionCondition.class, new ItemInHandActionCondition.Deserializer());
    public static final ActionConditionType ITEM_IN_INVENTORY = register(JobsPlus.getId("item_in_inventory"), ItemInInventoryActionCondition.class, new ItemInInventoryActionCondition.Deserializer());
    public static final ActionConditionType ITEM_EQUIPPED = register(JobsPlus.getId("item_equipped"), ItemEquippedActionCondition.class, new ItemEquippedActionCondition.Deserializer());

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
