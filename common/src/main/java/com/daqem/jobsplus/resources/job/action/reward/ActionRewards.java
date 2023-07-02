package com.daqem.jobsplus.resources.job.action.reward;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.exception.UnknownRewardTypeException;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.job.action.reward.rewards.CancelActionActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.block.DestroySpeedMultiplierActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.effect.EffectActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.effect.EffectAmplifierAdditionActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.effect.EffectDurationMultiplierActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.effect.RemoveEffectActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.experience.ExpActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.item.ItemActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.job.JobExpActionReward;
import com.daqem.jobsplus.resources.job.action.reward.rewards.job.JobExpMultiplierActionReward;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ActionRewards {

    public static final List<ActionRewardType> ACTION_REWARD_TYPES = new ArrayList<>();

    public static final ActionRewardType JOB_EXP = register(JobsPlus.getId("job_exp"), JobExpActionReward.class, new JobExpActionReward.Deserializer());
    public static final ActionRewardType EXP = register(JobsPlus.getId("exp"), ExpActionReward.class, new ExpActionReward.Deserializer());
    public static final ActionRewardType ITEM = register(JobsPlus.getId("item"), ItemActionReward.class, new ItemActionReward.Deserializer());
    public static final ActionRewardType EFFECT = register(JobsPlus.getId("effect"), EffectActionReward.class, new EffectActionReward.Deserializer());
    public static final ActionRewardType JOB_EXP_MULTIPLIER = register(JobsPlus.getId("job_exp_multiplier"), JobExpMultiplierActionReward.class, new JobExpMultiplierActionReward.Deserializer());
    public static final ActionRewardType EFFECT_DURATION_MULTIPLIER = register(JobsPlus.getId("effect_duration_multiplier"), EffectDurationMultiplierActionReward.class, new EffectDurationMultiplierActionReward.Deserializer());
    public static final ActionRewardType EFFECT_AMPLIFIER_ADDITION = register(JobsPlus.getId("effect_amplifier_addition"), EffectAmplifierAdditionActionReward.class, new EffectAmplifierAdditionActionReward.Deserializer());
    public static final ActionRewardType REMOVE_EFFECT = register(JobsPlus.getId("remove_effect"), RemoveEffectActionReward.class, new RemoveEffectActionReward.Deserializer());
    public static final ActionRewardType CANCEL_ACTION = register(JobsPlus.getId("cancel_action"), CancelActionActionReward.class, new CancelActionActionReward.Deserializer());
    public static final ActionRewardType DESTROY_SPEED_MULTIPLIER = register(JobsPlus.getId("destroy_speed_multiplier"), DestroySpeedMultiplierActionReward.class, new DestroySpeedMultiplierActionReward.Deserializer());

    private static ActionRewardType register(ResourceLocation location, Class<? extends ActionReward> clazz, JsonDeserializer<? extends ActionReward> deserializer) {
        ActionRewardType actionRewardType = new ActionRewardType(clazz, location, deserializer);
        ACTION_REWARD_TYPES.add(actionRewardType);
        return Registry.register(JobsPlusRegistry.ACTION_REWARD, location, actionRewardType);
    }

    public static Class<? extends ActionReward> getClass(ResourceLocation location) throws UnknownRewardTypeException {
        ActionRewardType actionRewardType = JobsPlusRegistry.ACTION_REWARD.get(location);
        if (actionRewardType == null) {
            throw new UnknownRewardTypeException(location);
        }
        return actionRewardType.clazz();
    }
}
