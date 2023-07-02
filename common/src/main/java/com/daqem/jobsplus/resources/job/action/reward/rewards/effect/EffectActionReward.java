package com.daqem.jobsplus.resources.job.action.reward.rewards.effect;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.action.ActionResult;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Type;

public class EffectActionReward extends ActionReward {

    private final MobEffect effect;
    private final int duration;

    public EffectActionReward(MobEffect effect, int duration) {
        super(ActionRewards.EFFECT);
        this.effect = effect;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "EffectActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", effect=" + effect.toString() +
                ", duration=" + duration +
                '}';
    }

    @Override
    public ActionResult apply(ActionData actionData) {
        JobsPlayer player = actionData.getPlayer();
        player.getPlayer().addEffect(new MobEffectInstance(effect, duration));
        return new ActionResult();
    }

    public static class Deserializer implements JsonDeserializer<EffectActionReward> {
        @Override
        public EffectActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new EffectActionReward(
                    Registry.MOB_EFFECT.get(new ResourceLocation(json.getAsJsonObject().get("effect").getAsString())),
                    GsonHelper.getAsInt(jsonObject, "duration", 100));
        }
    }
}
