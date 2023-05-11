package com.daqem.jobsplus.resources.job.action.reward.rewards.effect;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;

public class EffectDurationMultiplierActionReward extends ActionReward {

    private final double multiplier;

    public EffectDurationMultiplierActionReward(double multiplier) {
        super(ActionRewards.EFFECT_DURATION_MULTIPLIER);
        this.multiplier = multiplier;
    }

    @Override
    public void apply(ActionData actionData) {
        MobEffectInstance effect = actionData.getSpecification(ActionSpecification.MOB_EFFECT_INSTANCE);
        if (effect != null) {
            MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), Mth.floor(effect.getDuration() * multiplier), effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
            actionData.getPlayer().getServerPlayer().addEffect(newEffect);
        }
    }

    public static class Deserializer implements JsonDeserializer<EffectDurationMultiplierActionReward> {

        @Override
        public EffectDurationMultiplierActionReward deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject rewardObject = jsonElement.getAsJsonObject();
            double multiplier = GsonHelper.getAsDouble(rewardObject, "multiplier");
            return new EffectDurationMultiplierActionReward(
                    multiplier);
        }
    }
}
