package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class FishedUpItemAction extends Action {

    public FishedUpItemAction() {
        super(Actions.FISHED_UP_ITEM);
    }

    @Override
    public String toString() {
        return "FishedUpItemAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<FishedUpItemAction> {

        @Override
        public FishedUpItemAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new FishedUpItemAction();
        }
    }
}
