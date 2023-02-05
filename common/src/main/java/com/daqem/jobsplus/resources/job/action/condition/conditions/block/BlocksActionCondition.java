package com.daqem.jobsplus.resources.job.action.condition.conditions.block;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.daqem.jobsplus.util.block.BlockConverter;
import com.google.gson.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BlocksActionCondition extends ActionCondition {

    private final List<Block> blocks;
    private final List<TagKey<Block>> blockTags;

    public BlocksActionCondition(List<Block> blocks, List<TagKey<Block>> blockTags) {
        super(ActionConditions.BLOCKS);
        this.blocks = blocks;
        this.blockTags = blockTags;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null
                && (this.blocks.contains(blockState.getBlock())
                || this.blockTags.stream().anyMatch(blockState::is));
    }

    public static class Serializer implements JsonDeserializer<BlocksActionCondition> {

        @Override
        public BlocksActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            List<String> blockStrings = new ArrayList<>();
            jsonObject.get("blocks").getAsJsonArray().forEach(jsonElement -> blockStrings.add(jsonElement.getAsString()));
            return new BlocksActionCondition(
                    BlockConverter.convertToBlocks(blockStrings),
                    BlockConverter.convertToBlockTags(blockStrings));
        }
    }
}
