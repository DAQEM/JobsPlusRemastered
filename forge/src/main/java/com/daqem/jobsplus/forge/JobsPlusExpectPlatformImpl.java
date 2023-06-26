package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.forge.resources.ActionManagerForge;
import com.daqem.jobsplus.forge.resources.CraftingRestrictionManagerForge;
import com.daqem.jobsplus.forge.resources.JobManagerForge;
import com.daqem.jobsplus.forge.resources.PowerupManagerForge;
import com.daqem.jobsplus.resources.crafting.CraftingRestrictionManager;
import com.daqem.jobsplus.resources.job.JobManager;
import com.daqem.jobsplus.resources.job.action.ActionManager;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
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

    public static ActionManager getActionManager() {
        return new ActionManagerForge();
    }

    public static PowerupManager getPowerupManager() {
        return new PowerupManagerForge();
    }
}
