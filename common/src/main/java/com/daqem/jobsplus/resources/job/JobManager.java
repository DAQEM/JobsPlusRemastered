package com.daqem.jobsplus.resources.job;

import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.ActionForType;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
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
            .registerTypeHierarchyAdapter(JobInstance.class, new JobInstance.JobInstanceSerializer())
            .create();

    public static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, JobInstance> jobs = ImmutableMap.of();

    protected ImmutableMap<ResourceLocation, JsonElement> map = ImmutableMap.of();

    private static JobManager instance;

    public JobManager() {
        super(GSON, "jobs");
        instance = this;
    }

    public void apply(@NotNull Map<ResourceLocation, JsonElement> map, boolean isServer) {
        Map<ResourceLocation, JobInstance> tempJobInstances = new HashMap<>();

        map.forEach((location, jsonElement) -> {
            try {
                JobInstance job = GSON.fromJson(jsonElement.getAsJsonObject(), JobInstance.class);
                if (!job.isDefault() || (job.isDefault() && JobsPlusCommonConfig.enableDefaultJobs.get())) {
                    job.setLocation(location);
                    tempJobInstances.put(location, job);
                }
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location.toString(), e.getMessage());
                throw e;
            }
        });

        if (isServer) {
            LOGGER.info("Loaded {} jobs", tempJobInstances.size());
            this.jobs = ImmutableMap.copyOf(tempJobInstances);
        } else {
            tempJobInstances.forEach((location, job) -> {
                Map<ResourceLocation, JobInstance> tempJobInstance = new HashMap<>(jobs);
                tempJobInstance.remove(location);
                tempJobInstance.put(location, job);
                jobs = ImmutableMap.copyOf(tempJobInstance);
            });
        }
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.jobs = null;
        this.map = ImmutableMap.copyOf(map);
        apply(map, true);
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

    public void setJobs(Map<ResourceLocation, JobInstance> jobs) {
        this.jobs = ImmutableMap.copyOf(jobs);
    }

    public Map<ResourceLocation, JsonElement> getMap() {
        return map;
    }

    public void clearAll() {
        jobs = ImmutableMap.of();
        map = ImmutableMap.of();
    }

    public void addPowerups(ImmutableMap<ResourceLocation, PowerupInstance> powerups) {
        Map<ResourceLocation, List<PowerupInstance>> powerupsByJob = powerups.values().stream().collect(Collectors.groupingBy(PowerupInstance::getJobLocation));

        powerupsByJob.forEach((jobLocation, powerupInstances) -> {
            if (jobs.containsKey(jobLocation)) {
                LOGGER.info("Adding {} powerups to job {}", powerupInstances.size(), jobLocation.toString());
                jobs.get(jobLocation).setPowerups(powerupInstances);
            } else {
                LOGGER.error("Could not find job {} for powerups {}", jobLocation.toString(), powerupInstances.stream().map(PowerupInstance::getLocation).collect(Collectors.toList()));
            }
        });
    }

    public void addActions(ImmutableMap<ResourceLocation, Action> actions) {
        addJobActions(actions.values().stream().filter(action -> action.getForType() == ActionForType.JOB).collect(Collectors.toList()));
        addPowerupActions(actions.values().stream().filter(action -> action.getForType() == ActionForType.POWERUP).collect(Collectors.toList()));
    }

    private void addJobActions(List<Action> collect) {
        Map<ResourceLocation, List<Action>> actionsByJob = collect.stream().collect(Collectors.groupingBy(Action::getForLocation));

        actionsByJob.forEach((jobLocation, actions) -> {
            if (jobs.containsKey(jobLocation)) {
                LOGGER.info("Adding {} actions to job {}", actions.size(), jobLocation.toString());
                jobs.get(jobLocation).setActions(actions);
            } else {
                LOGGER.error("Could not find job {} for actions {}", jobLocation.toString(), actions.stream().map(Action::getLocation).collect(Collectors.toList()));
            }
        });
    }

    private void addPowerupActions(List<Action> collect) {
        Map<ResourceLocation, List<Action>> actionsByPowerup = collect.stream().collect(Collectors.groupingBy(Action::getForLocation));
        actionsByPowerup.forEach((powerupLocation, actions) -> {
            if (PowerupManager.getInstance().getAllPowerups().containsKey(powerupLocation)) {
                PowerupManager.getInstance().getAllPowerups().get(powerupLocation).setActions(actions);
            } else {
                LOGGER.error("Could not find powerup {} for actions {}", powerupLocation.toString(), actions.stream().map(Action::getLocation).collect(Collectors.toList()));
            }
        });
    }


}
