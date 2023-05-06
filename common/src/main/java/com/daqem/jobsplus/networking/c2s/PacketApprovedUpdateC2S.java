package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.JobsServerPlayer;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketApprovedUpdateC2S extends BaseC2SMessage {

    public PacketApprovedUpdateC2S() {
    }

    public PacketApprovedUpdateC2S(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_APPROVED_UPDATE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            serverPlayer.setUpdatedFromOldJobsPlus(false);
        }
    }
}
