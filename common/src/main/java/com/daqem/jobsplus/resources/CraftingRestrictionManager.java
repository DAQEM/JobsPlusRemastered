package com.daqem.jobsplus.resources;

import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CraftingRestrictionManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(CraftingRestriction.class, new CraftingRestriction.Deserializer<>())
            .create();
    private static final Logger LOGGER = LogUtils.getLogger();

    protected ImmutableMap<ResourceLocation, List<CraftingRestriction>> restrictions = ImmutableMap.of();

    public CraftingRestrictionManager() {
        super(GSON, "crafting_restrictions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {

        Map<ResourceLocation, List<CraftingRestriction>> tempRestrictions = new HashMap<>();
        AtomicInteger count = new AtomicInteger();

        map.forEach((location, jsonElement) -> {
            try {
                JsonArray restrictions = GsonHelper.getAsJsonArray(jsonElement.getAsJsonObject(), "restrictions");
                List<CraftingRestriction> tempCraftingRestrictions = new ArrayList<>();

                for (JsonElement restriction : restrictions) {
                    CraftingRestriction craftingRestriction = GSON.fromJson(restriction, CraftingRestriction.class);
                    tempCraftingRestrictions.add(craftingRestriction);
                }

                tempRestrictions.put(location, tempCraftingRestrictions);
                count.addAndGet(tempCraftingRestrictions.size());
            } catch (Exception e) {
                LOGGER.error("Could not deserialize crafting restriction {} because: {}", location.toString(), e.getMessage());
                throw e;
            }
        });

        LOGGER.info("Loaded {} crafting restrictions for {} jobs", count.get(), tempRestrictions.size());

        this.restrictions = ImmutableMap.copyOf(tempRestrictions);

        applyCraftingRestrictionToJobs();
    }

    private void applyCraftingRestrictionToJobs() {
        for (Map.Entry<ResourceLocation, List<CraftingRestriction>> entry : restrictions.entrySet()) {
            ResourceLocation jobLocation = entry.getKey();
            List<CraftingRestriction> craftingRestrictions = entry.getValue();
            JobInstance job = JobManager.getInstance().getJobInstance(jobLocation);
            if (job != null) {
                job.setCraftingRestrictions(craftingRestrictions);
            } else {
                LOGGER.error("Could not find job {} to apply crafting restrictions to", jobLocation.toString());
            }
        }
    }
}
