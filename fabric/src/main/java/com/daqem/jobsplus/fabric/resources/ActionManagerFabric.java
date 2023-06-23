package com.daqem.jobsplus.fabric.resources;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.resources.job.action.ActionManager;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ActionManagerFabric extends ActionManager implements IdentifiableResourceReloadListener {

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        super.apply(object, resourceManager, profilerFiller);
    }

    @Override
    public ResourceLocation getFabricId() {
        return JobsPlus.getId("job_actions");
    }
}
