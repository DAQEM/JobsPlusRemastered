package com.daqem.jobsplus.resources.job.action.condition.conditions.entity;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Shearable;

import java.lang.reflect.Type;

public class ReadyForShearingActionCondition extends ActionCondition {

    public ReadyForShearingActionCondition() {
        super(ActionConditions.READY_FOR_SHEARING);
    }

    @Override
    public String toString() {
        return "ReadyForShearingActionCondition{" +
                "type=" + this.getType() +
                '}';
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Entity entity = actionData.getSpecification(ActionSpecification.ENTITY);
        return entity instanceof Shearable shearable && shearable.readyForShearing();
    }

    public static class Deserializer implements JsonDeserializer<ReadyForShearingActionCondition> {

        @Override
        public ReadyForShearingActionCondition deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new ReadyForShearingActionCondition();
        }
    }
}
