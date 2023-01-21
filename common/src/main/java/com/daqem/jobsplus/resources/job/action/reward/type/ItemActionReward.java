package com.daqem.jobsplus.resources.job.action.reward.type;

import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.lang.reflect.Type;

public class ItemActionReward extends ActionReward {

    private final Item item;

    public ItemActionReward(double chance, Item item) {
        super(ActionRewards.ITEM, chance);
        this.item = item;
    }

    @Override
    public String toString() {
        return "ItemActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", item=" + item.toString() +
                '}';
    }

    public static class ItemActionRewardSerializer implements JsonDeserializer<ItemActionReward> {

        @Override
        public ItemActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ItemActionReward(
                    jsonObject.get("chance").getAsDouble(),
                    Registry.ITEM.get(new ResourceLocation(jsonObject.get("item").getAsString())));
        }
    }
}
