package com.daqem.jobsplus.resources.job.action.actions.entity;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GetHurtAction extends Action {

    public GetHurtAction() {
        super(Actions.GET_HURT);
    }

    @Override
    public String toString() {
        return "DeathAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Serializer implements JsonDeserializer<GetHurtAction> {

        @Override
        public GetHurtAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new GetHurtAction();
        }
    }
}
