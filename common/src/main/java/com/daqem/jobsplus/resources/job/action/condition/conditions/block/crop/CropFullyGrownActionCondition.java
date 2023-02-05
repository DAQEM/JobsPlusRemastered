package com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;

public class CropFullyGrownActionCondition extends ActionCondition {

    public CropFullyGrownActionCondition() {
        super(ActionConditions.CROP_FULLY_GROWN);
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null && blockState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockState);
    }

    @Override
    public String toString() {
        return "CropFullyGrownActionCondition{" +
                "type=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<CropFullyGrownActionCondition> {

        @Override
        public CropFullyGrownActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new CropFullyGrownActionCondition();
        }
    }
}
