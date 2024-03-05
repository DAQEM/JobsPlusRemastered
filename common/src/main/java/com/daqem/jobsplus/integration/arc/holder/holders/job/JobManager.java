package com.daqem.jobsplus.integration.arc.holder.holders.job;

import com.daqem.arc.api.action.holder.ActionHolderManager;
import com.daqem.arc.data.ActionManager;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.config.JobsPlusConfig;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.integration.arc.holder.type.JobsPlusActionHolderType;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JobManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(JobInstance.class, new JobInstance.Serializer())
            .create();

    public static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, JobInstance> jobs = ImmutableMap.of();

    private static JobManager instance;

    public JobManager() {
        super(GSON, "jobsplus/jobs");
        instance = this;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<ResourceLocation, JobInstance> tempJobInstances = new HashMap<>();

        map.forEach((location, jsonElement) -> {
            try {
                JobInstance job = GSON.fromJson(jsonElement.getAsJsonObject(), JobInstance.class);
                if (!job.isDefault() || (job.isDefault() && JobsPlusConfig.enableDefaultJobs.get())) {
                    job.setLocation(location);
                    tempJobInstances.put(location, job);
                    ActionHolderManager.getInstance().registerActionHolder(job);

                    job.clearActions();
                    ActionManager.getInstance().getActions().stream()
                            .filter(action -> action.getActionHolderType() == JobsPlusActionHolderType.JOB_INSTANCE && action.getActionHolderLocation().equals(location))
                            .forEach(job::addAction);
                }
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location.toString(), e.getMessage());
            }
        });

        LOGGER.info("Loaded {} jobs", tempJobInstances.size());
        this.jobs = ImmutableMap.copyOf(tempJobInstances);
    }

    public static JobManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getJobManager();
    }

    public JobInstance getJobInstance(ResourceLocation location) {
        return jobs.get(location);
    }

    public Map<ResourceLocation, JobInstance> getJobs() {
        return jobs;
    }

    public void replaceJobs(List<JobInstance> jobs) {
        Map<ResourceLocation, JobInstance> tempJobInstances = new HashMap<>();
        jobs.forEach(job -> {
            tempJobInstances.put(job.getLocation(), job);
            ActionHolderManager.getInstance().registerActionHolder(job);
        });
        this.jobs = ImmutableMap.copyOf(tempJobInstances);
        JobsPlus.LOGGER.info("Updated {} jobs", tempJobInstances.size());
    }

    public void addPowerups(ImmutableMap<ResourceLocation, PowerupInstance> powerups) {
        Map<ResourceLocation, List<PowerupInstance>> powerupsByJob = powerups.values().stream().collect(Collectors.groupingBy(PowerupInstance::getJobLocation));

        powerupsByJob.forEach((jobLocation, powerupInstances) -> {
            if (jobs.containsKey(jobLocation)) {
                JobInstance jobInstance = jobs.get(jobLocation);
                if (jobInstance != null) {
                    jobInstance.setPowerups(powerupInstances);
                }
            } else {
                LOGGER.error("Could not find job {} for powerups {}", jobLocation.toString(), powerupInstances.stream().map(PowerupInstance::getLocation).collect(Collectors.toList()));
            }
        });
    }
}
