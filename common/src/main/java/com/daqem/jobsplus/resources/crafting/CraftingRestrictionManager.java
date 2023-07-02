package com.daqem.jobsplus.resources.crafting;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.JobManager;
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

    protected ImmutableMap<ResourceLocation, JsonElement> map = ImmutableMap.of();

    private static CraftingRestrictionManager instance;

    public CraftingRestrictionManager() {
        super(GSON, "crafting_restrictions");
        instance = this;
    }

    public static CraftingRestrictionManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getCraftingRestrictionManager();
    }

    public void apply(Map<ResourceLocation, JsonElement> map, boolean isServer) {
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

        if (isServer) {
            LOGGER.info("Loaded {} crafting restrictions for {} jobs", count.get(), tempRestrictions.size());
            this.restrictions = ImmutableMap.copyOf(tempRestrictions);
            applyCraftingRestrictionsToJobs();
        } else {
            tempRestrictions.forEach((location, craftingRestrictions) -> {
                Map<ResourceLocation, List<CraftingRestriction>> tempCraftingRestrictions = new HashMap<>(restrictions);
                tempCraftingRestrictions.remove(location);
                tempCraftingRestrictions.put(location, craftingRestrictions);
                restrictions = ImmutableMap.copyOf(tempCraftingRestrictions);
                applyCraftingRestrictionToJob(location, craftingRestrictions);
            });
        }

    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.restrictions = null;
        this.map = ImmutableMap.copyOf(map);
        apply(map, true);
    }

    private void applyCraftingRestrictionsToJobs() {
        for (Map.Entry<ResourceLocation, List<CraftingRestriction>> entry : restrictions.entrySet()) {
            ResourceLocation jobLocation = entry.getKey();
            List<CraftingRestriction> craftingRestrictions = entry.getValue();
            applyCraftingRestrictionToJob(jobLocation, craftingRestrictions);
        }
    }

    private void applyCraftingRestrictionToJob(ResourceLocation jobLocation, List<CraftingRestriction> craftingRestrictions) {
        JobInstance job = JobManager.getInstance().getJobInstance(jobLocation);
        if (job != null) {
            job.setCraftingRestrictions(craftingRestrictions);
        } else {
            LOGGER.error("Could not find job {} to apply crafting restrictions to", jobLocation.toString());
        }
    }

    public JsonElement getFromLocation(ResourceLocation location) {
        return map.get(location);
    }

    public ImmutableMap<ResourceLocation, JsonElement> getMap() {
        return map;
    }

    public void clearAll() {
        restrictions = ImmutableMap.of();
        map = ImmutableMap.of();
    }
}
