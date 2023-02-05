package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SmeltItemAction extends Action {

    public SmeltItemAction() {
        super(Actions.SMELT_ITEM);
    }

    @Override
    public String toString() {
        return "SmeltItemAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SmeltItemAction> {

        @Override
        public SmeltItemAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SmeltItemAction();
        }
    }
}
