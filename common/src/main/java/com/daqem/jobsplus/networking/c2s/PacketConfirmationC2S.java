package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketConfirmationC2S extends BaseC2SMessage {

    private final ConfirmationMessageType type;
    private final int price;
    private final @Nullable JobInstance jobInstance;
    private final @Nullable PowerupInstance powerupInstance;

    public PacketConfirmationC2S(ConfirmationMessageType type, @NotNull JobInstance jobInstance) {
        this.type = type;
        this.price = type.getRequireCoinsType() == ConfirmationMessageType.RequireCoinsType.NONE ? 0 : jobInstance.getPrice();
        this.jobInstance = jobInstance;
        this.powerupInstance = null;
    }

    public PacketConfirmationC2S(ConfirmationMessageType type, @NotNull JobInstance jobInstance, @NotNull PowerupInstance powerupInstance) {
        this.type = type;
        this.price = powerupInstance.getPrice();
        this.jobInstance = jobInstance;
        this.powerupInstance = powerupInstance;
    }

    public PacketConfirmationC2S(FriendlyByteBuf friendlyByteBuf) {
        this.type = ConfirmationMessageType.values()[friendlyByteBuf.readVarInt()];
        this.price = friendlyByteBuf.readVarInt();

        String jobInstanceString = friendlyByteBuf.readUtf();
        ResourceLocation jobInstanceLocation = new ResourceLocation(jobInstanceString);
        this.jobInstance = jobInstanceString.isEmpty() ? null : JobInstance.of(jobInstanceLocation);

        String powerupInstanceString = friendlyByteBuf.readUtf();
        ResourceLocation powerupInstanceLocation = new ResourceLocation(powerupInstanceString);
        this.powerupInstance = powerupInstanceString.isEmpty() ? null : PowerupInstance.of(powerupInstanceLocation);
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_CONFIRMATION;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(type.ordinal());
        buf.writeVarInt(price);
        buf.writeUtf(jobInstance == null ? "" : jobInstance.getLocation().toString());
        buf.writeUtf(powerupInstance == null ? "" : powerupInstance.getLocation().toString());
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            if (serverPlayer.jobsplus$getCoins() >= price || type.getRequireCoinsType() == ConfirmationMessageType.RequireCoinsType.NONE) {
                boolean hasToPay = false;
                if (type == ConfirmationMessageType.BUY_POWER_UP) {
                    Job job = serverPlayer.jobsplus$getJob(jobInstance);
                    if (job != null) {
                        hasToPay = job.getPowerupManager().addPowerup(serverPlayer, job, powerupInstance);
                    }
                } else if (jobInstance != null) {
                    Job job = serverPlayer.jobsplus$getJob(jobInstance);
                    switch (type) {
                        case START_JOB_FREE -> {
                            if (job == null) {
                                serverPlayer.jobsplus$addNewJob(jobInstance);
                            }
                        }
                        case START_JOB_PAID -> {
                            if (job == null) {
                                serverPlayer.jobsplus$addNewJob(jobInstance);
                                hasToPay = true;
                            }
                        }
                        case STOP_JOB -> {
                            if (job != null) {
                                serverPlayer.jobsplus$removeAndRefundJob(jobInstance);
                            }
                        }
                    }
                }
                if (hasToPay) {
                    serverPlayer.jobsplus$setCoins(serverPlayer.jobsplus$getCoins() - price);
                }
            }
        }
    }
}
