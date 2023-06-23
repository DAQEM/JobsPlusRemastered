package com.daqem.jobsplus.resources.job.action.condition.conditions.effect;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.lang.reflect.Type;

public class EffectActionCondition extends ActionCondition {

    private final MobEffect effect;

    public EffectActionCondition(MobEffect effect) {
        super(ActionConditions.EFFECT);
        this.effect = effect;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        MobEffectInstance effectInstance = actionData.getSpecification(ActionSpecification.MOB_EFFECT_INSTANCE);
        return effectInstance != null && effectInstance.getEffect() == this.effect;
    }

    public static class Deserializer implements JsonDeserializer<EffectActionCondition> {

        @Override
        public EffectActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String effectName = GsonHelper.getAsString(jsonObject, "effect");
            ResourceLocation resourceLocation = new ResourceLocation(effectName);
            MobEffect effect = Registry.MOB_EFFECT.get(resourceLocation);
            if (effect == MobEffects.LUCK && !resourceLocation.equals(new ResourceLocation("minecraft:luck"))) {
                throw new JsonParseException("Invalid effect, expected to find a valid effect in EffectActionCondition");
            }
            return new EffectActionCondition(effect);
        }
    }
}
