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

public class ItemEquippedActionCondition extends ActionCondition {

    private final Item item;

    public ItemEquippedActionCondition(Item item) {
        super(ActionConditions.ITEM_EQUIPPED);
        this.item = item;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        ServerPlayer player = actionData.getPlayer().getServerPlayer();
        return player.getInventory().armor.stream().anyMatch(stack -> stack.getItem() == item);
    }

    public static class Deserializer implements JsonDeserializer<ItemEquippedActionCondition> {

        @Override
        public ItemEquippedActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Item item;
            if (jsonObject.has("item")) {
                String itemLocation = jsonObject.get("item").getAsString();
                item = Registry.ITEM.get(new ResourceLocation(itemLocation));
                if (item == Items.AIR && !itemLocation.equals("minecraft:air")) {
                    throw new JsonParseException("Unknown item " + itemLocation + " in ItemEquippedActionCondition");
                }
            } else {
                throw new JsonParseException("Missing item, expected to find a Item in ItemEquippedActionCondition");
            }
            return new ItemEquippedActionCondition(item);
        }
    }
}
