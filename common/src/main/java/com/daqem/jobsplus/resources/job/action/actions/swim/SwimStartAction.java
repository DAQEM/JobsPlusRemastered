package com.daqem.jobsplus.resources.job.action.actions.swim;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SwimStartAction extends Action {

    public SwimStartAction() {
        super(Actions.SWIM_START);
    }

    @Override
    public String toString() {
        return "SwimStartAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SwimStartAction> {

        @Override
        public SwimStartAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SwimStartAction();
        }
    }
}
