package com.daqem.jobsplus.resources.job.action.actions.job;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class JobLevelUpAction extends Action {

    public JobLevelUpAction() {
        super(Actions.JOB_LEVEL_UP);
    }

    @Override
    public String toString() {
        return "JobLevelUpAction{" +
                "type=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<JobLevelUpAction> {

        @Override
        public JobLevelUpAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new JobLevelUpAction();
        }
    }
}
