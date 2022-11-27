package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class JobsPlusExpectPlatformImpl {
    /**
     * This is our actual method to {@link JobsPlusExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
