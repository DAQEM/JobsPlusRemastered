package com.daqem.jobsplus.forge.registry;

import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.NewRegistryEvent;

public class JobsPlusRegistryForge {

    @SubscribeEvent
    public static void onRegisterRegistries(NewRegistryEvent event) {
//        JobsPlusRegistry.JOB_INSTANCE = new MappedRegistry<>(JobsPlusRegistry.JOB_INSTANCE_KEY, Lifecycle.experimental(), null);
//        JobsPlusRegistry.POWERUP_INSTANCE = new MappedRegistry<>(JobsPlusRegistry.POWERUP_INSTANCE_KEY, Lifecycle.experimental(), null);
        JobsPlusRegistry.ACTION = new MappedRegistry<>(JobsPlusRegistry.ACTION_KEY, Lifecycle.experimental(), null);
        JobsPlusRegistry.ACTION_REWARD = new MappedRegistry<>(JobsPlusRegistry.ACTION_REWARD_KEY, Lifecycle.experimental(), null);
        JobsPlusRegistry.ACTION_CONDITION = new MappedRegistry<>(JobsPlusRegistry.ACTION_CONDITION_KEY, Lifecycle.experimental(), null);
    }
}
