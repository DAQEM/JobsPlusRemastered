package com.daqem.jobsplus.networking.utils;

import com.daqem.jobsplus.JobsPlus;
import net.minecraft.network.chat.Component;

public class ConfirmationMessage {

    private final String message;
    private Object[] objects;

    public ConfirmationMessage(String message) {
        this.message = message;
    }

    public void withObjects(Object... objects) {
        this.objects = objects;
    }

    public Component getComponent() {
        if (objects == null)
            return JobsPlus.translatable("gui.confirmation." + message);
        return JobsPlus.translatable("gui.confirmation." + message, objects);
    }
}
