package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SprintStopAction extends Action {

    public SprintStopAction() {
        super(Actions.SPRINT_STOP);
    }

    @Override
    public String toString() {
        return "SprintStopAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SprintStopAction> {

        @Override
        public SprintStopAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SprintStopAction();
        }
    }
}
