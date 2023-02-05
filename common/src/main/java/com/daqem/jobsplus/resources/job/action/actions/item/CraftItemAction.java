package com.daqem.jobsplus.resources.job.action.actions.item;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CraftItemAction extends Action {

    public CraftItemAction() {
        super(Actions.CRAFT_ITEM);
    }

    @Override
    public String toString() {
        return "CraftItemAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<CraftItemAction> {

        @Override
        public CraftItemAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new CraftItemAction();
        }
    }
}
