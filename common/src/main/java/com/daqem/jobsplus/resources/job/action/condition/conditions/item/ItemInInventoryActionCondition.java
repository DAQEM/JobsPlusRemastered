package com.daqem.jobsplus.resources.job.action.condition.conditions.item;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.lang.reflect.Type;

public class ItemInInventoryActionCondition extends ActionCondition {

    private final Item item;

    public ItemInInventoryActionCondition(Item item) {
        super(ActionConditions.ITEM_IN_INVENTORY);
        this.item = item;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        ServerPlayer player = actionData.getPlayer().getServerPlayer();
        return player.getInventory().items.stream().anyMatch(stack -> stack.getItem() == item);
    }

    public static class Deserializer implements JsonDeserializer<ItemInInventoryActionCondition> {

        @Override
        public ItemInInventoryActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Item item;
            if (jsonObject.has("item")) {
                String itemLocation = jsonObject.get("item").getAsString();
                item = Registry.ITEM.get(new ResourceLocation(itemLocation));
                if (item == Items.AIR && !itemLocation.equals("minecraft:air")) {
                    throw new JsonParseException("Unknown item " + itemLocation + " in ItemInInventoryActionCondition");
                }
            } else {
                throw new JsonParseException("Missing item, expected to find a Item in ItemInInventoryActionCondition");
            }
            return new ItemInInventoryActionCondition(item);
        }
    }
}
