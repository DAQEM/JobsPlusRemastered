package com.daqem.jobsplus.resources.job.action.actions.job;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class JobExpAction extends Action {

    public JobExpAction() {
        super(Actions.JOB_EXP);
    }

    @Override
    public String toString() {
        return "JobExpAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<JobExpAction> {

        @Override
        public JobExpAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new JobExpAction();
        }
    }
}
