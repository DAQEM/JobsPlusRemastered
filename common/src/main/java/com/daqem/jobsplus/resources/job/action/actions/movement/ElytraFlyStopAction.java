package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ElytraFlyStopAction extends Action {

    public ElytraFlyStopAction() {
        super(Actions.ELYTRA_FLY_STOP);
    }

    @Override
    public String toString() {
        return "ElytraFlyStopAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<ElytraFlyStopAction> {

        @Override
        public ElytraFlyStopAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ElytraFlyStopAction();
        }
    }
}
