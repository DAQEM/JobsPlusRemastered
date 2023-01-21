package com.daqem.jobsplus.resources.job.action.reward.type;

import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ExpActionReward extends ActionReward {

    private final int minExp;
    private final int maxExp;

    public ExpActionReward(double chance, int minExp, int maxExp) {
        super(ActionRewards.EXP, chance);
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    @Override
    public String toString() {
        return "ExpActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", min_exp=" + minExp +
                ", max_exp=" + maxExp +
                '}';
    }

    public static class ExpActionRewardSerializer implements JsonDeserializer<ExpActionReward> {

        @Override
        public ExpActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ExpActionReward(
                    jsonObject.get("chance").getAsDouble(),
                    jsonObject.get("min_exp").getAsInt(),
                    jsonObject.get("max_exp").getAsInt());
        }
    }
}
