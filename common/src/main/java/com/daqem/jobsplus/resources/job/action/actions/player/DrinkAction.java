package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DrinkAction extends Action {

    public DrinkAction() {
        super(Actions.DRINK);
    }

    @Override
    public String toString() {
        return "DrinkAction{" +
                "type=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<DrinkAction> {

        @Override
        public DrinkAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new DrinkAction();
        }
    }
}
