package com.daqem.jobsplus;

import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class JobsPlusExpectPlatform {

    @ExpectPlatform
    public static JobManager getJobManager() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static PowerupManager getPowerupManager() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
}
