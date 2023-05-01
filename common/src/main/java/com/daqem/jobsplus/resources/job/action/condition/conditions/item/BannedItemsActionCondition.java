package com.daqem.jobsplus.resources.job.action.condition.conditions.item;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.daqem.jobsplus.util.item.ItemConverter;
import com.google.gson.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BannedItemsActionCondition extends ActionCondition {

    private final List<Item> bannedItems;
    private final List<TagKey<Item>> bannedItemTags;

    public BannedItemsActionCondition(List<Item> bannedItems, List<TagKey<Item>> bannedItemTags) {
        super(ActionConditions.BANNED_ITEMS);
        this.bannedItems = bannedItems;
        this.bannedItemTags = bannedItemTags;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        ItemStack itemStack = actionData.getSpecification(ActionSpecification.ITEM_STACK);
        return itemStack != null && !(isBannedItem(itemStack) || isBannedItemByTag(itemStack));
    }

    private boolean isBannedItem(ItemStack itemStack) {
        return this.bannedItems.contains(itemStack.getItem());
    }

    private boolean isBannedItemByTag(ItemStack itemStack) {
        return this.bannedItemTags.stream().anyMatch(itemStack::is);
    }

    public static class Deserializer implements JsonDeserializer<BannedItemsActionCondition> {

        @Override
        public BannedItemsActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            List<String> itemStrings = new ArrayList<>();
            jsonObject.get("banned_items").getAsJsonArray().forEach(jsonElement -> itemStrings.add(jsonElement.getAsString()));
            return new BannedItemsActionCondition(
                    ItemConverter.convertToItems(itemStrings),
                    ItemConverter.convertToItemTags(itemStrings));
        }
    }
}
