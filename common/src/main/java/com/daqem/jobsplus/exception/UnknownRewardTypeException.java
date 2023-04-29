package com.daqem.jobsplus.exception;

import net.minecraft.resources.ResourceLocation;

public class UnknownRewardTypeException extends RuntimeException {

    public UnknownRewardTypeException(ResourceLocation location) {
        super("Unknown reward type: " + location.toString());
    }
}
