package com.daqem.jobsplus.resources.job.action.reward.type;

import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;

import java.lang.reflect.Type;

public class JobExpActionReward extends ActionReward {

    private final int minExp;
    private final int maxExp;

    public JobExpActionReward(double chance, int minExp, int maxExp) {
        super(ActionRewards.JOB_EXP, chance);
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

    public static class JobExpActionRewardSerializer implements JsonDeserializer<JobExpActionReward> {

        @Override
        public JobExpActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new JobExpActionReward(
                    jsonObject.get("chance").getAsDouble(),
                    jsonObject.get("min_exp").getAsInt(),
                    jsonObject.get("max_exp").getAsInt());
        }
    }
}
