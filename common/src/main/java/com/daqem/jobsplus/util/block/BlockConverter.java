package com.daqem.jobsplus.util.block;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Collectors;

public class BlockConverter {

    public static List<Block> convertToBlocks(List<String> blocks) {
        return blocks.stream()
                .filter(blockLoc -> !blockLoc.startsWith("#"))
                .filter(blockLoc -> blockLoc.contains(":"))
                .map(blockLoc -> Registry.BLOCK.get(
                        new ResourceLocation(blockLoc)))
                .collect(Collectors.toList());
    }

    public static List<TagKey<Block>> convertToBlockTags(List<String> blockTags) {
        return blockTags.stream()
                .filter(blockLoc -> blockLoc.startsWith("#"))
                .filter(blockLoc -> blockLoc.contains(":"))
                .map(blockLoc -> TagKey.create(
                        Registry.BLOCK_REGISTRY,
                        new ResourceLocation(
                                blockLoc.replace("#", ""))))
                .collect(Collectors.toList());
    }
}
