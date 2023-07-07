package com.daqem.jobsplus.registry;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.data.crafting.restriction.CraftingRestrictionType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class JobsPlusRegistry {

    public static final ResourceKey<Registry<CraftingRestrictionType>> CRAFTING_RESTRICTION_KEY = ResourceKey.createRegistryKey(JobsPlus.getId("crafting_restriction"));

    public static Registry<CraftingRestrictionType> CRAFTING_RESTRICTION;
}
