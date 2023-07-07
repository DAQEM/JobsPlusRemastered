package com.daqem.jobsplus;

import com.daqem.jobsplus.data.crafting.CraftingRestrictionManager;

import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupManager;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class JobsPlusExpectPlatform {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static JobManager getJobManager() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CraftingRestrictionManager getCraftingRestrictionManager() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static PowerupManager getPowerupManager() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
}
