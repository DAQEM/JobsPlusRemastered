package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public class PacketConfirmationC2S extends BaseC2SMessage {

    private final ConfirmationMessageType type;
    private final int price;
    private final @Nullable JobInstance jobInstance;
    private final @Nullable PowerupInstance powerupInstance;

    public PacketConfirmationC2S(ConfirmationMessageType type, int price, @Nullable JobInstance jobInstance) {
        this.type = type;
        this.price = price;
        this.jobInstance = jobInstance;
        this.powerupInstance = null;
    }

    public PacketConfirmationC2S(ConfirmationMessageType type, int price, @Nullable PowerupInstance powerupInstance) {
        this.type = type;
        this.price = price;
        this.jobInstance = null;
        this.powerupInstance = powerupInstance;
    }

    public PacketConfirmationC2S(FriendlyByteBuf friendlyByteBuf) {
        this.type = ConfirmationMessageType.values()[friendlyByteBuf.readVarInt()];
        this.price = friendlyByteBuf.readVarInt();
        this.jobInstance = JobInstance.of(friendlyByteBuf.readResourceLocation());
        this.powerupInstance = PowerupInstance.of(friendlyByteBuf.readResourceLocation());
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_CONFIRMATION;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(type.ordinal());
        buf.writeVarInt(price);
        if (jobInstance != null) {
            buf.writeResourceLocation(jobInstance.getLocation());
        }
        if (powerupInstance != null) {
            buf.writeResourceLocation(powerupInstance.getLocation());
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            if (serverPlayer.getCoins() >= price) {
                boolean success = false;
                if (type == ConfirmationMessageType.BUY_POWER_UP) {
                    if (powerupInstance != null) {
                        if (!serverPlayer.hasPowerup(powerupInstance)) {
                            serverPlayer.addPowerup(powerupInstance);
                            success = true;
                        }
                    }
                } else if (jobInstance != null) {
                    switch (type) {
                        case START_JOB_FREE -> {
                            if (!serverPlayer.hasJob(jobInstance)) {
                                serverPlayer.addNewJob(jobInstance);
                            }
                        }
                        case STOP_JOB_FREE -> {
                            if (serverPlayer.hasJob(jobInstance)) {
                                serverPlayer.removeJob(jobInstance);
                            }
                        }
                        case START_JOB_PAID -> {
                            if (!serverPlayer.hasJob(jobInstance)) {
                                serverPlayer.addNewJob(jobInstance);
                                success = true;
                            }
                        }
                        case STOP_JOB_PAID -> {
                            if (serverPlayer.hasJob(jobInstance)) {
                                serverPlayer.removeJob(jobInstance);
                                success = true;
                            }
                        }
                    }
                }
                if (success) {
                    serverPlayer.setCoins(serverPlayer.getCoins() - price);
                }
            }
        }
    }
}
