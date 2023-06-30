package com.daqem.jobsplus.resources.job.action.reward.rewards.effect;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class EffectAmplifierAdditionActionReward extends ActionReward {

    private final int addition;

    public EffectAmplifierAdditionActionReward(int addition) {
        super(ActionRewards.EFFECT_AMPLIFIER_ADDITION);
        this.addition = addition;
    }

    @Override
    public boolean apply(ActionData actionData) {
        MobEffectInstance effect = actionData.getSpecification(ActionSpecification.MOB_EFFECT_INSTANCE);
        if (effect != null) {
            ServerPlayer serverPlayer = actionData.getPlayer().getServerPlayer();
            MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), effect.getDuration(), Mth.floor(effect.getAmplifier() + addition), effect.isAmbient(), effect.isVisible());
            serverPlayer.addEffect(newEffect, new ServerPlayer(Objects.requireNonNull(serverPlayer.getServer()), serverPlayer.getLevel(), new GameProfile(UUID.randomUUID(), "Jobs+Powerup"), null));
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<EffectAmplifierAdditionActionReward> {

        @Override
        public EffectAmplifierAdditionActionReward deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject rewardObject = jsonElement.getAsJsonObject();
            int addition = GsonHelper.getAsInt(rewardObject, "addition");
            return new EffectAmplifierAdditionActionReward(
                    addition);
        }
    }
}
