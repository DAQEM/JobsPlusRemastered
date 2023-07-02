package com.daqem.jobsplus.resources.job.action.reward.rewards.effect;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionResult;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Type;

public class RemoveEffectActionReward extends ActionReward {


    public RemoveEffectActionReward() {
        super(ActionRewards.REMOVE_EFFECT);
    }

    @Override
    public ActionResult apply(ActionData actionData) {
        MobEffectInstance effect = actionData.getSpecification(ActionSpecification.MOB_EFFECT_INSTANCE);
        if (effect != null) {
            Player player = actionData.getPlayer().getPlayer();
            player.removeEffect(effect.getEffect());
        }
        return new ActionResult();
    }

    public static class Deserializer implements JsonDeserializer<RemoveEffectActionReward> {

        @Override
        public RemoveEffectActionReward deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new RemoveEffectActionReward();
        }
    }
}
