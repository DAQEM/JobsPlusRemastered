package com.daqem.jobsplus.resources.job.action.condition.conditions.block.ore;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;

public class IsOreActionCondition extends ActionCondition {

    public IsOreActionCondition() {
        super(ActionConditions.IS_ORE);
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null && isOre(blockState.getBlock());
    }

    @SuppressWarnings("deprecation")
    public static boolean isOre(Block block) {
        return ((block instanceof DropExperienceBlock && block != Blocks.SCULK)
                || block instanceof RedStoneOreBlock
                || block.defaultBlockState().is(new TagKey<>(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores")))
                || block.defaultBlockState().is(new TagKey<>(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "ores")))
                || block == Blocks.ANCIENT_DEBRIS);
    }

    public static class Deserializer implements JsonDeserializer<IsOreActionCondition> {

        @Override
        public IsOreActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new IsOreActionCondition();
        }
    }
}
