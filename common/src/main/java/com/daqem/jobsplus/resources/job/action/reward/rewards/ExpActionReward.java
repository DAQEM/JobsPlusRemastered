package com.daqem.jobsplus.resources.job.action.reward.rewards;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.*;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Type;

public class ExpActionReward extends ActionReward {

    private final int minExp;
    private final int maxExp;

    public ExpActionReward(double chance, int minExp, int maxExp) {
        super(ActionRewards.EXP, chance);
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    @Override
    public String toString() {
        return "ExpActionReward{" +
                "chance=" + this.getChance() +
                ", type=" + this.getType() +
                ", min_exp=" + minExp +
                ", max_exp=" + maxExp +
                '}';
    }

    @Override
    public void apply(ActionData actionData) {
        JobsServerPlayer player = actionData.getPlayer();
        int exp = ((ServerPlayer) player).getRandom().nextInt(minExp, maxExp + 1);
        ((ServerPlayer) player).giveExperiencePoints(exp);
    }

    public static class Serializer implements JsonDeserializer<ExpActionReward> {

        @Override
        public ExpActionReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int minExp = jsonObject.get("min_exp").getAsInt();
            int maxExp = jsonObject.get("max_exp").getAsInt();
            if (minExp > maxExp) {
                throw new JsonParseException("min_exp cannot be greater than max_exp for ExpActionReward.");
            }

            double chance = jsonObject.has("chance") ? jsonObject.get("chance").getAsDouble() : 100;

            return new ExpActionReward(
                    chance,
                    minExp,
                    maxExp);
        }
    }
}
