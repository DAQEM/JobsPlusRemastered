package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketTogglePowerUpC2S extends BaseC2SMessage {

    private final JobInstance jobInstance;
    private final PowerupInstance powerupInstance;

    public PacketTogglePowerUpC2S(JobInstance jobInstance, PowerupInstance powerupInstance) {
        this.jobInstance = jobInstance;
        this.powerupInstance = powerupInstance;
    }

    public PacketTogglePowerUpC2S(FriendlyByteBuf friendlyByteBuf) {
        this.jobInstance = JobInstance.of(friendlyByteBuf.readResourceLocation());
        this.powerupInstance = PowerupInstance.of(friendlyByteBuf.readResourceLocation());
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_TOGGLE_POWER_UP;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(jobInstance.getLocation());
        buf.writeResourceLocation(powerupInstance.getLocation());
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            Job job = serverPlayer.jobsplus$getJob(jobInstance);
            if (job != null) {
                Powerup powerup = job.getPowerupManager().getPowerup(powerupInstance);
                if (powerup != null) {
                    powerup.toggle();
                    serverPlayer.jobsplus$updateJob(job);
                }
            }
        }
    }
}
