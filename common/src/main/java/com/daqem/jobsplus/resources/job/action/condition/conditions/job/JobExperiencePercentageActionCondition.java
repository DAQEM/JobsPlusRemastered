package com.daqem.jobsplus.resources.job.action.condition.conditions.job;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class JobExperiencePercentageActionCondition extends ActionCondition {

    private final double percentage;

    public JobExperiencePercentageActionCondition(double percentage) {
        super(ActionConditions.JOB_EXPERIENCE_PERCENTAGE);
        this.percentage = percentage;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        return actionData.getSourceJob().getExperiencePercentage() >= percentage;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class Deserializer implements JsonDeserializer<JobExperiencePercentageActionCondition> {

        @Override
        public JobExperiencePercentageActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            double percentage;
            if (jsonObject.has("percentage")) {
                percentage = jsonObject.get("percentage").getAsDouble();
            } else {
                throw new JsonParseException("Missing percentage, expected to find a double in JobExperiencePercentageActionCondition");
            }
            return new JobExperiencePercentageActionCondition(percentage);
        }
    }

}
