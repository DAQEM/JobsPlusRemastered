package com.daqem.jobsplus.resources.job.action.reward;

import com.daqem.jobsplus.player.ActionData;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public abstract class ActionReward {

    private final ActionRewardType type;
    private double chance;

    public ActionReward(ActionRewardType type) {
        this.type = type;
    }

    public double getChance() {
        return chance;
    }

    public ActionRewardType getType() {
        return type;
    }

    public abstract void apply(ActionData actionData);

    public boolean passedChance(ActionData actionData) {
        return chance == 100 || actionData.getPlayer().nextRandomDouble() * 100 <= chance;
    }

    //with chance
    public ActionReward withChance(double chance) {
        this.chance = chance;
        return this;
    }

    public static class ActionRewardSerializer<T extends ActionReward> implements JsonDeserializer<T> {

        private static Gson getGson() {
            GsonBuilder builder = new GsonBuilder().setPrettyPrinting();

            for (ActionRewardType type : ActionRewards.ACTION_REWARD_TYPES) {
                builder.registerTypeAdapter(type.clazz(), type.deserializer());
            }

            return builder.create();
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject rewardObject = json.getAsJsonObject();
            String type = GsonHelper.getAsString(rewardObject, "type");
            ResourceLocation location = new ResourceLocation(type);
            Class<? extends ActionReward> clazz = ActionRewards.getClass(location);

            return (T) getGson().fromJson(rewardObject, clazz)
                    .withChance(GsonHelper.getAsDouble(rewardObject, "chance", 100));
        }
    }
}
