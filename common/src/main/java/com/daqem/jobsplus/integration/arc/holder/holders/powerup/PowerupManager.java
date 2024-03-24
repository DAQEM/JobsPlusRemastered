package com.daqem.jobsplus.integration.arc.holder.holders.powerup;

import com.daqem.arc.api.action.holder.ActionHolderManager;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.type.JobsPlusActionHolderType;
import com.google.common.collect.ImmutableMap;
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
import java.util.List;
import java.util.Map;

public abstract class PowerupManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PowerupInstance.class, new PowerupInstance.Serializer())
            .create();

    private static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, PowerupInstance> powerups = ImmutableMap.of();

    private static PowerupManager instance;

    public PowerupManager() {
        super(GSON, "jobsplus/powerups");
        instance = this;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        ActionHolderManager actionHolderManager = ActionHolderManager.getInstance();
        actionHolderManager.clearAllActionHoldersForType(JobsPlusActionHolderType.POWERUP_INSTANCE);
        List<PowerupInstance> tempPowerups = new ArrayList<>();

        map.forEach((location, jsonElement) -> {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonObject.addProperty("location", location.toString());
                PowerupInstance powerup = GSON.fromJson(jsonObject, PowerupInstance.class);
                tempPowerups.add(powerup);
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location, e.getMessage());
                throw e;
            }
        });

        LOGGER.info("Loaded {} job powerups", tempPowerups.size());
        actionHolderManager.registerActionHolders(new ArrayList<>(tempPowerups));
    }

    public static PowerupManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getPowerupManager();
    }

    public ImmutableMap<ResourceLocation, PowerupInstance> getRootPowerups() {
        return getAllPowerups().entrySet().stream()
                .filter(entry -> entry.getValue().getParentLocation() == null)
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<ResourceLocation, PowerupInstance> getAllPowerups() {
        return ActionHolderManager.getInstance().getActionHolders().stream()
                .filter(actionHolder -> actionHolder instanceof PowerupInstance)
                .map(actionHolder -> (PowerupInstance) actionHolder)
                .collect(ImmutableMap.toImmutableMap(PowerupInstance::getLocation, powerupInstance -> powerupInstance));
    }
}
