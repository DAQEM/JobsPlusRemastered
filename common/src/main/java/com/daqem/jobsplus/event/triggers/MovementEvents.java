package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.action.ActionDataBuilder;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;

public class MovementEvents {

    public static void onSwim(JobsServerPlayer player, int swimmingDistanceInCm) {
        new ActionDataBuilder(player, Actions.SWIM)
                .withSpecification(ActionSpecification.DISTANCE_IN_CM, swimmingDistanceInCm)
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

    public static void onWalk(JobsServerPlayer player, int walkingDistanceInCm) {
        new ActionDataBuilder(player, Actions.WALK)
                .withSpecification(ActionSpecification.DISTANCE_IN_CM, walkingDistanceInCm)
                .build()
                .sendToAction();
    }

    public static void onStopWalking(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.WALK_STOP)
                .build()
                .sendToAction();
    }

    public static void onStartWalking(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.WALK_START)
                .build()
                .sendToAction();
    }

    public static void onSprint(JobsServerPlayer player, int sprintingDistanceInCm) {
        new ActionDataBuilder(player, Actions.SPRINT)
                .withSpecification(ActionSpecification.DISTANCE_IN_CM, sprintingDistanceInCm)
                .build()
                .sendToAction();
    }

    public static void onStopSprinting(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.SPRINT_STOP)
                .build()
                .sendToAction();
    }

    public static void onStartSprinting(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.SPRINT_START)
                .build()
                .sendToAction();
    }

    public static void onCrouch(JobsServerPlayer player, int crouchDistanceInCm) {
        new ActionDataBuilder(player, Actions.CROUCH)
                .withSpecification(ActionSpecification.DISTANCE_IN_CM, crouchDistanceInCm)
                .build()
                .sendToAction();
    }

    public static void onStopCrouching(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.CROUCH_STOP)
                .build()
                .sendToAction();
    }

    public static void onStartCrouching(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.CROUCH_START)
                .build()
                .sendToAction();
    }

    public static void onElytraFly(JobsServerPlayer player, int flyingDistanceInCm) {
        new ActionDataBuilder(player, Actions.ELYTRA_FLY)
                .withSpecification(ActionSpecification.DISTANCE_IN_CM, flyingDistanceInCm)
                .build()
                .sendToAction();
    }

    public static void onStopElytraFlying(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.ELYTRA_FLY_STOP)
                .build()
                .sendToAction();
    }

    public static void onStartElytraFlying(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.ELYTRA_FLY_START)
                .build()
                .sendToAction();
    }
}
