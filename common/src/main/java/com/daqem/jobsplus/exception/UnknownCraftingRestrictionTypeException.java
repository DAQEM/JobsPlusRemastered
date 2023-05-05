package com.daqem.jobsplus.exception;

import net.minecraft.resources.ResourceLocation;

public class UnknownCraftingRestrictionTypeException extends RuntimeException {

    public UnknownCraftingRestrictionTypeException(ResourceLocation location) {
        super("Unknown crafting restriction type: " + location.toString());
    }
}
