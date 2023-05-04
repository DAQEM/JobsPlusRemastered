package com.daqem.jobsplus.resources.job.action.reward;

import com.daqem.jobsplus.player.ActionData;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Type;

public abstract class ActionReward {

    private final ActionRewardType type;
    private final double chance;

    public ActionReward(ActionRewardType type, double chance) {
        this.type = type;
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

    public ActionRewardType getType() {
        return type;
    }

    public abstract void apply(ActionData actionData);

    public boolean passedChance(ActionData actionData) {
        return ((ServerPlayer) actionData.getPlayer()).getRandom().nextDouble() * 100 <= chance;
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
            if (!rewardObject.has("type")) {
                throw new JsonParseException("ActionReward must have a type");
            }

            String type = rewardObject.get("type").getAsString();
            ResourceLocation location = new ResourceLocation(type);
            Class<? extends ActionReward> clazz = ActionRewards.getClass(location);

            return (T) getGson().fromJson(rewardObject, clazz);
        }
    }
}
