package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.action.ActionDataBuilder;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;
import dev.architectury.event.events.common.PlayerEvent;

public class AdvancementEvents {

    public static void registerEvents() {
        PlayerEvent.PLAYER_ADVANCEMENT.register((player, advancement) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.ADVANCEMENT)
                        .withSpecification(ActionSpecification.ADVANCEMENT, advancement)
                        .build()
                        .sendToAction();
            }
        });
    }
}
