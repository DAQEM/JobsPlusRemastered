package com.daqem.jobsplus.event.client;

import com.daqem.jobsplus.networking.c2s.PacketSyncRequest;
import com.daqem.jobsplus.resources.JobManager;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.Minecraft;

public class EventSyncRequest {

    public static void registerEvent() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((player) -> {
            if (player.getLevel().isClientSide()) {
                if (!Minecraft.getInstance().isLocalServer()) {
                    JobManager.getInstance().clearAll();
                    new PacketSyncRequest().sendToServer();
                }
            }
        });
    }
}


