package com.daqem.jobsplus.resources.job.action.reward;

import net.minecraft.resources.ResourceLocation;

public record ActionRewardType(Class<? extends ActionReward> clazz, ResourceLocation location) {

    @Override
    public String toString() {
        return "ActionRewardType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
