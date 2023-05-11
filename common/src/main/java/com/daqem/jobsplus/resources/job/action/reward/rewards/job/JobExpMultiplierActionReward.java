package com.daqem.jobsplus.resources.job.action.reward.rewards.job;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class JobExpMultiplierActionReward extends ActionReward {

    private final double multiplier;

    public JobExpMultiplierActionReward(double multiplier) {
        super(ActionRewards.JOB_EXP_MULTIPLIER);
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

    @Override
    public void apply(ActionData actionData) {
        Integer specification = actionData.getSpecification(ActionSpecification.JOB_EXP);
        if (specification != null) {
            int exp = specification;
            int experience = (int) (exp * this.multiplier) - exp;
            actionData.getSourceJob().addExperienceWithoutEvent(experience);
        }
    }

    public static class Deserializer implements JsonDeserializer<JobExpMultiplierActionReward> {

        @Override
        public JobExpMultiplierActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new JobExpMultiplierActionReward(
                    GsonHelper.getAsDouble(jsonObject, "multiplier", 1));
        }
    }
}
