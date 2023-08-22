package com.daqem.jobsplus.interation.arc.reward.rewards.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.action.result.ActionResult;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.arc.api.reward.AbstractReward;
import com.daqem.arc.api.reward.IReward;
import com.daqem.arc.api.reward.serializer.IRewardSerializer;
import com.daqem.arc.api.reward.serializer.RewardSerializer;
import com.daqem.arc.api.reward.type.IRewardType;
import com.daqem.jobsplus.interation.arc.action.data.type.JobsPlusActionDataType;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.interation.arc.reward.serializer.JobsPlusRewardSerializer;
import com.daqem.jobsplus.interation.arc.reward.type.JobsPlusRewardType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class JobCoinReward extends AbstractReward {

    private final int amount;

    public JobCoinReward(double chance, int priority, int amount) {
        super(chance, priority);
        this.amount = amount;
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
        ArcPlayer player = actionData.getPlayer();
        if (player instanceof JobsServerPlayer jobsServerPlayer) {
            jobsServerPlayer.jobsplus$addCoins(this.amount);
        }
        return new ActionResult();
    }

    public static class Serializer implements RewardSerializer<JobCoinReward> {

        @Override
        public JobCoinReward fromJson(JsonObject jsonObject, double chance, int priority) {
            return new JobCoinReward(
                    chance,
                    priority,
                    GsonHelper.getAsInt(jsonObject, "amount"));
        }

        @Override
        public JobCoinReward fromNetwork(FriendlyByteBuf friendlyByteBuf, double chance, int priority) {
            return new JobCoinReward(
                    chance,
                    priority,
                    friendlyByteBuf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobCoinReward type) {
            RewardSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeInt(type.amount);
        }
    }
}
