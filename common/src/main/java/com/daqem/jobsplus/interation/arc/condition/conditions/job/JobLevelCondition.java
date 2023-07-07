package com.daqem.jobsplus.interation.arc.condition.conditions.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.condition.AbstractCondition;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.interation.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class JobLevelCondition extends AbstractCondition {

    private final int min;
    private final int max;

    public JobLevelCondition(boolean inverted, int min, int max) {
        super(inverted);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        if (actionData.getSourceActionHolder() instanceof JobInstance jobInstance) {
            if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
                Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
                if (job != null) {
                    return job.getLevel() >= min && job.getLevel() <= max;
                }
            }
        }
        return false;
    }

    @Override
    public IConditionType<? extends ICondition> getType() {
        return JobsPlusConditionType.JOB_LEVEL;
    }

    @Override
    public IConditionSerializer<? extends ICondition> getSerializer() {
        return JobsPlusConditionSerializer.JOB_LEVEL;
    }

    public static class Serializer implements ConditionSerializer<JobLevelCondition> {

        @Override
        public JobLevelCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new JobLevelCondition(
                    inverted,
                    GsonHelper.getAsInt(jsonObject, "min"),
                    GsonHelper.getAsInt(jsonObject, "max"));
        }

        @Override
        public JobLevelCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new JobLevelCondition(
                    inverted,
                    friendlyByteBuf.readInt(),
                    friendlyByteBuf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobLevelCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeInt(type.min);
            friendlyByteBuf.writeInt(type.max);
        }
    }
}
