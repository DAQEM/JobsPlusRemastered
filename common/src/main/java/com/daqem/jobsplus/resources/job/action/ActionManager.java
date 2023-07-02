package com.daqem.jobsplus.resources.job.action;

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

public abstract class ActionManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Action.class, new Action.ActionSerializer<>())
            .create();

    public static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, Action> actions = ImmutableMap.of();

    protected ImmutableMap<ResourceLocation, JsonElement> map = ImmutableMap.of();

    private static ActionManager instance;

    public ActionManager() {
        super(GSON, "job_actions");
        instance = this;
    }

    public void apply(Map<ResourceLocation, JsonElement> object, boolean isServer) {
        List<Action> tempActions = new ArrayList<>();

        object.forEach((location, jsonElement) -> {
            try {
                Action action = GSON.fromJson(jsonElement, Action.class);
                action.setLocation(location);
                tempActions.add(action);
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location, e.getMessage());
                throw e;
            }
        });

        if (isServer) {
            actions = ImmutableMap.copyOf(tempActions.stream().collect(ImmutableMap.toImmutableMap(Action::getLocation, action -> action)));
            LOGGER.info("Loaded {} job actions", actions.size());
            JobManager.getInstance().addActions(actions);
        } else {
            tempActions.forEach(action -> {
                Map<ResourceLocation, Action> tempActionsMap = new HashMap<>(actions);
                tempActionsMap.remove(action.getLocation());
                tempActionsMap.put(action.getLocation(), action);
                actions = ImmutableMap.copyOf(tempActionsMap);
                JobManager.getInstance().addActions(actions);
            });
        }


    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.actions = null;
        this.map = ImmutableMap.copyOf(map);
        this.apply(map, true);
    }

    public static ActionManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getActionManager();
    }

    public Map<ResourceLocation, Action> getActions() {
        return actions;
    }

    public ImmutableMap<ResourceLocation, JsonElement> getMap() {
        return map;
    }

    public void clearAll() {
        actions = ImmutableMap.of();
        map = ImmutableMap.of();
    }
}
