package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.resources.JobManager;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class JobsPlusExpectPlatformImpl {
    /**
     * This is our actual method to {@link JobsPlusExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    /**
     * This is our actual method to {@link JobsPlusExpectPlatform#getJobManager()}.
     */
    public static JobManager getJobManager() {
        return SideProxyForge.getJobManager();
    }
}
