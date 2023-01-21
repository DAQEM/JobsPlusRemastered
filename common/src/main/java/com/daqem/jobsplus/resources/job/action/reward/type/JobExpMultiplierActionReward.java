package com.daqem.jobsplus.resources.job.action.reward.type;

import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class JobExpMultiplierActionReward extends ActionReward {

    private final double multiplier;

    public JobExpMultiplierActionReward(double chance, double multiplier) {
        super(ActionRewards.JOB_EXP_MULTIPLIER, chance);
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return "JobExpMultiplierActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", multiplier=" + multiplier +
                '}';
    }

    public static class JobExpMultiplierActionRewardSerializer implements JsonDeserializer<JobExpMultiplierActionReward> {

        @Override
        public JobExpMultiplierActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new JobExpMultiplierActionReward(
                    json.getAsJsonObject().get("chance").getAsDouble()
                    , json.getAsJsonObject().get("multiplier").getAsDouble());
        }
    }
}
