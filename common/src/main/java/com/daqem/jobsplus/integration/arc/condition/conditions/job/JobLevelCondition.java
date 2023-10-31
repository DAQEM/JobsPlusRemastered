package com.daqem.jobsplus.integration.arc.condition.conditions.job;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.condition.AbstractCondition;
import com.daqem.arc.api.condition.ICondition;
import com.daqem.arc.api.condition.serializer.ConditionSerializer;
import com.daqem.arc.api.condition.serializer.IConditionSerializer;
import com.daqem.arc.api.condition.type.IConditionType;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.integration.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class JobLevelCondition extends AbstractCondition implements IJobCondition {

    private static final String EMPTY_JOB_LOCATION = "jobsplus:empty";

    private final ResourceLocation jobLocation;
    private final int level;

    public JobLevelCondition(boolean inverted, ResourceLocation jobLocation, int level) {
        super(inverted);
        this.jobLocation = jobLocation;
        this.level = level;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        JobInstance jobInstance;
        if (!jobLocation.equals(new ResourceLocation(EMPTY_JOB_LOCATION))) {
            jobInstance = JobInstance.of(jobLocation);
        } else if (actionData.getSourceActionHolder() instanceof JobInstance jobInstance2) {
            jobInstance = jobInstance2;
        } else {
            return false;
        }

        if (actionData.getPlayer() instanceof JobsServerPlayer jobsServerPlayer) {
            Job job = jobsServerPlayer.jobsplus$getJob(jobInstance);
            int level;
            if (job == null) level = 0;
            else level = job.getLevel();
            return level <= this.level;
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

    public ResourceLocation getJobLocation() {
        return jobLocation;
    }

    @Override
    public int getRequiredLevel() {
        return level;
    }

    public static class Serializer implements ConditionSerializer<JobLevelCondition> {

        @Override
        public JobLevelCondition fromJson(ResourceLocation location, JsonObject jsonObject, boolean inverted) {
            return new JobLevelCondition(
                    inverted,
                    new ResourceLocation(GsonHelper.getAsString(jsonObject, "job", EMPTY_JOB_LOCATION)),
                    GsonHelper.getAsInt(jsonObject, "level"));
        }

        @Override
        public JobLevelCondition fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, boolean inverted) {
            return new JobLevelCondition(
                    inverted,
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobLevelCondition type) {
            ConditionSerializer.super.toNetwork(friendlyByteBuf, type);
            friendlyByteBuf.writeResourceLocation(type.jobLocation);
            friendlyByteBuf.writeInt(type.level);
        }
    }
}
