package com.daqem.jobsplus.resources.job.action.condition.conditions.effect;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;

public class EffectCategoryActionCondition extends ActionCondition {

    private final MobEffectCategory effectCategory;

    public EffectCategoryActionCondition(MobEffectCategory effectCategory) {
        super(ActionConditions.EFFECT_CATEGORY);
        this.effectCategory = effectCategory;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        MobEffectInstance effectInstance = actionData.getSpecification(ActionSpecification.MOB_EFFECT_INSTANCE);
        return effectInstance != null && effectInstance.getEffect().getCategory() == this.effectCategory;
    }

    public static class Deserializer implements JsonDeserializer<EffectCategoryActionCondition> {

        @Override
        public EffectCategoryActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String effectCategoryName = GsonHelper.getAsString(jsonObject, "category");
            MobEffectCategory effectCategory;
            try {
                effectCategory = MobEffectCategory.valueOf(effectCategoryName);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid effect_category, expected to find a valid effect category ('BENEFICIAL', 'HARMFUL' or 'NEUTRAL') in EffectCategoryActionCondition");
            }
            return new EffectCategoryActionCondition(effectCategory);
        }
    }
}
