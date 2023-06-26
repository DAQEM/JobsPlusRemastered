package com.daqem.jobsplus.resources.job.powerup;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.jobsplus.resources.job.JobManager;
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
            .registerTypeHierarchyAdapter(PowerupInstance.class, new PowerupInstance.PowerupSerializer())
            .create();

    public static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, PowerupInstance> powerups = ImmutableMap.of();

    protected ImmutableMap<ResourceLocation, JsonElement> map = ImmutableMap.of();

    private static PowerupManager instance;

    public PowerupManager() {
        super(GSON, "job_powerups");
        instance = this;
    }


    public void apply(@NotNull Map<ResourceLocation, JsonElement> map, boolean isServer) {
        List<PowerupInstance> tempPowerups = new ArrayList<>();

        map.forEach((location, jsonElement) -> {
            JobsPlus.LOGGER.error("Loading job powerup {}", location.toString());
            try {
                PowerupInstance powerup = GSON.fromJson(jsonElement, PowerupInstance.class);
                powerup.setLocation(location);
                tempPowerups.add(powerup);
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location, e.getMessage());
                throw e;
            }
        });

        if (isServer) {
            powerups = ImmutableMap.copyOf(sortPowerups(tempPowerups));
            LOGGER.info("Loaded {} job powerups", powerups.size());
            JobManager.getInstance().addPowerups(powerups);
        } else {
            tempPowerups.forEach(powerup -> {
                Map<ResourceLocation, PowerupInstance> tempPowerupInstance = new HashMap<>(powerups);
                tempPowerupInstance.remove(powerup.getLocation());
                tempPowerupInstance.put(powerup.getLocation(), powerup);
                tempPowerupInstance = sortPowerups(new ArrayList<>(tempPowerupInstance.values()));
                powerups = ImmutableMap.copyOf(tempPowerupInstance);
                JobManager.getInstance().addPowerups(powerups);
            });
        }
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.powerups = null;
        this.map = ImmutableMap.copyOf(map);
        this.apply(map, true);
    }

    public static PowerupManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getPowerupManager();
    }

    public ImmutableMap<ResourceLocation, PowerupInstance> getRootPowerups() {
        return powerups;
    }

    public ImmutableMap<ResourceLocation, JsonElement> getMap() {
        return map;
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

    public Map<ResourceLocation, PowerupInstance> getAllPowerupsRecursive(Map<ResourceLocation, PowerupInstance> powerups) {
        Map<ResourceLocation, PowerupInstance> allPowerups = new HashMap<>();
        powerups.forEach((location, powerup) -> {
            allPowerups.put(location, powerup);
            allPowerups.putAll(getAllPowerupsRecursive(powerup.getChildren().stream().collect(ImmutableMap.toImmutableMap(PowerupInstance::getLocation, powerupInstance -> powerupInstance))));
        });
        return allPowerups;
    }
}
