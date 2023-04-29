package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.JobsPlus;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JobsPlus.MOD_ID)
public class JobsPlusForge {
    public JobsPlusForge() {
        EventBuses.registerModEventBus(JobsPlus.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        JobsPlus.init();

        DistExecutor.safeRunForDist(
                () -> SideProxyForge.Client::new,
                () -> SideProxyForge.Server::new
        );
    }
}
