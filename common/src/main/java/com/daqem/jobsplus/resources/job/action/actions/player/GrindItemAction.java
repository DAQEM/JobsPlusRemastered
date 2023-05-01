package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GrindItemAction extends Action {

    public GrindItemAction() {
        super(Actions.GRIND_ITEM);
    }

    @Override
    public String toString() {
        return "GrindItemAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<GrindItemAction> {

        @Override
        public GrindItemAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new GrindItemAction();
        }
    }
}
