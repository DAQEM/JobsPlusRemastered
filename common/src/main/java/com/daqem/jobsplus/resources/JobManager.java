package com.daqem.jobsplus.resources;

import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class JobManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(JobInstance.class, new JobInstance.JobInstanceSerializer())
            .create();

    public static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, JobInstance> jobs = ImmutableMap.of();
    protected ImmutableMap<ResourceLocation, PowerupInstance> powerups = ImmutableMap.of();

    protected ImmutableMap<ResourceLocation, JsonElement> map = ImmutableMap.of();

    private static JobManager instance;

    public JobManager() {
        super(GSON, "jobs");
        instance = this;
    }

    public void apply(Map<ResourceLocation, JsonElement> map, boolean isServer) {
        Map<ResourceLocation, JobInstance> tempJobInstances = new HashMap<>();
        Map<ResourceLocation, PowerupInstance> tempPowerupInstances = new HashMap<>();

        map.forEach((location, jsonElement) -> {
            try {
                JobInstance job = GSON.fromJson(jsonElement.getAsJsonObject(), JobInstance.class);
                if (!job.isDefault() || (job.isDefault() && JobsPlusCommonConfig.enableDefaultJobs.get())) {
                    job.setLocations(location);
                    for (PowerupInstance powerupInstance : job.getPowerups()) {
                        tempPowerupInstances.put(powerupInstance.getLocation(), powerupInstance);
                    }
                    tempJobInstances.put(location, job);
                }
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location.toString(), e.getMessage());
                throw e;
            }
        });

        if (isServer) {
            LOGGER.info("Loaded {} jobs and {} powerups", tempJobInstances.size(), tempPowerupInstances.size());
            this.jobs = ImmutableMap.copyOf(tempJobInstances);
            this.powerups = ImmutableMap.copyOf(tempPowerupInstances);
        } else {
            tempJobInstances.forEach((location, job) -> {
                Map<ResourceLocation, JobInstance> tempJobInstance = new HashMap<>(jobs);
                tempJobInstance.remove(location);
                tempJobInstance.put(location, job);
                jobs = ImmutableMap.copyOf(tempJobInstance);
            });
            tempPowerupInstances.forEach((location, powerup) -> {
                Map<ResourceLocation, PowerupInstance> tempPowerupInstance = new HashMap<>(powerups);
                tempPowerupInstance.remove(location);
                tempPowerupInstance.put(location, powerup);
                powerups = ImmutableMap.copyOf(tempPowerupInstance);
            });
        }
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.jobs = null;
        this.powerups = null;
        this.map = ImmutableMap.copyOf(map);
        this.apply(map, true);
    }

    public static JobManager getInstance() {
        return instance;
    }

    public JobInstance getJob(ResourceLocation location) {
        return jobs.get(location);
    }

    public Map<ResourceLocation, JobInstance> getJobs() {
        return jobs;
    }

    public Map<ResourceLocation, PowerupInstance> getPowerups() {
        return powerups;
    }

    public Map<ResourceLocation, JsonElement> getMap() {
        return map;
    }

    public void clearAll() {
        jobs = ImmutableMap.of();
        powerups = ImmutableMap.of();
        map = ImmutableMap.of();
    }
}
