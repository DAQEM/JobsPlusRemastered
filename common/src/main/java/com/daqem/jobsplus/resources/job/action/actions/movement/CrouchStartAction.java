package com.daqem.jobsplus.resources.job.action.actions.movement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CrouchStartAction extends Action {

    public CrouchStartAction() {
        super(Actions.CROUCH_START);
    }

    @Override
    public String toString() {
        return "CrouchStartAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<CrouchStartAction> {

        @Override
        public CrouchStartAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new CrouchStartAction();
        }
    }
}
