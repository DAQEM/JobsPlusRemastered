package com.daqem.jobsplus.resources.job.action.condition.conditions.job.powerup;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.Objects;

public class PowerupNotActiveActionCondition extends ActionCondition {

    ResourceLocation powerupThatShouldNotBeActiveLocation;

    public PowerupNotActiveActionCondition(ResourceLocation powerupLocation) {
        super(ActionConditions.POWERUP_NOT_ACTIVE);
        this.powerupThatShouldNotBeActiveLocation = powerupLocation;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        PowerupInstance powerupInstance = PowerupInstance.of(powerupThatShouldNotBeActiveLocation);
        Powerup powerup = actionData.getPlayer().getJobs().stream()
                .map(job -> job.getPowerupManager().getPowerup(powerupInstance))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return powerup == null || (powerup.getPowerupInstance().getLocation().equals(this.powerupThatShouldNotBeActiveLocation)
                && powerup.getPowerupState() != PowerupState.ACTIVE);
    }

    @Override
    public String toString() {
        return "PowerUpNotActiveActionCondition{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<PowerupNotActiveActionCondition> {

        @Override
        public PowerupNotActiveActionCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject conditionObject = jsonElement.getAsJsonObject();
            return new PowerupNotActiveActionCondition(new ResourceLocation(GsonHelper.getAsString(conditionObject, "powerup")));
        }
    }
}
