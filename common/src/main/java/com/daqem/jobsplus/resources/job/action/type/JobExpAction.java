package com.daqem.jobsplus.resources.job.action.type;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JobExpAction extends Action {

    private final boolean allJobs;
    private final List<ResourceLocation> jobs;

    public JobExpAction(boolean allJobs, List<ResourceLocation> jobs) {
        super(Actions.JOB_EXP);
        this.allJobs = allJobs;
        this.jobs = jobs;
    }

    @Override
    public String toString() {
        return "JobExpAction{" +
                "type=" + this.getType() +
                ", all_jobs=" + allJobs +
                ", jobs=" + jobs +
                '}';
    }

    public static class JobExpActionSerializer implements JsonDeserializer<JobExpAction> {

        @Override
        public JobExpAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            List<ResourceLocation> jobs = new ArrayList<>();
            json.getAsJsonObject().get("jobs").getAsJsonArray().forEach(job -> jobs.add(new ResourceLocation(job.getAsString())));

            return new JobExpAction(
                    json.getAsJsonObject().get("all_jobs").getAsBoolean(),
                    jobs);
        }
    }
}
