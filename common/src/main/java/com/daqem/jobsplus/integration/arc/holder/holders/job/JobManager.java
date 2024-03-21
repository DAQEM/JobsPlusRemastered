package com.daqem.jobsplus.integration.arc.holder.holders.job;

import com.daqem.arc.api.action.holder.ActionHolderManager;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.config.JobsPlusConfig;
import com.daqem.jobsplus.integration.arc.holder.type.JobsPlusActionHolderType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JobManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(JobInstance.class, new JobInstance.Serializer())
            .create();

    public static final Logger LOGGER = LogUtils.getLogger();

    private static JobManager instance;

    public JobManager() {
        super(GSON, "jobsplus/jobs");
        instance = this;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        ActionHolderManager actionHolderManager = ActionHolderManager.getInstance();
        actionHolderManager.clearAllActionHoldersForType(JobsPlusActionHolderType.JOB_INSTANCE);
        Map<ResourceLocation, JobInstance> tempJobInstances = new HashMap<>();

        map.forEach((location, jsonElement) -> {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonObject.addProperty("location", location.toString());
                JobInstance job = GSON.fromJson(jsonObject, JobInstance.class);
                if (!job.isDefault() || (job.isDefault() && JobsPlusConfig.enableDefaultJobs.get())) {
                    tempJobInstances.put(location, job);
                }
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location.toString(), e.getMessage());
            }
        });

        LOGGER.info("Loaded {} jobs", tempJobInstances.size());
        actionHolderManager.registerActionHolders(new ArrayList<>(tempJobInstances.values()));
    }

    public static JobManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getJobManager();
    }

    public Map<ResourceLocation, JobInstance> getJobs() {
        return ActionHolderManager.getInstance().getActionHolders().stream()
                .filter(actionHolder -> actionHolder instanceof JobInstance)
                .map(actionHolder -> (JobInstance) actionHolder)
                .collect(Collectors.toMap(JobInstance::getLocation, jobInstance -> jobInstance));
    }
}
