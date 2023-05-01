package com.daqem.jobsplus.resources.job.action.actions.block;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PlaceBlockAction extends Action {

    public PlaceBlockAction() {
        super(Actions.PLACE_BLOCK);
    }

    @Override
    public String toString() {
        return "BlockPlaceAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<PlaceBlockAction> {

        @Override
        public PlaceBlockAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new PlaceBlockAction();
        }
    }
}
