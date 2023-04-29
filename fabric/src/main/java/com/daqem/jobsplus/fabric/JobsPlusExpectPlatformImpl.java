package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.config.ICommonConfig;
import com.daqem.jobsplus.resources.JobManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class JobsPlusExpectPlatformImpl {
    /**
     * This is our actual method to {@link JobsPlusExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    /**
     * This is our actual method to {@link JobsPlusExpectPlatform#getJobManager()}.
     */
    public static JobManager getJobManager() {
        return JobsPlusFabric.getJobManager();
    }

    public static ICommonConfig getCommonConfig() {
        return JobsPlusFabric.getCommonConfig();
    }
}
