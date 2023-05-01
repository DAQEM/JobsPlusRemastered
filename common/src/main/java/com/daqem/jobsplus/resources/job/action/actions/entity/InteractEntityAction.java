package com.daqem.jobsplus.resources.job.action.actions.entity;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class InteractEntityAction extends Action {

    public InteractEntityAction() {
        super(Actions.INTERACT_ENTITY);
    }

    @Override
    public String toString() {
        return "InteractEntityAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<InteractEntityAction> {

        @Override
        public InteractEntityAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new InteractEntityAction();
        }
    }
}
