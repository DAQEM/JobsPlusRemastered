package com.daqem.jobsplus.resources.job.action.actions.item;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ThrowItemAction extends Action {

    public ThrowItemAction() {
        super(Actions.THROW_ITEM);
    }

    @Override
    public String toString() {
        return "ThrowItemAction{" +
                "type=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<ThrowItemAction> {

        @Override
        public ThrowItemAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ThrowItemAction();
        }
    }
}
