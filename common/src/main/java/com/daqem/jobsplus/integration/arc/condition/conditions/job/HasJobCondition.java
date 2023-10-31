package com.daqem.jobsplus.integration.arc.condition.conditions.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.condition.AbstractCondition;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.integration.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class HasJobCondition extends AbstractCondition implements IJobCondition{

    private final ResourceLocation jobLocation;

    public HasJobCondition(boolean inverted, ResourceLocation jobLocation) {
        super(inverted);
        this.jobLocation = jobLocation;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        JobInstance jobInstance = JobInstance.of(jobLocation);
        if (jobInstance != null) {
            if (jobInstance.getLocation().equals(jobLocation)) {
                if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
                    Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
                    if (job != null) {
                        return job.getLevel() > 0;
                    }
                }
            }
        } else {
            JobsPlus.LOGGER.error("Job " + jobLocation + " does not exist! Trying to use it in the condition has_job.");
        }
        return false;
    }

    @Override
    public IConditionType<? extends ICondition> getType() {
        return JobsPlusConditionType.HAS_JOB;
    }

    @Override
    public IConditionSerializer<? extends ICondition> getSerializer() {
        return JobsPlusConditionSerializer.HAS_JOB;
    }

    @Override
    public ResourceLocation getJobLocation() {
        return jobLocation;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    public static class Serializer implements ConditionSerializer<HasJobCondition> {

        @Override
        public HasJobCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new HasJobCondition(
                    inverted,
                    getResourceLocation(jsonObject, "job"));
        }

        @Override
        public HasJobCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new HasJobCondition(
                    inverted,
                    friendlyByteBuf.readResourceLocation());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, HasJobCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeResourceLocation(type.jobLocation);
        }
    }
}

