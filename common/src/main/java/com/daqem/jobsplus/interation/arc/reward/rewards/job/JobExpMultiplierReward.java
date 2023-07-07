package com.daqem.jobsplus.interation.arc.reward.rewards.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.result.ActionResult;
import com.daqem.arc.api.reward.AbstractReward;
import com.daqem.arc.api.reward.IReward;
import com.daqem.arc.api.reward.serializer.IRewardSerializer;
import com.daqem.arc.api.reward.serializer.RewardSerializer;
import com.daqem.arc.api.reward.type.IRewardType;
import com.daqem.jobsplus.interation.arc.action.data.type.JobsPlusActionDataType;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.reward.serializer.JobsPlusRewardSerializer;
import com.daqem.jobsplus.interation.arc.reward.type.JobsPlusRewardType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class JobExpMultiplierReward extends AbstractReward {

    private final double multiplier;

    public JobExpMultiplierReward(double chance, double multiplier) {
        super(chance);
        this.multiplier = multiplier;
    }

    @Override
    public IRewardType<?> getType() {
        return JobsPlusRewardType.JOB_EXP_MULTIPLIER;
    }

    @Override
    public IRewardSerializer<? extends IReward> getSerializer() {
        return JobsPlusRewardSerializer.JOB_EXP_MULTIPLIER;
    }

    @Override
    public ActionResult apply(ActionData actionData) {
        if (actionData.getSourceActionHolder() instanceof JobInstance jobInstance) {
            Integer specification = actionData.getData(JobsPlusActionDataType.JOB_EXP);
            if (specification != null) {
                if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
                    Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
                    if (job != null) {
                        int exp = specification;
                        int experience = (int) (exp * this.multiplier) - exp;
                        job.addExperienceWithoutEvent(experience);
                    }
                }
            }
        }
        return new ActionResult();
    }

    public static class Serializer implements RewardSerializer<JobExpMultiplierReward> {

        @Override
        public JobExpMultiplierReward fromJson(JsonObject jsonObject, double chance) {
            return new JobExpMultiplierReward(
                    chance,
                    GsonHelper.getAsDouble(jsonObject, "multiplier"));
        }

        @Override
        public JobExpMultiplierReward fromNetwork(FriendlyByteBuf friendlyByteBuf, double chance) {
            return new JobExpMultiplierReward(
                    chance,
                    friendlyByteBuf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobExpMultiplierReward type) {
            RewardSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeDouble(type.multiplier);
        }
    }
}
