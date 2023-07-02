package com.daqem.jobsplus.event.client;

import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.networking.sync.instances.c2s.PacketSyncJobInstancesRequestC2S;
import com.daqem.jobsplus.networking.sync.jobs.c2s.PacketRequestJobsSyncC2S;
import com.daqem.jobsplus.resources.crafting.CraftingRestrictionManager;
import com.daqem.jobsplus.resources.job.JobManager;
import com.daqem.jobsplus.resources.job.action.ActionManager;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.Minecraft;

public class EventSyncRequest {

    public static void registerEvent() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((player) -> {
            if (player instanceof JobsClientPlayer clientPlayer) {
                if (!Minecraft.getInstance().isLocalServer()) {
                    JobManager.getInstance().clearAll();
                    PowerupManager.getInstance().clearAll();
                    ActionManager.getInstance().clearAll();
                    CraftingRestrictionManager.getInstance().clearAll();
                    new PacketSyncJobInstancesRequestC2S().sendToServer();
                }
                clientPlayer.getJobs().clear();
                new PacketRequestJobsSyncC2S().sendToServer();
            }
        });
    }
}


