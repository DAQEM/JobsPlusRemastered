package com.daqem.jobsplus.resources.job.action.condition.conditions.item;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
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

public class ItemsActionCondition extends ActionCondition {

    private final List<Item> items;
    private final List<TagKey<Item>> itemTags;


    public ItemsActionCondition(List<Item> items, List<TagKey<Item>> itemTags) {
        super(ActionConditions.ITEMS);
        this.items = items;
        this.itemTags = itemTags;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        ItemStack itemStack = actionData.getSpecification(ActionSpecification.ITEM_STACK);
        if (itemStack == null) {
            Item item = actionData.getSpecification(ActionSpecification.ITEM);
            if (item != null) {
                itemStack = item.getDefaultInstance();
            }
        }
        return itemStack != null && (isItem(itemStack) || isItemByTag(itemStack));
    }

    private boolean isItem(ItemStack itemStack) {
        return this.items.contains(itemStack.getItem());
    }

    private boolean isItemByTag(ItemStack itemStack) {
        return this.itemTags.stream().anyMatch(itemStack::is);
    }

    public static class Deserializer implements JsonDeserializer<ItemsActionCondition> {

        @Override
        public ItemsActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            List<String> itemStrings = new ArrayList<>();
            jsonObject.get("items").getAsJsonArray().forEach(jsonElement -> itemStrings.add(jsonElement.getAsString()));
            return new ItemsActionCondition(
                    ItemConverter.convertToItems(itemStrings),
                    ItemConverter.convertToItemTags(itemStrings));
        }
    }

}
