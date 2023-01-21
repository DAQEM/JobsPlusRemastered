package com.daqem.jobsplus.resources.job.action.condition.type;

import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JobLevelActionCondition extends ActionCondition {

    private final boolean allJobs;
    private final List<ResourceLocation> jobs;
    private final int minLevel;
    private final int maxLevel;

    public JobLevelActionCondition(boolean allJobs, List<ResourceLocation> jobs, int minLevel, int maxLevel) {
        super(ActionConditions.JOB_LEVEL);
        this.allJobs = allJobs;
        this.jobs = jobs;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public String toString() {
        return "JobLevelActionCondition{" +
                "type=" + this.getType() +
                ", all_jobs=" + allJobs +
                ", jobs=" + jobs +
                ", minLevel=" + minLevel +
                ", maxLevel=" + maxLevel +
                '}';
    }

    public static class JobLevelActionConditionSerializer implements JsonDeserializer<JobLevelActionCondition> {

        @Override
        public JobLevelActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            List<ResourceLocation> jobs = new ArrayList<>();
            json.getAsJsonObject().get("jobs").getAsJsonArray().forEach(job -> jobs.add(new ResourceLocation(job.getAsString())));

            return new JobLevelActionCondition(
                    json.getAsJsonObject().get("all_jobs").getAsBoolean(),
                    jobs,
                    json.getAsJsonObject().get("min_level").getAsInt(),
                    json.getAsJsonObject().get("max_level").getAsInt());
        }
    }
}
