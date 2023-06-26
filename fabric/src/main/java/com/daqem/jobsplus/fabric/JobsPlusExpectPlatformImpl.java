package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.fabric.resources.ActionManagerFabric;
import com.daqem.jobsplus.fabric.resources.CraftingRestrictionManagerFabric;
import com.daqem.jobsplus.fabric.resources.JobManagerFabric;
import com.daqem.jobsplus.fabric.resources.PowerupManagerFabric;
import com.daqem.jobsplus.resources.crafting.CraftingRestrictionManager;
import com.daqem.jobsplus.resources.job.JobManager;
import com.daqem.jobsplus.resources.job.action.ActionManager;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
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

    public static ActionManager getActionManager() {
        return new ActionManagerFabric();
    }

    public static PowerupManager getPowerupManager() {
        return new PowerupManagerFabric();
    }
}
