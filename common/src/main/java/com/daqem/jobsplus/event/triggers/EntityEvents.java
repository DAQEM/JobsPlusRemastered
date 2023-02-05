package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;

public class EntityEvents {

    public static void registerEvents() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.DEATH)
                        .withSpecification(ActionSpecification.DAMAGE_SOURCE, source)
                        .build()
                        .sendToAction();
            } else if (source.getEntity() instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.KILL_ENTITY)
                        .withSpecification(ActionSpecification.ENTITY, entity)
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });

        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (entity instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.GET_HURT)
                        .withSpecification(ActionSpecification.DAMAGE_SOURCE, source)
                        .withSpecification(ActionSpecification.DAMAGE_AMOUNT, amount)
                        .build()
                        .sendToAction();
            } else if (source.getEntity() instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.HURT_ENTITY)
                        .withSpecification(ActionSpecification.ENTITY, entity)
                        .withSpecification(ActionSpecification.DAMAGE_AMOUNT, amount)
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });
    }
}
