package com.daqem.jobsplus.player.action;

import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.ActionType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ActionData {

    private final JobsPlayer player;
    private final ActionType actionType;
    private final Map<ActionSpecification<?>, Object> specifications;
    private Job sourceJob;

    public ActionData(JobsPlayer player, ActionType actionType, Map<ActionSpecification<?>, Object> specifications) {
        this.player = player;
        this.actionType = actionType;
        this.specifications = specifications;
        this.sourceJob = null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getSpecification(ActionSpecification<T> specification) {
        return (T) this.specifications.get(specification);
    }

    public JobsPlayer getPlayer() {
        return player;
    }

    public Job getSourceJob() {
        return sourceJob;
    }

    public void setSourceJob(Job sourceJob) {
        this.sourceJob = sourceJob;
    }

//    public boolean sendToAction() {
//        AtomicBoolean shouldCancel = new AtomicBoolean(false);
//
//        List<Job> jobs = this.player.getJobs();
//        for (Job job : jobs) {
//            job.getJobInstance().getActions().forEach(action -> {
//                shouldCancel.set(handleAction(job, action));
//            });
//            job.getPowerupManager().getAllPowerups().forEach(powerup -> {
//                if (powerup.getPowerupState() == PowerupState.ACTIVE) {
//                    powerup.getPowerupInstance().getActions().forEach(action -> {
//                        this.specifications.put(ActionSpecification.POWERUP, powerup);
//                         shouldCancel.set(handleAction(job, action));
//                    });
//                }
//            });
//        }
//        return shouldCancel.get();
//    }
//
//    private boolean handleAction(Job job, Action action) {
//        AtomicBoolean shouldCancel = new AtomicBoolean(false);
//
//        if (action.getType() == this.actionType) {
//            if (this.specifications.containsKey(ActionSpecification.ONLY_FOR_JOB)) {
//                Job onlyForJob = this.getSpecification(ActionSpecification.ONLY_FOR_JOB);
//                if (job.equals(onlyForJob)) {
//                    this.setSourceJob(job);
//                    shouldCancel.set(action.perform(this));
//                }
//            } else {
//                this.setSourceJob(job);
//                shouldCancel.set(action.perform(this));
//            }
//        }
//        return shouldCancel.get();
//    }

    public ActionResult sendToAction() {
        return this.player.getJobs().stream()
                .map(this::handleJob)
                .reduce(new ActionResult(), ActionResult::combine);
    }

    private ActionResult handleJob(Job job) {
        ActionResult result = job.getJobInstance().getActions().stream()
                .filter(action -> action.getType() == this.actionType)
                .map(action -> handleAction(job, action))
                .reduce(new ActionResult(), ActionResult::combine);

        if (result.shouldCancelAction()) return result;

        return job.getPowerupManager().getAllPowerups().stream()
                .filter(powerup -> powerup.getPowerupState() == PowerupState.ACTIVE)
                .map(powerup -> handlePowerupAction(job, powerup))
                .reduce(new ActionResult(), ActionResult::combine);
    }

    private ActionResult handlePowerupAction(Job job, Powerup powerup) {
        this.specifications.put(ActionSpecification.POWERUP, powerup);
        return powerup.getPowerupInstance().getActions().stream()
                .filter(action -> action.getType() == this.actionType)
                .map(action -> handleAction(job, action))
                .reduce(new ActionResult(), ActionResult::combine);
    }

    private ActionResult handleAction(Job job, Action action) {
        Job onlyForJob = this.getSpecification(ActionSpecification.ONLY_FOR_JOB);
        if (this.specifications.containsKey(ActionSpecification.ONLY_FOR_JOB) && !job.equals(onlyForJob)) {
            return new ActionResult();
        }

        this.setSourceJob(job);
        return action.perform(this);
    }
}
