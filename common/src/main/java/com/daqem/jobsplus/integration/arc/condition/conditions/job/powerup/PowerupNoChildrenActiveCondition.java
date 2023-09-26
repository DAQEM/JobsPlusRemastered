package com.daqem.jobsplus.integration.arc.condition.conditions.job.powerup;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.condition.AbstractCondition;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.integration.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.integration.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class PowerupNoChildrenActiveCondition extends AbstractCondition {

    public PowerupNoChildrenActiveCondition(boolean inverted) {
        super(inverted);
    }

    @Override
    public boolean isMet(ActionData actionData) {
        if (actionData.getSourceActionHolder() instanceof Powerup powerup) {
            return !powerup.hasActiveChildren();
        }
        return false;
    }

    @Override
    public IConditionType<? extends ICondition> getType() {
        return JobsPlusConditionType.POWERUP_NO_CHILDREN_ACTIVE;
    }

    @Override
    public IConditionSerializer<? extends ICondition> getSerializer() {
        return JobsPlusConditionSerializer.POWERUP_NO_CHILDREN_ACTIVE;
    }

    public static class Serializer implements ConditionSerializer<PowerupNoChildrenActiveCondition> {

        @Override
        public PowerupNoChildrenActiveCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new PowerupNoChildrenActiveCondition(inverted);
        }

        @Override
        public PowerupNoChildrenActiveCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new PowerupNoChildrenActiveCondition(inverted);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PowerupNoChildrenActiveCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
        }
    }
}
