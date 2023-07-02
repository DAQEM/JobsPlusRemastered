package com.daqem.jobsplus.resources.job.action.reward.rewards.block;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionResult;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.daqem.jobsplus.resources.job.action.reward.rewards.effect.EffectDurationMultiplierActionReward;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class DestroySpeedMultiplierActionReward extends ActionReward {

    private final float multiplier;

    public DestroySpeedMultiplierActionReward(float multiplier) {
        super(ActionRewards.DESTROY_SPEED_MULTIPLIER);
        this.multiplier = multiplier;
    }

    @Override
    public ActionResult apply(ActionData actionData) {
        return new ActionResult().withDestroySpeedModifier(multiplier);
    }

    public static class Deserializer implements JsonDeserializer<DestroySpeedMultiplierActionReward> {

        @Override
        public DestroySpeedMultiplierActionReward deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject rewardObject = jsonElement.getAsJsonObject();
            float multiplier = GsonHelper.getAsFloat(rewardObject, "multiplier");
            return new DestroySpeedMultiplierActionReward(
                    multiplier);
        }
    }
}
