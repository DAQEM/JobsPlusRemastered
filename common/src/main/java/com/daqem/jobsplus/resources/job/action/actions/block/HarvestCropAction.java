package com.daqem.jobsplus.resources.job.action.actions.block;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class HarvestCropAction extends Action {

    public HarvestCropAction() {
        super(Actions.HARVEST_CROP);
    }

    @Override
    public String toString() {
        return "HarvestCropAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<HarvestCropAction> {

        @Override
        public HarvestCropAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new HarvestCropAction();
        }
    }
}
