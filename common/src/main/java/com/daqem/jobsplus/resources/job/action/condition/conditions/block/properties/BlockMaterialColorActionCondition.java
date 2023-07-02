package com.daqem.jobsplus.resources.job.action.condition.conditions.block.properties;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;

public class BlockMaterialColorActionCondition extends ActionCondition {

    private final int id;

    public BlockMaterialColorActionCondition(int id) {
        super(ActionConditions.BLOCK_MATERIAL_COLOR);
        this.id = id;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null && blockState.getMaterial().getColor().id == id;
    }

    public static class Deserializer implements JsonDeserializer<BlockMaterialColorActionCondition> {

        @Override
        public BlockMaterialColorActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int id = GsonHelper.getAsInt(json.getAsJsonObject(), "id");
            return new BlockMaterialColorActionCondition(id);
        }
    }
}
