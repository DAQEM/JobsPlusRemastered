package com.daqem.jobsplus.resources.job.action.condition.conditions.block;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;

public class BlockActionCondition extends ActionCondition {

    private final Block block;

    public BlockActionCondition(Block block) {
        super(ActionConditions.BLOCK);
        this.block = block;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null && blockState.getBlock() == this.block;
    }

    public static class Deserializer implements JsonDeserializer<BlockActionCondition> {

        @Override
        public BlockActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Block block;
            if (jsonObject.has("block")) {
                String blockLocation = jsonObject.get("block").getAsString();
                block = Registry.BLOCK.get(new ResourceLocation(blockLocation));
                if (block == Blocks.AIR && !blockLocation.equals("minecraft:air")) {
                    throw new JsonParseException("Unknown block '" + blockLocation + " in BlockActionCondition");
                }
            } else {
                throw new JsonParseException("Missing block, expected to find a string in BlockActionCondition");
            }
            return new BlockActionCondition(block);
        }
    }
}
