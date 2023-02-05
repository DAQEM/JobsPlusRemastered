package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EatAction extends Action {

    public EatAction() {
        super(Actions.EAT);
    }

    @Override
    public String toString() {
        return "EatAction{" +
                "actionType=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<EatAction> {

        @Override
        public EatAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new EatAction();
        }
    }
}
