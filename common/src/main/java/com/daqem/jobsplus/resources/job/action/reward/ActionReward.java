package com.daqem.jobsplus.resources.job.action.reward;

import com.daqem.jobsplus.resources.job.action.reward.type.*;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public abstract class ActionReward {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(JobExpActionReward.class, new JobExpActionReward.JobExpActionRewardSerializer())
            .registerTypeAdapter(ExpActionReward.class, new ExpActionReward.ExpActionRewardSerializer())
            .registerTypeAdapter(ItemActionReward.class, new ItemActionReward.ItemActionRewardSerializer())
            .registerTypeAdapter(GiveEffectActionReward.class, new GiveEffectActionReward.GiveEffectActionRewardSerializer())
            .registerTypeAdapter(JobExpMultiplierActionReward.class, new JobExpMultiplierActionReward.JobExpMultiplierActionRewardSerializer())
            .create();

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

    public static class ActionRewardSerializer<T extends ActionReward> implements JsonDeserializer<T> {

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject rewardObject = json.getAsJsonObject();
            Class<? extends ActionReward> clazz = ActionRewards.getClass(new ResourceLocation(rewardObject.get("type").getAsString()));
            rewardObject.remove("type");
            return (T) GSON.fromJson(rewardObject, clazz);
        }
    }
}
