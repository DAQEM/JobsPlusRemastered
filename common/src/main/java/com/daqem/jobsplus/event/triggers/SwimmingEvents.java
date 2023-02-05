package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;

public class SwimmingEvents {

    public static void onSwim(JobsServerPlayer player, int swimmingDistanceInCm) {
        new ActionDataBuilder(player, Actions.SWIM)
                .withSpecification(ActionSpecification.SWIMMING_DISTANCE_IN_CM, swimmingDistanceInCm)
                .build()
                .sendToAction();
    }

    public static void onStartSwimming(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.SWIM_START)
                .build()
                .sendToAction();
    }

    public static void onStopSwimming(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.SWIM_STOP)
                .build()
                .sendToAction();
    }
}
