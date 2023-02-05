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

public class BannedBlocksActionCondition extends ActionCondition {

    private final List<Block> bannedBlocks;
    private final List<TagKey<Block>> bannedBlockTags;

    public BannedBlocksActionCondition(List<Block> bannedBlocks, List<TagKey<Block>> bannedBlockTags) {
        super(ActionConditions.BANNED_BLOCKS);
        this.bannedBlocks = bannedBlocks;
        this.bannedBlockTags = bannedBlockTags;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null
                && (!this.bannedBlocks.contains(blockState.getBlock())
                || this.bannedBlockTags.stream().noneMatch(blockState::is));
    }

    public static class Deserializer implements JsonDeserializer<BannedBlocksActionCondition> {

        @Override
        public BannedBlocksActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            List<String> blockStrings = new ArrayList<>();
            jsonObject.get("banned_blocks").getAsJsonArray().forEach(jsonElement -> blockStrings.add(jsonElement.getAsString()));
            return new BannedBlocksActionCondition(
                    BlockConverter.convertToBlocks(blockStrings),
                    BlockConverter.convertToBlockTags(blockStrings));
        }
    }
}
