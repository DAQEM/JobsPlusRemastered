package com.daqem.jobsplus.event.client;

import com.daqem.jobsplus.client.player.JobsClientPlayer;

import com.daqem.jobsplus.networking.sync.instances.c2s.PacketSyncJobInstancesRequestC2S;
import com.daqem.jobsplus.data.crafting.CraftingRestrictionManager;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.Minecraft;

public class EventSyncRequest {

    public static void registerEvent() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((player) -> {
            if (player instanceof JobsClientPlayer) {
                if (!Minecraft.getInstance().isLocalServer()) {
                    CraftingRestrictionManager.getInstance().clearAll();
                    new PacketSyncJobInstancesRequestC2S().sendToServer();
                }
            }
        });
    }
}


