package com.daqem.jobsplus.exception;

import net.minecraft.resources.ResourceLocation;

public class UnknownConditionTypeException extends RuntimeException {

    public UnknownConditionTypeException(ResourceLocation location) {
        super("Unknown condition type: " + location.toString());
    }
}
