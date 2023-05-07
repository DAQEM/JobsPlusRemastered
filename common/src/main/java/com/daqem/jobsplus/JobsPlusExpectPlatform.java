package com.daqem.jobsplus;

import com.daqem.jobsplus.resources.CraftingRestrictionManager;
import com.daqem.jobsplus.resources.JobManager;
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
}
