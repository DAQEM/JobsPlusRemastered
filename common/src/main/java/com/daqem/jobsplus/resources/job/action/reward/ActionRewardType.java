package com.daqem.jobsplus.resources.job.action.reward;

import com.google.gson.JsonDeserializer;
import net.minecraft.resources.ResourceLocation;

public record ActionRewardType(Class<? extends ActionReward> clazz, ResourceLocation location,
                               JsonDeserializer<? extends ActionReward> deserializer) {

    @Override
    public String toString() {
        return "ActionRewardType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
