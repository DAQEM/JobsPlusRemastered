package com.daqem.jobsplus.forge.registry;

import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.NewRegistryEvent;

public class JobsPlusRegistryForge {

    @SubscribeEvent
    public static void onRegisterRegistries(NewRegistryEvent event) {
        JobsPlusRegistry.ACTION = new MappedRegistry<>(JobsPlusRegistry.ACTION_KEY, Lifecycle.experimental(), null);
        JobsPlusRegistry.ACTION_REWARD = new MappedRegistry<>(JobsPlusRegistry.ACTION_REWARD_KEY, Lifecycle.experimental(), null);
        JobsPlusRegistry.ACTION_CONDITION = new MappedRegistry<>(JobsPlusRegistry.ACTION_CONDITION_KEY, Lifecycle.experimental(), null);
        JobsPlusRegistry.CRAFTING_RESTRICTION = new MappedRegistry<>(JobsPlusRegistry.CRAFTING_RESTRICTION_KEY, Lifecycle.experimental(), null);
    }
}
