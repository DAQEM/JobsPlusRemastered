package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.exception.UnknownConditionTypeException;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.job.action.condition.conditions.NotActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.OrActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BannedBlocksActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BlockActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.BlocksActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop.CropAgeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop.CropFullyGrownActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.ore.IsOreActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.properties.BlockHardnessActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.block.properties.BlockMaterialColorActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.effect.EffectActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.effect.EffectCategoryActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.entity.EntityTypeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.entity.EntityTypesActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.entity.ReadyForShearingActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.experience.ExpDropActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.experience.ExpLevelActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.item.*;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.JobExperiencePercentageActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.JobLevelActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.powerup.PowerupNoChildrenActiveActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.job.powerup.PowerupNotActiveActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.movement.DistanceActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.recipe.IsBlastingRecipeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.conditions.recipe.IsSmokingRecipeActionCondition;
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
    public static final ActionConditionType NOT = register(JobsPlus.getId("not"), NotActionCondition.class, new NotActionCondition.Deserializer());

    public static final ActionConditionType CROP_FULLY_GROWN = register(JobsPlus.getId("crop_fully_grown"), CropFullyGrownActionCondition.class, new CropFullyGrownActionCondition.Deserializer());
    public static final ActionConditionType CROP_AGE = register(JobsPlus.getId("crop_age"), CropAgeActionCondition.class, new CropAgeActionCondition.Deserializer());
    public static final ActionConditionType JOB_LEVEL = register(JobsPlus.getId("job_level"), JobLevelActionCondition.class, new JobLevelActionCondition.Deserializer());
    public static final ActionConditionType BLOCK = register(JobsPlus.getId("block"), BlockActionCondition.class, new BlockActionCondition.Deserializer());
    public static final ActionConditionType BLOCKS = register(JobsPlus.getId("blocks"), BlocksActionCondition.class, new BlocksActionCondition.Deserializer());
    public static final ActionConditionType BANNED_BLOCKS = register(JobsPlus.getId("banned_blocks"), BannedBlocksActionCondition.class, new BannedBlocksActionCondition.Deserializer());
    public static final ActionConditionType DISTANCE = register(JobsPlus.getId("swimming_distance"), DistanceActionCondition.class, new DistanceActionCondition.Deserializer());
    public static final ActionConditionType JOB_EXPERIENCE_PERCENTAGE = register(JobsPlus.getId("job_experience_percentage"), JobExperiencePercentageActionCondition.class, new JobExperiencePercentageActionCondition.Deserializer());
    public static final ActionConditionType ENTITY_TYPE = register(JobsPlus.getId("entity_type"), EntityTypeActionCondition.class, new EntityTypeActionCondition.Deserializer());
    public static final ActionConditionType ENTITY_TYPES = register(JobsPlus.getId("entity_types"), EntityTypesActionCondition.class, new EntityTypesActionCondition.Deserializer());
    public static final ActionConditionType DIMENSION = register(JobsPlus.getId("dimension"), DimensionActionCondition.class, new DimensionActionCondition.Deserializer());
    public static final ActionConditionType SCOREBOARD = register(JobsPlus.getId("scoreboard"), ScoreboardActionCondition.class, new ScoreboardActionCondition.Deserializer());
    public static final ActionConditionType TEAM = register(JobsPlus.getId("team"), TeamActionCondition.class, new TeamActionCondition.Deserializer());
    public static final ActionConditionType ITEM_IN_HAND = register(JobsPlus.getId("item_in_hand"), ItemInHandActionCondition.class, new ItemInHandActionCondition.Deserializer());
    public static final ActionConditionType ITEM_IN_INVENTORY = register(JobsPlus.getId("item_in_inventory"), ItemInInventoryActionCondition.class, new ItemInInventoryActionCondition.Deserializer());
    public static final ActionConditionType ITEM_EQUIPPED = register(JobsPlus.getId("item_equipped"), ItemEquippedActionCondition.class, new ItemEquippedActionCondition.Deserializer());
    public static final ActionConditionType ITEM = register(JobsPlus.getId("item"), ItemActionCondition.class, new ItemActionCondition.Deserializer());
    public static final ActionConditionType ITEMS = register(JobsPlus.getId("items"), ItemsActionCondition.class, new ItemsActionCondition.Deserializer());
    public static final ActionConditionType EXP_DROP = register(JobsPlus.getId("exp_drop"), ExpDropActionCondition.class, new ExpDropActionCondition.Deserializer());
    public static final ActionConditionType EXP_LEVEL = register(JobsPlus.getId("exp_level"), ExpLevelActionCondition.class, new ExpLevelActionCondition.Deserializer());
    public static final ActionConditionType READY_FOR_SHEARING = register(JobsPlus.getId("ready_for_shearing"), ReadyForShearingActionCondition.class, new ReadyForShearingActionCondition.Deserializer());
    public static final ActionConditionType BANNED_ITEMS = register(JobsPlus.getId("banned_items"), BannedItemsActionCondition.class, new BannedItemsActionCondition.Deserializer());
    public static final ActionConditionType IS_BLASTING_RECIPE = register(JobsPlus.getId("is_blasting_recipe"), IsBlastingRecipeActionCondition.class, new IsBlastingRecipeActionCondition.Deserializer());
    public static final ActionConditionType IS_SMOKING_RECIPE = register(JobsPlus.getId("is_smoking_recipe"), IsSmokingRecipeActionCondition.class, new IsSmokingRecipeActionCondition.Deserializer());
    public static final ActionConditionType IS_ORE = register(JobsPlus.getId("is_ore"), IsOreActionCondition.class, new IsOreActionCondition.Deserializer());
    public static final ActionConditionType POWERUP_NOT_ACTIVE = register(JobsPlus.getId("powerup_not_active"), PowerupNotActiveActionCondition.class, new PowerupNotActiveActionCondition.Deserializer());
    public static final ActionConditionType POWERUP_NO_CHILDREN_ACTIVE = register(JobsPlus.getId("powerup_no_children_active"), PowerupNoChildrenActiveActionCondition.class, new PowerupNoChildrenActiveActionCondition.Deserializer());
    public static final ActionConditionType EFFECT_CATEGORY = register(JobsPlus.getId("effect_category"), EffectCategoryActionCondition.class, new EffectCategoryActionCondition.Deserializer());
    public static final ActionConditionType EFFECT = register(JobsPlus.getId("effect"), EffectActionCondition.class, new EffectActionCondition.Deserializer());
    public static final ActionConditionType BLOCK_HARDNESS = register(JobsPlus.getId("block_hardness"), BlockHardnessActionCondition.class, new BlockHardnessActionCondition.Deserializer());
    public static final ActionConditionType BLOCK_MATERIAL_COLOR = register(JobsPlus.getId("block_material_color"), BlockMaterialColorActionCondition.class, new BlockMaterialColorActionCondition.Deserializer());

    private static ActionConditionType register(ResourceLocation location, Class<? extends ActionCondition> clazz, JsonDeserializer<? extends ActionCondition> deserializer) {
        ActionConditionType actionConditionType = new ActionConditionType(clazz, location, deserializer);
        ACTION_CONDITION_TYPES.add(actionConditionType);
        return Registry.register(JobsPlusRegistry.ACTION_CONDITION, location, actionConditionType);
    }

    public static Class<? extends ActionCondition> getClass(ResourceLocation location) throws UnknownConditionTypeException {
        ActionConditionType actionConditionType = JobsPlusRegistry.ACTION_CONDITION.get(location);
        if (actionConditionType == null)
            throw new UnknownConditionTypeException(location);
        return actionConditionType.clazz();
    }
}
