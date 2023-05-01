package com.daqem.jobsplus.util.item;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemConverter {

    public static List<Item> convertToItems(List<String> items) {
        return items.stream()
                .filter(itemLoc -> !itemLoc.startsWith("#"))
                .filter(itemLoc -> itemLoc.contains(":"))
                .map(itemLoc -> Registry.ITEM.get(
                        new ResourceLocation(itemLoc)))
                .collect(Collectors.toList());
    }

    public static List<TagKey<Item>> convertToItemTags(List<String> itemTags) {
        return itemTags.stream()
                .filter(itemLoc -> itemLoc.startsWith("#"))
                .filter(itemLoc -> itemLoc.contains(":"))
                .map(itemLoc -> TagKey.create(
                        Registry.ITEM_REGISTRY,
                        new ResourceLocation(
                                itemLoc.replace("#", ""))))
                .collect(Collectors.toList());
    }
}
