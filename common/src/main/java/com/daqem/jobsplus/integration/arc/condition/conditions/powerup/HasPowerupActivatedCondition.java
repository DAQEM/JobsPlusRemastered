package com.daqem.jobsplus.integration.arc.condition.conditions.powerup;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.condition.AbstractCondition;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.HasJobCondition;
import com.daqem.jobsplus.integration.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.integration.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class HasPowerupActivatedCondition extends AbstractCondition  {

    private final ResourceLocation powerupLocation;

    public HasPowerupActivatedCondition(boolean inverted, ResourceLocation powerupLocation) {
        super(inverted);
        this.powerupLocation = powerupLocation;
    }

    @Override
    public IConditionType<? extends ICondition> getType() {
        return JobsPlusConditionType.HAS_POWERUP_ACTIVATED;
    }

    @Override
    public IConditionSerializer<? extends ICondition> getSerializer() {
        return JobsPlusConditionSerializer.HAS_POWERUP_ACTIVATED;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        PowerupInstance powerupInstance = PowerupInstance.of(powerupLocation);
        if (powerupInstance != null) {
            if (powerupInstance.getLocation().equals(powerupLocation)) {
                if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
                    Powerup powerup = jobsServerPlayer.jobsplus$getPowerup(powerupInstance);
                    return powerup != null && powerup.getPowerupState() == PowerupState.ACTIVE;
                }
            }
        } else {
            JobsPlus.LOGGER.error("Powerup " + powerupLocation + " does not exist! Trying to use it in the condition has_powerup_activated.");
        }
        return false;
    }

    public static class Serializer implements ConditionSerializer<HasPowerupActivatedCondition> {

        @Override
        public HasPowerupActivatedCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new HasPowerupActivatedCondition(
                    inverted,
                    getResourceLocation(jsonObject, "powerup"));
        }

        @Override
        public HasPowerupActivatedCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new HasPowerupActivatedCondition(
                    inverted,
                    friendlyByteBuf.readResourceLocation());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, HasPowerupActivatedCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeResourceLocation(type.powerupLocation);
        }
    }
}
