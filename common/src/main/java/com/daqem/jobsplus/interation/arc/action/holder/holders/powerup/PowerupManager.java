package com.daqem.jobsplus.interation.arc.action.holder.holders.powerup;

import com.daqem.arc.api.action.holder.ActionHolderManager;
import com.daqem.arc.data.ActionManager;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.interation.arc.action.holder.type.JobsPlusActionHolderType;
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

import java.util.ArrayList;
import java.util.HashMap;
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
        List<PowerupInstance> tempPowerups = new ArrayList<>();

        map.forEach((location, jsonElement) -> {
            try {
                PowerupInstance powerup = GSON.fromJson(jsonElement, PowerupInstance.class);
                powerup.setLocation(location);
                tempPowerups.add(powerup);
                ActionHolderManager.getInstance().registerActionHolder(powerup);

                powerup.clearActions();
                ActionManager.getInstance().getActions().stream()
                        .filter(action -> action.getActionHolderType() == JobsPlusActionHolderType.POWERUP_INSTANCE && action.getActionHolderLocation().equals(location))
                        .forEach(powerup::addAction);
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location, e.getMessage());
                throw e;
            }
        });

        powerups = ImmutableMap.copyOf(sortPowerups(tempPowerups));
        JobManager.getInstance().addPowerups(powerups);
        LOGGER.info("Loaded {} job powerups", getAllPowerups().size());
    }

    public static PowerupManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getPowerupManager();
    }

    public ImmutableMap<ResourceLocation, PowerupInstance> getRootPowerups() {
        return powerups;
    }

    public Map<ResourceLocation, PowerupInstance> sortPowerups(List<PowerupInstance> powerupInstances) {
        powerupInstances.forEach(powerupInstance -> {
            if (powerupInstance.getParentLocation() != null) {
                PowerupInstance parent = getPowerupFromList(powerupInstances, powerupInstance.getParentLocation());
                if (parent != null) {
                    powerupInstance.setParent(parent);
                    parent.addChild(powerupInstance);
                }
            }
        });

        return powerupInstances.stream()
                .filter(powerupInstance -> powerupInstance.getParent() == null)
                .collect(ImmutableMap.toImmutableMap(PowerupInstance::getLocation, powerup -> powerup));
    }

    public PowerupInstance getPowerupFromList(List<PowerupInstance> powerupInstances, ResourceLocation location) {
        return powerupInstances.stream().filter(powerupInstance -> powerupInstance.getLocation().equals(location)).findFirst().orElse(null);
    }

    public Map<ResourceLocation, PowerupInstance> getAllPowerups() {
        return getAllPowerupsRecursive(powerups);
    }

    private Map<ResourceLocation, PowerupInstance> getAllPowerupsRecursive(Map<ResourceLocation, PowerupInstance> powerups) {
        Map<ResourceLocation, PowerupInstance> allPowerups = new HashMap<>();
        powerups.forEach((location, powerup) -> {
            allPowerups.put(location, powerup);
            allPowerups.putAll(getAllPowerupsRecursive(powerup.getChildren().stream().collect(ImmutableMap.toImmutableMap(PowerupInstance::getLocation, powerupInstance -> powerupInstance))));
        });
        return allPowerups;
    }

    public void replacePowerups(List<PowerupInstance> powerupInstances) {
        powerups = ImmutableMap.copyOf(sortPowerups(powerupInstances));
        JobManager.getInstance().addPowerups(powerups);
        JobsPlus.LOGGER.info("Updated {} powerups", powerups.size());
    }
}
