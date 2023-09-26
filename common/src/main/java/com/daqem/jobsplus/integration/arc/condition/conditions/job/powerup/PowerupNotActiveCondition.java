package com.daqem.jobsplus.integration.arc.condition.conditions.job.powerup;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.condition.AbstractCondition;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.integration.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.integration.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class PowerupNotActiveCondition extends AbstractCondition {

    ResourceLocation powerupThatShouldNotBeActiveLocation;

    public PowerupNotActiveCondition(boolean inverted, ResourceLocation powerupThatShouldNotBeActiveLocation) {
        super(inverted);
        this.powerupThatShouldNotBeActiveLocation = powerupThatShouldNotBeActiveLocation;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        if (actionData.getPlayer() instanceof JobsPlayer player) {
            PowerupInstance powerupInstance = PowerupInstance.of(powerupThatShouldNotBeActiveLocation);
            Powerup powerup = player.jobsplus$getJobs().stream()
                    .map(job -> job.getPowerupManager().getPowerup(powerupInstance))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            return powerup == null || (powerup != null
                    && (powerup.getPowerupInstance().getLocation().equals(this.powerupThatShouldNotBeActiveLocation)
                    && powerup.getPowerupState() != PowerupState.ACTIVE));
        }
        return false;
    }

    @Override
    public IConditionType<? extends ICondition> getType() {
        return JobsPlusConditionType.POWERUP_NOT_ACTIVE;
    }

    @Override
    public IConditionSerializer<? extends ICondition> getSerializer() {
        return JobsPlusConditionSerializer.POWERUP_NOT_ACTIVE;
    }

    public static class Serializer implements ConditionSerializer<PowerupNotActiveCondition> {


        @Override
        public PowerupNotActiveCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new PowerupNotActiveCondition(
                    inverted,
                    getResourceLocation(jsonObject, "powerup"));
        }

        @Override
        public PowerupNotActiveCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new PowerupNotActiveCondition(
                    inverted,
                    friendlyByteBuf.readResourceLocation());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PowerupNotActiveCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeResourceLocation(type.powerupThatShouldNotBeActiveLocation);
        }
    }
}
