package com.daqem.jobsplus.integration.arc.reward.rewards.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.result.ActionResult;
import com.daqem.arc.api.reward.AbstractReward;
import com.daqem.arc.api.reward.IReward;
import com.daqem.arc.api.reward.serializer.IRewardSerializer;
import com.daqem.arc.api.reward.serializer.RewardSerializer;
import com.daqem.arc.api.reward.type.IRewardType;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.reward.serializer.JobsPlusRewardSerializer;
import com.daqem.jobsplus.integration.arc.reward.type.JobsPlusRewardType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class JobExpReward extends AbstractReward {

    private final int min;
    private final int max;

    public JobExpReward(double chance, int priority, int min, int max) {
        super(chance, priority);
        this.min = min;
        this.max = max;

        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max for JobExpActionReward.");
        }
    }

    @Override
    public IRewardType<?> getType() {
        return JobsPlusRewardType.JOB_EXP;
    }

    @Override
    public IRewardSerializer<? extends IReward> getSerializer() {
        return JobsPlusRewardSerializer.JOB_EXP;
    }

    @Override
    public ActionResult apply(ActionData actionData) {
        if (actionData.getSourceActionHolder() instanceof JobInstance jobInstance) {
            if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
                Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
                if (job != null) {
                    int exp = actionData.getPlayer().arc$getPlayer().getRandom().nextInt(min, max + 1);
                    job.addExperience(exp);
                }
            }
        }
        return new ActionResult();
    }

    public static class Serializer implements RewardSerializer<JobExpReward> {
        @Override
        public JobExpReward fromJson(JsonObject jsonObject, double chance, int priority) {
            return new JobExpReward(
                    chance,
                    priority,
                    GsonHelper.getAsInt(jsonObject, "min"),
                    GsonHelper.getAsInt(jsonObject, "max"));
        }

        @Override
        public JobExpReward fromNetwork(FriendlyByteBuf friendlyByteBuf, double chance, int priority) {
            return new JobExpReward(
                    chance,
                    priority,
                    friendlyByteBuf.readInt(),
                    friendlyByteBuf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobExpReward type) {
            RewardSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeInt(type.min);
            friendlyByteBuf.writeInt(type.max);
        }
    }
}
