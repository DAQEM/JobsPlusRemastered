package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.forge.data.JobManagerForge;
import com.daqem.jobsplus.forge.data.PowerupManagerForge;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;

public class JobsPlusExpectPlatformImpl {

    public static JobManager getJobManager() {
        return new JobManagerForge();
    }

    public static PowerupManager getPowerupManager() {
        return new PowerupManagerForge();
    }
}
