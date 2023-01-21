package com.daqem.jobsplus.resources.job.action.reward.type;

import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.lang.reflect.Type;

public class GiveEffectActionReward extends ActionReward {

    private final MobEffect effect;
    private final int duration;

    public GiveEffectActionReward(double chance, MobEffect effect, int duration) {
        super(ActionRewards.GIVE_EFFECT, chance);
        this.effect = effect;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "GiveEffectActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", effect=" + effect.toString() +
                ", duration=" + duration +
                '}';
    }

    public static class GiveEffectActionRewardSerializer implements JsonDeserializer<GiveEffectActionReward> {
        @Override
        public GiveEffectActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new GiveEffectActionReward(
                    json.getAsJsonObject().get("chance").getAsDouble(),
                    Registry.MOB_EFFECT.get(new ResourceLocation(json.getAsJsonObject().get("effect").getAsString())),
                    json.getAsJsonObject().get("duration").getAsInt());
        }
    }
}
