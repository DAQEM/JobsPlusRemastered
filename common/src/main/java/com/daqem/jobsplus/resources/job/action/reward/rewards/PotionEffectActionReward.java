package com.daqem.jobsplus.resources.job.action.reward.rewards;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;

public class PotionEffectActionReward extends ActionReward {

    private final MobEffect effect;
    private final int duration;

    public PotionEffectActionReward(double chance, MobEffect effect, int duration) {
        super(ActionRewards.POTION_EFFECT, chance);
        this.effect = effect;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "PotionEffectActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", effect=" + effect.toString() +
                ", duration=" + duration +
                '}';
    }

    @Override
    public void apply(ActionData actionData) {
        JobsServerPlayer player = actionData.getPlayer();
        ((ServerPlayer) player).addEffect(new MobEffectInstance(effect, duration));
    }

    public static class Serializer implements JsonDeserializer<PotionEffectActionReward> {
        @Override
        public PotionEffectActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            double chance = jsonObject.has("chance") ? jsonObject.get("chance").getAsDouble() : 100;
            int duration = jsonObject.has("duration") ? jsonObject.get("duration").getAsInt() : 100;

            return new PotionEffectActionReward(
                    chance,
                    Registry.MOB_EFFECT.get(new ResourceLocation(json.getAsJsonObject().get("effect").getAsString())),
                    duration);
        }
    }
}
