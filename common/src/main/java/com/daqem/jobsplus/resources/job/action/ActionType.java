package com.daqem.jobsplus.resources.job.action;

import net.minecraft.resources.ResourceLocation;

public record ActionType(Class<? extends Action> clazz, ResourceLocation location) {

    @Override
    public String toString() {
        return "ActionType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
