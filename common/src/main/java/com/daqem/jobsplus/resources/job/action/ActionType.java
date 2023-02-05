package com.daqem.jobsplus.resources.job.action;

import com.google.gson.JsonDeserializer;
import net.minecraft.resources.ResourceLocation;

public record ActionType(Class<? extends Action> clazz, ResourceLocation location,
                         JsonDeserializer<? extends Action> deserializer) {

    @Override
    public String toString() {
        return "ActionType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
