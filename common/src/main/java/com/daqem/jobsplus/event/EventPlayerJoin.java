package com.daqem.jobsplus.event;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupManager;
import com.daqem.jobsplus.networking.sync.ClientboundUpdateJobInstancesPacket;
import com.daqem.jobsplus.networking.sync.ClientboundUpdatePowerupInstancesPacket;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobsPacket;
import com.daqem.jobsplus.player.JobsPlayer;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.dedicated.DedicatedServer;

public class EventPlayerJoin {

    public static void registerEvent() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    if (player.server instanceof DedicatedServer) {
                        new ClientboundUpdateJobInstancesPacket(JobManager.getInstance().getJobs().values().stream().toList()).sendTo(player);
                        new ClientboundUpdatePowerupInstancesPacket(PowerupManager.getInstance().getAllPowerups().values().stream().toList()).sendTo(player);
                    }
                    if (player instanceof JobsPlayer jobsPlayer) {
                        new ClientboundUpdateJobsPacket(jobsPlayer.jobsplus$getJobs()).sendTo(player);
                    }
                } catch (InterruptedException e) {
                    JobsPlus.LOGGER.error("Error while sending data to player on join");
                }
            }).start();
        });
    }
}
