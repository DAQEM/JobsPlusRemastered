package com.daqem.jobsplus.resources.job.action.condition.conditions.block.properties;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;

public class BlockHardnessActionCondition extends ActionCondition {

    private final float minHardness;
    private final float maxHardness;

    public BlockHardnessActionCondition(float minHardness, float maxHardness) {
        super(ActionConditions.BLOCK_HARDNESS);
        this.minHardness = minHardness;
        this.maxHardness = maxHardness;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        BlockPos blockPos = actionData.getSpecification(ActionSpecification.BLOCK_POSITION);
        if (blockState == null || blockPos == null) return false;
        float hardness = blockState.getDestroySpeed(actionData.getPlayer().getServerPlayer().getLevel(), blockPos);
        return hardness >= minHardness && hardness <= maxHardness;
    }

    public static class Deserializer implements JsonDeserializer<BlockHardnessActionCondition> {

        @Override
        public BlockHardnessActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            float minHardness = GsonHelper.getAsFloat(json.getAsJsonObject(), "min", 0.0F);
            float maxHardness = GsonHelper.getAsFloat(json.getAsJsonObject(), "max", Float.MAX_VALUE);
            return new BlockHardnessActionCondition(minHardness, maxHardness);
        }
    }
}
