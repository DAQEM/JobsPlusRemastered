package com.daqem.jobsplus.resources.job.action.reward.rewards;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public class ItemActionReward extends ActionReward {

    private final ItemStack itemStack;

    public ItemActionReward(double chance, ItemStack itemStack) {
        super(ActionRewards.ITEM, chance);
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

    public static class Serializer implements JsonDeserializer<ItemActionReward> {

        @Override
        public ItemActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new ResourceLocation(jsonObject.get("item").getAsString())));

            if (jsonObject.has("nbt")) {
                try {
                    itemStack.setTag(TagParser.parseTag(jsonObject.get("nbt").getAsString()));
                } catch (CommandSyntaxException e) {
                    throw new JsonParseException("Failed to parse nbt ( " + jsonObject.get("nbt").getAsString() + " ) for item reward.");
                }
            }

            double chance = jsonObject.has("chance") ? jsonObject.get("chance").getAsDouble() : 100;

            return new ItemActionReward(
                    chance,
                    itemStack);
        }
    }
}
