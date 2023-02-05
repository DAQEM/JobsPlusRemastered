package com.daqem.jobsplus.resources.job.action.condition.conditions.item;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.lang.reflect.Type;

public class ItemInHandActionCondition extends ActionCondition {

    private final Item item;
    private final InteractionHand hand;

    public ItemInHandActionCondition(Item item, InteractionHand hand) {
        super(ActionConditions.ITEM_IN_HAND);
        this.item = item;
        this.hand = hand;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        ServerPlayer player = actionData.getPlayer().getServerPlayer();
        return (hand == null && player.getMainHandItem().getItem() == item || player.getOffhandItem().getItem() == item)
                || (hand != null && player.getItemInHand(hand).getItem() == item);
    }

    public static class Deserializer implements JsonDeserializer<ItemInHandActionCondition> {

        @Override
        public ItemInHandActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Item item;
            InteractionHand hand;
            if (jsonObject.has("item")) {
                String itemLocation = jsonObject.get("item").getAsString();
                item = Registry.ITEM.get(new ResourceLocation(itemLocation));
                if (item == Items.AIR && !itemLocation.equals("minecraft:air")) {
                    throw new JsonParseException("Unknown item " + itemLocation + " in ItemInHandActionCondition");
                }
            } else {
                throw new JsonParseException("Missing item, expected to find a Item in ItemInHandActionCondition");
            }

            if (jsonObject.has("hand")) {
                String handString = jsonObject.get("hand").getAsString();
                if (handString.contains("main")) {
                    hand = InteractionHand.MAIN_HAND;
                } else if (handString.contains("off")) {
                    hand = InteractionHand.OFF_HAND;
                } else {
                    throw new JsonParseException("Unknown hand " + handString + " in ItemInHandActionCondition");
                }
            } else {
                hand = null;
            }

            return new ItemInHandActionCondition(item, hand);
        }
    }
}
