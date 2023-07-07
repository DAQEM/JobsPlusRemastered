package com.daqem.jobsplus.data.crafting.restriction;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.exception.UnknownCraftingRestrictionTypeException;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.data.crafting.restriction.restrictions.ItemCraftingRestriction;
import com.daqem.jobsplus.data.crafting.restriction.restrictions.TagCraftingRestriction;
import com.google.gson.JsonDeserializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CraftingRestrictions {

    public static final List<CraftingRestrictionType> CRAFTING_RESTRICTION_TYPES = new ArrayList<>();

    public static final CraftingRestrictionType ITEM = register(JobsPlus.getId("item"), ItemCraftingRestriction.class, new ItemCraftingRestriction.Deserializer());
    public static final CraftingRestrictionType TAG = register(JobsPlus.getId("tag"), TagCraftingRestriction.class, new TagCraftingRestriction.Deserializer());

    private static <T extends CraftingRestriction> CraftingRestrictionType register(ResourceLocation location, Class<T> clazz, JsonDeserializer<? extends CraftingRestriction> deserializer) {
        CraftingRestrictionType craftingRestrictionType = new CraftingRestrictionType(clazz, location, deserializer);
        CRAFTING_RESTRICTION_TYPES.add(craftingRestrictionType);
        return Registry.register(JobsPlusRegistry.CRAFTING_RESTRICTION, location, craftingRestrictionType);
    }

    public static Class<? extends CraftingRestriction> getClass(ResourceLocation location) throws UnknownCraftingRestrictionTypeException {
        CraftingRestrictionType craftingRestrictionType = JobsPlusRegistry.CRAFTING_RESTRICTION.get(location);
        if (craftingRestrictionType == null) {
            throw new UnknownCraftingRestrictionTypeException(location);
        }
        return craftingRestrictionType.clazz();
    }

}
