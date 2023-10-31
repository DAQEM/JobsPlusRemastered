package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.fabric.data.JobManagerFabric;
import com.daqem.jobsplus.fabric.data.PowerupManagerFabric;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;

public class JobsPlusExpectPlatformImpl {
    public static JobManager getJobManager() {
        return new JobManagerFabric();
    }

    public static PowerupManager getPowerupManager() {
        return new PowerupManagerFabric();
    }
}
