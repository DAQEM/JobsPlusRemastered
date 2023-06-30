package com.daqem.jobsplus.resources.job.action.reward.rewards.job;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class JobExpActionReward extends ActionReward {

    private final int minExp;
    private final int maxExp;

    public JobExpActionReward(int minExp, int maxExp) {
        super(ActionRewards.JOB_EXP);
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    @Override
    public String toString() {
        return "JobExpActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", min_exp=" + minExp +
                ", max_exp=" + maxExp +
                '}';
    }

    @Override
    public boolean apply(ActionData actionData) {
        JobsServerPlayer player = actionData.getPlayer();
        int exp = ((ServerPlayer) player).getRandom().nextInt(minExp, maxExp + 1);
        actionData.getSourceJob().addExperience(exp);
        return false;
    }

    public static class Deserializer implements JsonDeserializer<JobExpActionReward> {

        @Override
        public JobExpActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int minExp = GsonHelper.getAsInt(jsonObject, "min_exp");
            int maxExp = GsonHelper.getAsInt(jsonObject, "max_exp");
            if (minExp > maxExp) {
                throw new JsonParseException("min_exp cannot be greater than max_exp for ExpActionReward.");
            }
            return new JobExpActionReward(
                    minExp,
                    maxExp);
        }
    }
}
