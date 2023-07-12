package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.fabric.data.JobManagerFabric;
import com.daqem.jobsplus.fabric.data.PowerupManagerFabric;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class JobsPlusExpectPlatformImpl {
    /**
     * This is our actual method to {@link JobsPlusExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static JobManager getJobManager() {
        return new JobManagerFabric();
    }

    public static PowerupManager getPowerupManager() {
        return new PowerupManagerFabric();
    }
}
