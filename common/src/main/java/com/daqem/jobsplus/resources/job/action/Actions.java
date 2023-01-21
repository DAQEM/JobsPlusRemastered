package com.daqem.jobsplus.resources.job.action;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.registry.JobsPlusRegistry;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.action.type.BuildAction;
import com.daqem.jobsplus.resources.job.action.type.JobExpAction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class Actions {

    //register build action
    public static ActionType BUILD = register(JobsPlus.getId("on_build"), BuildAction.class);
    public static ActionType JOB_EXP = register(JobsPlus.getId("on_job_exp"), JobExpAction.class);

    //create register method
    private static ActionType register(ResourceLocation location, Class<? extends Action> clazz) {
        return Registry.register(JobsPlusRegistry.ACTION, location, new ActionType(clazz, location));
    }

    public static Class<? extends Action> getClass(ResourceLocation location) {
        ActionType actionType = JobsPlusRegistry.ACTION.get(location);
        if (actionType == null) {
            JobManager.LOGGER.error("Unknown action type: {}", location.toString());
        }
        return actionType.clazz();
    }
}
