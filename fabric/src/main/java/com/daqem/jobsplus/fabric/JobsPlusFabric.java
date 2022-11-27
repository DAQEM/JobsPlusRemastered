package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlus;
import net.fabricmc.api.ModInitializer;

public class JobsPlusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        JobsPlus.init();
    }
}
