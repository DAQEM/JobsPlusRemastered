package com.daqem.jobsplus.integration.arc.reward.rewards.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.action.result.ActionResult;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.arc.api.reward.AbstractReward;
import com.daqem.arc.api.reward.IReward;
import com.daqem.arc.api.reward.serializer.IRewardSerializer;
import com.daqem.arc.api.reward.serializer.RewardSerializer;
import com.daqem.arc.api.reward.type.IRewardType;
import com.daqem.jobsplus.integration.arc.data.type.JobsPlusActionDataType;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.integration.arc.reward.serializer.JobsPlusRewardSerializer;
import com.daqem.jobsplus.integration.arc.reward.type.JobsPlusRewardType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class JobExpMultiplierReward extends AbstractReward {

    private final ResourceLocation jobLocation;
    private final double multiplier;

    public JobExpMultiplierReward(double chance, int priority, ResourceLocation jobLocation, double multiplier) {
        super(chance, priority);
        this.jobLocation = jobLocation;
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
        IActionHolder sourceActionHolder = actionData.getSourceActionHolder();
        ArcPlayer player = actionData.getPlayer();
        if (player instanceof JobsServerPlayer jobsServerPlayer) {
            Job job = null;
            if (sourceActionHolder instanceof JobInstance jobInstance) {
                if (!jobInstance.getLocation().equals(jobLocation)) return new ActionResult();
                job = jobsServerPlayer.jobsplus$getJob(jobInstance);
            } else if (sourceActionHolder instanceof PowerupInstance powerupInstance) {
                if (!powerupInstance.getJobLocation().equals(jobLocation)) return new ActionResult();
                job = jobsServerPlayer.jobsplus$getJob(JobInstance.of(powerupInstance.getJobLocation()));
            }
            if (job != null) {
                Integer exp = actionData.getData(JobsPlusActionDataType.JOB_EXP);
                if (exp != null) {
                    int experience = (int) (exp * this.multiplier) - exp;
                    job.addExperienceWithoutEvent(experience);
                }
            }
        }
        return new ActionResult();
    }

    public static class Serializer implements RewardSerializer<JobExpMultiplierReward> {

        @Override
        public JobExpMultiplierReward fromJson(JsonObject jsonObject, double chance, int priority) {
            return new JobExpMultiplierReward(
                    chance,
                    priority,
                    new ResourceLocation(GsonHelper.getAsString(jsonObject, "job")),
                    GsonHelper.getAsDouble(jsonObject, "multiplier"));
        }

        @Override
        public JobExpMultiplierReward fromNetwork(FriendlyByteBuf friendlyByteBuf, double chance, int priority) {
            return new JobExpMultiplierReward(
                    chance,
                    priority,
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobExpMultiplierReward type) {
            RewardSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeResourceLocation(type.jobLocation);
            friendlyByteBuf.writeDouble(type.multiplier);
        }
    }
}
