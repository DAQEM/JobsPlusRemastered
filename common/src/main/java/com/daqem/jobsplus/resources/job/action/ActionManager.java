package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
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
import java.util.List;
import java.util.Map;

public abstract class ActionManager extends SimpleJsonResourceReloadListener {

    private static Gson getGson(ResourceLocation location) {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Action.class, new Action.ActionSerializer<>(location))
                .create();
    }

    public static final Logger LOGGER = LogUtils.getLogger();
    protected ImmutableMap<ResourceLocation, Action> actions = ImmutableMap.of();

    protected ImmutableMap<ResourceLocation, JsonElement> map = ImmutableMap.of();

    private static ActionManager instance;

    public ActionManager() {
        super(getGson(null), "job_actions");
        instance = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        List<Action> tempActions = new ArrayList<>();

        object.forEach((location, jsonElement) -> {
            JobsPlus.LOGGER.error("Loading job action {}", location.toString());
            try {
                Action action = getGson(location).fromJson(jsonElement, Action.class);
                tempActions.add(action);
            } catch (Exception e) {
                LOGGER.error("Could not deserialize job {} because: {}", location, e.getMessage());
                throw e;
            }
        });

        actions = ImmutableMap.copyOf(tempActions.stream().collect(ImmutableMap.toImmutableMap(Action::getLocation, action -> action)));
        LOGGER.info("Loaded {} job actions", actions.size());
    }

    public static ActionManager getInstance() {
        return instance != null ? instance : JobsPlusExpectPlatform.getActionManager();
    }

    public Map<ResourceLocation, Action> getActions() {
        return actions;
    }
}
