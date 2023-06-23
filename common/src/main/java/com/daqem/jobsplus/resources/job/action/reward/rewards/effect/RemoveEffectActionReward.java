package com.daqem.jobsplus.resources.job.action.reward.rewards.effect;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;

public class RemoveEffectActionReward extends ActionReward {


    public RemoveEffectActionReward() {
        super(ActionRewards.REMOVE_EFFECT);
    }

    @Override
    public void apply(ActionData actionData) {
        MobEffectInstance effect = actionData.getSpecification(ActionSpecification.MOB_EFFECT_INSTANCE);
        if (effect != null) {
            ServerPlayer serverPlayer = actionData.getPlayer().getServerPlayer();
            serverPlayer.removeEffect(effect.getEffect());
        }
    }

    public static class Deserializer implements JsonDeserializer<RemoveEffectActionReward> {

        @Override
        public RemoveEffectActionReward deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new RemoveEffectActionReward();
        }
    }
}
