package com.daqem.jobsplus.resources.job.action.actions.block;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GetDestroySpeedAction extends Action {

    public GetDestroySpeedAction() {
        super(Actions.GET_DESTROY_SPEED, true);
    }

    @Override
    public String toString() {
        return "GetDestroySpeedAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<GetDestroySpeedAction> {

        @Override
        public GetDestroySpeedAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new GetDestroySpeedAction();
        }
    }
}
