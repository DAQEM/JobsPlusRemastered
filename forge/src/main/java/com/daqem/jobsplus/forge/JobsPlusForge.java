package com.daqem.jobsplus.forge;

import dev.architectury.platform.forge.EventBuses;
import com.daqem.jobsplus.JobsPlus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JobsPlus.MOD_ID)
public class JobsPlusForge {
    public JobsPlusForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(JobsPlus.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        JobsPlus.init();
    }
}
