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

public class JobExperiencePercentageCondition extends AbstractCondition {

    private final double percentage;

    public JobExperiencePercentageCondition(boolean inverted, double percentage) {
        super(inverted);
        this.percentage = percentage;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        if (actionData.getSourceActionHolder() instanceof JobInstance jobInstance) {
            if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
                Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
                if (job != null) {
                    return job.getExperiencePercentage() >= percentage;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public IConditionType<? extends ICondition> getType() {
        return JobsPlusConditionType.JOB_EXPERIENCE_PERCENTAGE;
    }

    @Override
    public IConditionSerializer<? extends ICondition> getSerializer() {
        return JobsPlusConditionSerializer.JOB_EXPERIENCE_PERCENTAGE;
    }

    public static class Serializer implements ConditionSerializer<JobExperiencePercentageCondition> {

        @Override
        public JobExperiencePercentageCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new JobExperiencePercentageCondition(
                    inverted,
                    GsonHelper.getAsDouble(jsonObject, "percentage"));
        }

        @Override
        public JobExperiencePercentageCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new JobExperiencePercentageCondition(
                    inverted,
                    friendlyByteBuf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobExperiencePercentageCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeDouble(type.percentage);
        }
    }
}
