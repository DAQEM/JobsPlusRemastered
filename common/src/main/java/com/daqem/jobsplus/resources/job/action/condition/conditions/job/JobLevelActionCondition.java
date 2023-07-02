package com.daqem.jobsplus.resources.job.action.condition.conditions.job;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class JobLevelActionCondition extends ActionCondition {

    private final int minLevel;
    private final int maxLevel;

    public JobLevelActionCondition(int minLevel, int maxLevel) {
        super(ActionConditions.JOB_LEVEL);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Job job = actionData.getSourceJob();
        return job != null
                && job.getLevel() >= this.minLevel
                && job.getLevel() <= this.maxLevel;
    }

    @Override
    public String toString() {
        return "JobLevelActionCondition{" +
                "type=" + this.getType() +
                ", minLevel=" + minLevel +
                ", maxLevel=" + maxLevel +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<JobLevelActionCondition> {

        @Override
        public JobLevelActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int minLevel = jsonObject.has("min_level") ? jsonObject.get("min_level").getAsInt() : 0;
            int maxLevel = jsonObject.has("max_level") ? jsonObject.get("max_level").getAsInt() : Integer.MAX_VALUE;
            if (minLevel > maxLevel) {
                throw new JsonParseException("min_level cannot be greater than max_level for JobLevelActionCondition.");
            }
            return new JobLevelActionCondition(
                    minLevel,
                    maxLevel);
        }
    }
}
