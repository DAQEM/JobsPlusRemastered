package com.daqem.jobsplus.registry;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestrictionType;
import com.daqem.jobsplus.resources.job.action.ActionType;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditionType;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewardType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class JobsPlusRegistry {

    public static final ResourceKey<Registry<ActionType>> ACTION_KEY = ResourceKey.createRegistryKey(JobsPlus.getId("action"));
    public static final ResourceKey<Registry<ActionRewardType>> ACTION_REWARD_KEY = ResourceKey.createRegistryKey(JobsPlus.getId("action_reward"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(JobsPlus.getId("action_condition"));
    public static final ResourceKey<Registry<CraftingRestrictionType>> CRAFTING_RESTRICTION_KEY = ResourceKey.createRegistryKey(JobsPlus.getId("crafting_restriction"));

    public static Registry<ActionType> ACTION;
    public static Registry<ActionRewardType> ACTION_REWARD;
    public static Registry<ActionConditionType> ACTION_CONDITION;
    public static Registry<CraftingRestrictionType> CRAFTING_RESTRICTION;
}
