package com.daqem.jobsplus.resources.job.action.reward.rewards.item;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class ItemActionReward extends ActionReward {

    private final ItemStack itemStack;

    public ItemActionReward(ItemStack itemStack) {
        super(ActionRewards.ITEM);
        this.itemStack = itemStack;
    }

    @Override
    public String toString() {
        return "ItemActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", item=" + itemStack.copy().getItem().getDescriptionId() +
                ", nbt=" + NbtUtils.structureToSnbt(itemStack.copy().getOrCreateTag()) +
                '}';
    }

    @Override
    public void apply(ActionData actionData) {
        JobsServerPlayer player = actionData.getPlayer();
        ((ServerPlayer) player).addItem(itemStack.copy());
    }

    public static class Deserializer implements JsonDeserializer<ItemActionReward> {

        @Override
        public ItemActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ItemStack itemStack = new ItemStack(GsonHelper.getAsItem(jsonObject, "item"));
            if (jsonObject.has("tag")) {
                String tag = jsonObject.get("tag").getAsString();
                try {
                    itemStack.setTag(TagParser.parseTag(tag));
                } catch (CommandSyntaxException e) {
                    throw new JsonParseException("Failed to parse tag ( " + tag + " ) for item reward.");
                }
            }

            return new ItemActionReward(
                    itemStack);
        }
    }
}
