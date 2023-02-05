package com.daqem.jobsplus.resources.job.action.reward;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.reward.rewards.*;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ActionRewards {

    public static final List<ActionRewardType> ACTION_REWARD_TYPES = new ArrayList<>();

    public static final ActionRewardType JOB_EXP = register(JobsPlus.getId("job_exp"), JobExpActionReward.class, new JobExpActionReward.Serializer());
    public static final ActionRewardType EXP = register(JobsPlus.getId("exp"), ExpActionReward.class, new ExpActionReward.Serializer());
    public static final ActionRewardType ITEM = register(JobsPlus.getId("item"), ItemActionReward.class, new ItemActionReward.Serializer());
    public static final ActionRewardType POTION_EFFECT = register(JobsPlus.getId("potion_effect"), PotionEffectActionReward.class, new PotionEffectActionReward.Serializer());
    public static final ActionRewardType JOB_EXP_MULTIPLIER = register(JobsPlus.getId("job_exp_multiplier"), JobExpMultiplierActionReward.class, new JobExpMultiplierActionReward.Serializer());

    private static ActionRewardType register(ResourceLocation location, Class<? extends ActionReward> clazz, JsonDeserializer<? extends ActionReward> deserializer) {
        ActionRewardType actionRewardType = new ActionRewardType(clazz, location, deserializer);
        ACTION_REWARD_TYPES.add(actionRewardType);
        return Registry.register(JobsPlusRegistry.ACTION_REWARD, location, actionRewardType);
    }

    public static Class<? extends ActionReward> getClass(ResourceLocation location) {
        ActionRewardType actionRewardType = JobsPlusRegistry.ACTION_REWARD.get(location);
        if (actionRewardType == null) {
            JobManager.LOGGER.error("Unknown action reward type: {}", location.toString());
        }
        return actionRewardType.clazz();
    }
}
