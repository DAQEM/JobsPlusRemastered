package com.daqem.jobsplus.resources.job.action.condition.conditions.job.powerup;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PowerupNoChildrenActiveActionCondition extends ActionCondition {

    public PowerupNoChildrenActiveActionCondition() {
        super(ActionConditions.POWERUP_NO_CHILDREN_ACTIVE);
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Powerup sourcePowerup = actionData.getSpecification(ActionSpecification.POWERUP);
        return sourcePowerup == null || !sourcePowerup.hasActiveChildren();
    }

    public static class Deserializer implements JsonDeserializer<PowerupNoChildrenActiveActionCondition> {
        @Override
        public PowerupNoChildrenActiveActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new PowerupNoChildrenActiveActionCondition();
        }
    }
}
