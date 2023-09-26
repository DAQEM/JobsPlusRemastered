package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.forge.data.JobManagerForge;
import com.daqem.jobsplus.forge.data.PowerupManagerForge;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;
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

    public static PowerupManager getPowerupManager() {
        return new PowerupManagerForge();
    }
}
