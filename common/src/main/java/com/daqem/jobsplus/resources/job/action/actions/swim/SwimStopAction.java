package com.daqem.jobsplus.resources.job.action.actions.swim;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SwimStopAction extends Action {

    public SwimStopAction() {
        super(Actions.SWIM_STOP);
    }

    @Override
    public String toString() {
        return "SwimStopAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SwimStopAction> {

        @Override
        public SwimStopAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SwimStopAction();
        }
    }
}
