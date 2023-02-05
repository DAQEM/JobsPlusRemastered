package com.daqem.jobsplus.resources.job.action.actions.block;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BlockBreakAction extends Action {

    public BlockBreakAction() {
        super(Actions.BLOCK_BREAK);
    }

    @Override
    public String toString() {
        return "BlockBreakAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Serializer implements JsonDeserializer<BlockBreakAction> {

        @Override
        public BlockBreakAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new BlockBreakAction();
        }
    }
}
