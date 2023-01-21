package com.daqem.jobsplus.event.player;

import dev.architectury.event.events.common.PlayerEvent;

public class EventPlayerJoin {

    public static void registerEvent() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {

        });
    }
}
