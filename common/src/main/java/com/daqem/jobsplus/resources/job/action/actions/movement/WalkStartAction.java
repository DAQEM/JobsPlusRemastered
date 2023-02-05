package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class WalkStartAction extends Action {

    public WalkStartAction() {
        super(Actions.WALK_START);
    }

    @Override
    public String toString() {
        return "WalkStartAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<WalkStartAction> {

        @Override
        public WalkStartAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new WalkStartAction();
        }
    }
}
