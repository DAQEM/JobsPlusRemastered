package com.daqem.jobsplus.resources.job.action.type;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.daqem.jobsplus.util.block.BlockConverter;
import com.google.gson.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BuildAction extends Action {

    private boolean allBlocks;
    private List<Block> blocks;
    private List<TagKey<Block>> blockTags;
    private List<Block> bannedBlocks;
    private List<TagKey<Block>> bannedBlockTags;

    public BuildAction(boolean allBlocks, List<String> blocks, List<String> bannedBlocks) {
        super(Actions.BUILD);

        this.blocks = BlockConverter.convertToBlocks(blocks);
        this.blockTags = BlockConverter.convertToBlockTags(blocks);
        this.bannedBlocks = BlockConverter.convertToBlocks(blocks);
        this.bannedBlockTags = BlockConverter.convertToBlockTags(blocks);
    }

    @Override
    public String toString() {
        return "BuildAction{" +
                "type=" + this.getType() +
                ", all_blocks=" + allBlocks +
                ", blocks=" + blocks +
                ", block_tags=" + blockTags +
                ", banned_blocks=" + bannedBlocks +
                ", banned_block_tags=" + bannedBlockTags +
                '}';
    }

    public static class BuildActionSerializer implements JsonDeserializer<BuildAction> {

        @Override
        public BuildAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            List<String> blocks = new ArrayList<>();
            List<String> bannedBlocks = new ArrayList<>();

            jsonObject.get("blocks").getAsJsonArray().forEach(jsonElement -> blocks.add(jsonElement.getAsString()));
            jsonObject.get("banned_blocks").getAsJsonArray().forEach(jsonElement -> bannedBlocks.add(jsonElement.getAsString()));

            return new BuildAction(
                    jsonObject.get("all_blocks").getAsBoolean(),
                    blocks,
                    bannedBlocks);
        }
    }
}
