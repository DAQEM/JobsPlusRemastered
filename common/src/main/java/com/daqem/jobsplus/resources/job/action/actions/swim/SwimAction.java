package com.daqem.jobsplus.resources.job.action.actions.swim;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SwimAction extends Action {

    public SwimAction() {
        super(Actions.SWIM);
    }

    @Override
    public String toString() {
        return "SwimAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<SwimAction> {

        @Override
        public SwimAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SwimAction();
        }
    }
}
