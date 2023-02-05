package com.daqem.jobsplus.resources.job.action.actions.advancement;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AdvancementAction extends Action {

    public AdvancementAction() {
        super(Actions.ADVANCEMENT);
    }

    @Override
    public String toString() {
        return "AdvancementAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<AdvancementAction> {

        @Override
        public AdvancementAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new AdvancementAction();
        }
    }
}
