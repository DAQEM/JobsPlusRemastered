package com.daqem.jobsplus.event.server;

import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.JobManager;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.ActionManager;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class EventServerStart {

    public static void registerEvent() {
        LifecycleEvent.SERVER_BEFORE_START.register(server -> {
            assembleJobInstances();
        });
    }

    private static void assembleJobInstances() {
        Map<ResourceLocation, JobInstance> jobInstances = JobManager.getInstance().getJobs();
        Map<ResourceLocation, Action> actions = ActionManager.getInstance().getActions();

        for (Map.Entry<ResourceLocation, JobInstance> jobInstance : jobInstances.entrySet()) {
            JobInstance job = jobInstance.getValue();
            job.setActions(actions.values().stream().filter(action -> action.getJobLocation().equals(job.getLocation())).toList());
        }
    }

}
