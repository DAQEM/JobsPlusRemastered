package com.daqem.jobsplus.player.action;

import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.resources.job.action.ActionType;

import java.util.HashMap;
import java.util.Map;

public class ActionDataBuilder {

    private final JobsPlayer player;
    private final ActionType actionType;
    private final Map<ActionSpecification<?>, Object> specifications;

    public ActionDataBuilder(JobsPlayer player, ActionType actionType) {
        this.player = player;
        this.actionType = actionType;
        this.specifications = new HashMap<>();
    }

    public <T> ActionDataBuilder withSpecification(ActionSpecification<T> specification, T value) {
        this.specifications.put(specification, value);
        return this;
    }

    public ActionData build() {
        return new ActionData(this.player, this.actionType, this.specifications);
    }
}
