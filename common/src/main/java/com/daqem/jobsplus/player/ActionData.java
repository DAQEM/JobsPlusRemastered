package com.daqem.jobsplus.player;

import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.ActionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ActionData {

    private final JobsServerPlayer player;
    private final ActionType actionType;
    private final Map<ActionSpecification<?>, Object> specifications;
    private Job sourceJob;

    public ActionData(JobsServerPlayer player, ActionType actionType, Map<ActionSpecification<?>, Object> specifications) {
        this.player = player;
        this.actionType = actionType;
        this.specifications = specifications;
        this.sourceJob = null;
    }

    public boolean hasSpecification(ActionSpecification<?> specification) {
        return this.specifications.containsKey(specification);
    }

    @Nullable
    public <T> T getSpecification(ActionSpecification<T> specification) {
        return (T) this.specifications.get(specification);
    }

    public JobsServerPlayer getPlayer() {
        return player;
    }

    public Job getSourceJob() {
        return sourceJob;
    }

    public void setSourceJob(Job sourceJob) {
        this.sourceJob = sourceJob;
    }

    public void sendToAction() {
        List<Job> jobs = this.player.getJobs();
        for (Job job : jobs) {
            job.getJobInstance().getActions().forEach(action -> {
                handleAction(job, action);
            });
            job.getPowerupManager().getAllPowerups().forEach(powerup -> {
                if (powerup.getPowerupState() == PowerupState.ACTIVE) {
                    powerup.getPowerupInstance().getActions().forEach(action -> {
                        this.specifications.put(ActionSpecification.POWERUP, powerup);
                        handleAction(job, action);
                    });
                }
            });
        }
    }

    private void handleAction(Job job, Action action) {
        if (action.getType() == this.actionType) {
            if (this.specifications.containsKey(ActionSpecification.ONLY_FOR_JOB)) {
                Job onlyForJob = this.getSpecification(ActionSpecification.ONLY_FOR_JOB);
                if (job.equals(onlyForJob)) {
                    this.setSourceJob(job);
                    action.perform(this);
                }
            } else {
                this.setSourceJob(job);
                action.perform(this);
            }
        }
    }
}
