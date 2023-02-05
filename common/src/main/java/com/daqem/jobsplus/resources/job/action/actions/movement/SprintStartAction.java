package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SprintStartAction extends Action {

    public SprintStartAction() {
        super(Actions.SPRINT_START);
    }

    @Override
    public String toString() {
        return "SprintStartAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SprintStartAction> {

        @Override
        public SprintStartAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SprintStartAction();
        }
    }
}
