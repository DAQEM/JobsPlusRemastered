package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.fabric.resources.CraftingRestrictionManagerFabric;
import com.daqem.jobsplus.fabric.resources.JobManagerFabric;
import com.daqem.jobsplus.resources.CraftingRestrictionManager;
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

    public static JobManager getJobManager() {
        return new JobManagerFabric();
    }

    public static CraftingRestrictionManager getCraftingRestrictionManager() {
        return new CraftingRestrictionManagerFabric();
    }
}
