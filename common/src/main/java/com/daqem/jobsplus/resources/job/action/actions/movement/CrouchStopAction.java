package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CrouchStopAction extends Action {

    public CrouchStopAction() {
        super(Actions.CROUCH_STOP);
    }

    @Override
    public String toString() {
        return "CrouchStopAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<CrouchStopAction> {

        @Override
        public CrouchStopAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new CrouchStopAction();
        }
    }
}
