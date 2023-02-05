package com.daqem.jobsplus.resources.job.action.condition.conditions.item;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemsActionCondition extends ActionCondition {

    private final List<Item> items;

    public ItemsActionCondition(List<Item> items) {
        super(ActionConditions.ITEMS);
        this.items = items;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Item item = actionData.getSpecification(ActionSpecification.ITEM);
        if (item == null) {
            ItemStack itemStack = actionData.getSpecification(ActionSpecification.ITEM_STACK);
            if (itemStack != null) {
                item = itemStack.getItem();
            }
        }
        return item != null && items.contains(item);
    }

    public static class Deserializer implements JsonDeserializer<ItemsActionCondition> {

        @Override
        public ItemsActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ArrayList<Item> items = new ArrayList<>();
            if (jsonObject.has("items")) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("items");
                for (JsonElement jsonElement : jsonArray) {
                    Item item = Registry.ITEM.get(new ResourceLocation(jsonElement.getAsString()));
                    if (item == Items.AIR && !jsonElement.getAsString().equals("minecraft:air")) {
                        throw new JsonParseException("Unknown item " + jsonElement.getAsString() + " in ItemsActionCondition.");
                    } else {
                        items.add(item);
                    }
                }
            } else {
                throw new JsonParseException("Missing items, expected to find a string in ItemsActionCondition");
            }
            return new ItemsActionCondition(items);
        }
    }

}
