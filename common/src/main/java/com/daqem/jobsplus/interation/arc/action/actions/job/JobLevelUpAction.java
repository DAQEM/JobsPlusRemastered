package com.daqem.jobsplus.interation.arc.action.actions.job;

import com.daqem.arc.api.action.AbstractAction;
import com.daqem.arc.api.action.holder.type.IActionHolderType;
import com.daqem.arc.api.action.serializer.ActionSerializer;
import com.daqem.arc.api.action.serializer.IActionSerializer;
import com.daqem.arc.api.action.type.IActionType;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.reward.IReward;
import com.daqem.jobsplus.interation.arc.action.serializer.JobsPlusActionSerializer;
import com.daqem.jobsplus.interation.arc.action.type.JobsPlusActionType;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class JobLevelUpAction extends AbstractAction {

    public JobLevelUpAction(ResourceLocation location, ResourceLocation actionHolderLocation, IActionHolderType<?> actionHolderType, boolean performOnClient, List<IReward> rewards, List<ICondition> conditions) {
        super(location, actionHolderLocation, actionHolderType, performOnClient, rewards, conditions);
    }

    @Override
    public IActionType<?> getType() {
        return JobsPlusActionType.JOB_LEVEL_UP;
    }

    @Override
    public IActionSerializer<?> getSerializer() {
        return JobsPlusActionSerializer.JOB_LEVEL_UP;
    }

    public static class Serializer implements ActionSerializer<JobLevelUpAction> {

        @Override
        public JobLevelUpAction fromJson(ResourceLocation location, JsonObject jsonObject, ResourceLocation actionHolderLocation, IActionHolderType<?> actionHolderType, boolean performOnClient, List<IReward> rewards, List<ICondition> conditions) {
            return new JobLevelUpAction(location, actionHolderLocation, actionHolderType, performOnClient, rewards, conditions);
        }

        @Override
        public JobLevelUpAction fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, ResourceLocation actionHolderLocation, IActionHolderType<?> actionHolderType, boolean performOnClient, List<IReward> rewards, List<ICondition> conditions) {
            return new JobLevelUpAction(location, actionHolderLocation, actionHolderType, performOnClient, rewards, conditions);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobLevelUpAction type) {
            ActionSerializer.super.toNetwork(friendlyByteBuf, type);
        }
    }
}
