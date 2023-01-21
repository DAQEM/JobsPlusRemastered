package com.daqem.jobsplus.resources.job.action.condition;

import net.minecraft.resources.ResourceLocation;

public record ActionConditionType(Class<? extends ActionCondition> clazz, ResourceLocation location) {

    @Override
    public String toString() {
        return "ActionConditionType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
