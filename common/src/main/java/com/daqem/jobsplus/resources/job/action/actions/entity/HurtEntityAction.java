package com.daqem.jobsplus.resources.job.action.actions.entity;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class HurtEntityAction extends Action {

    public HurtEntityAction() {
        super(Actions.HURT_ENTITY);
    }

    @Override
    public String toString() {
        return "HurtEntityAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Serializer implements JsonDeserializer<HurtEntityAction> {

        @Override
        public HurtEntityAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new HurtEntityAction();
        }
    }
}
