package com.daqem.jobsplus.exception;

import net.minecraft.resources.ResourceLocation;

public class UnknownActionTypeException extends RuntimeException {

    public UnknownActionTypeException(ResourceLocation location) {
        super("Unknown action type: " + location.toString());
    }
}
