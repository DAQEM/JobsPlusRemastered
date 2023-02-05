package com.daqem.jobsplus.resources.job.action.condition;

import com.google.gson.JsonDeserializer;
import net.minecraft.resources.ResourceLocation;

public record ActionConditionType(Class<? extends ActionCondition> clazz, ResourceLocation location,
                                  JsonDeserializer<? extends ActionCondition> deserializer) {

    @Override
    public String toString() {
        return "ActionConditionType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
