package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SprintAction extends Action {

    public SprintAction() {
        super(Actions.SPRINT);
    }

    @Override
    public String toString() {
        return "SprintAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SprintAction> {

        @Override
        public SprintAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SprintAction();
        }
    }
}
