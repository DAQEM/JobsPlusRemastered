package com.daqem.jobsplus.integration.arc.condition.conditions.job;

import net.minecraft.resources.ResourceLocation;

public interface IJobCondition {

    ResourceLocation getJobLocation();

    int getRequiredLevel();
}
