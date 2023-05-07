package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.forge.resources.CraftingRestrictionManagerForge;
import com.daqem.jobsplus.forge.resources.JobManagerForge;
import com.daqem.jobsplus.resources.CraftingRestrictionManager;
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

    public static JobManager getJobManager() {
        return new JobManagerForge();
    }

    public static CraftingRestrictionManager getCraftingRestrictionManager() {
        return new CraftingRestrictionManagerForge();
    }
}
