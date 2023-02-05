package com.daqem.jobsplus.resources.job.action.actions.block;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BlockPlaceAction extends Action {

    public BlockPlaceAction() {
        super(Actions.BLOCK_PLACE);
    }

    @Override
    public String toString() {
        return "BlockPlaceAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Serializer implements JsonDeserializer<BlockPlaceAction> {

        @Override
        public BlockPlaceAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new BlockPlaceAction();
        }
    }
}
