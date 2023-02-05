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

import java.lang.reflect.Type;

public class ItemActionCondition extends ActionCondition {

    private final Item item;

    public ItemActionCondition(Item item) {
        super(ActionConditions.ITEM);
        this.item = item;
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
        return item != null && item == this.item;
    }

    public static class Deserializer implements JsonDeserializer<ItemActionCondition> {

        @Override
        public ItemActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Item item;
            if (jsonObject.has("item")) {
                item = Registry.ITEM.get(new ResourceLocation(jsonObject.get("item").getAsString()));
            } else {
                throw new JsonParseException("Missing item, expected to find a string in ItemActionCondition");
            }
            return new ItemActionCondition(item);
        }
    }
}
