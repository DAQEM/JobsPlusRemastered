package com.daqem.jobsplus.resources.crafting.restriction;

import com.google.gson.JsonDeserializer;
import net.minecraft.resources.ResourceLocation;

public record CraftingRestrictionType(Class<? extends CraftingRestriction> clazz, ResourceLocation location,
                                      JsonDeserializer<? extends CraftingRestriction> deserializer) {

    @Override
    public String toString() {
        return "CraftingRestrictionType{" +
                "clazz=" + clazz +
                ", location=" + location +
                '}';
    }
}
