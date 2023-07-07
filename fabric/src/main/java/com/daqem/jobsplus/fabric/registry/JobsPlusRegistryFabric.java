package com.daqem.jobsplus.fabric.registry;

import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;

public class JobsPlusRegistryFabric {

    public static void init() {
    }

    static {
        JobsPlusRegistry.CRAFTING_RESTRICTION = new MappedRegistry<>(JobsPlusRegistry.CRAFTING_RESTRICTION_KEY, Lifecycle.experimental(), null);
    }
}
