package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketTogglePowerUpC2S extends BaseC2SMessage {

    private final PowerupInstance powerupInstance;

    public PacketTogglePowerUpC2S(PowerupInstance powerupInstance) {
        this.powerupInstance = powerupInstance;
    }

    public PacketTogglePowerUpC2S(FriendlyByteBuf friendlyByteBuf) {
        this.powerupInstance = PowerupInstance.of(friendlyByteBuf.readResourceLocation());
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_TOGGLE_POWER_UP;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(powerupInstance.getLocation());
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            if (serverPlayer.hasPowerup(powerupInstance)) {
                serverPlayer.togglePowerup(powerupInstance);
            }
        }
    }
}
