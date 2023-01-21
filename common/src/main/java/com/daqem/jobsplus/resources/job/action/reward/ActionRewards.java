package com.daqem.jobsplus.resources.job.action.reward;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.reward.type.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ActionRewards {

    public static ActionRewardType JOB_EXP = register(JobsPlus.getId("job_exp"), JobExpActionReward.class);
    public static ActionRewardType EXP = register(JobsPlus.getId("exp"), ExpActionReward.class);
    public static ActionRewardType ITEM = register(JobsPlus.getId("item"), ItemActionReward.class);
    public static ActionRewardType GIVE_EFFECT = register(JobsPlus.getId("give_effect"), GiveEffectActionReward.class);
    public static ActionRewardType JOB_EXP_MULTIPLIER = register(JobsPlus.getId("job_exp_multiplier"), JobExpMultiplierActionReward.class);

    private static ActionRewardType register(ResourceLocation location, Class<? extends ActionReward> clazz) {
        return Registry.register(JobsPlusRegistry.ACTION_REWARD, location, new ActionRewardType(clazz, location));
    }

    public static Class<? extends ActionReward> getClass(ResourceLocation location) {
        ActionRewardType actionRewardType = JobsPlusRegistry.ACTION_REWARD.get(location);
        if (actionRewardType == null) {
            JobManager.LOGGER.error("Unknown action reward type: {}", location.toString());
        }
        return actionRewardType.clazz();
    }
}
