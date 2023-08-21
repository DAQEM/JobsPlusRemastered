package com.daqem.jobsplus.networking.sync.jobs;

import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.job.Job;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundRemoveJobPacket extends BaseS2CMessage {

    private final ResourceLocation jobLocation;

    public ClientboundRemoveJobPacket(ResourceLocation jobLocation) {
        this.jobLocation = jobLocation;
    }

    public ClientboundRemoveJobPacket(FriendlyByteBuf friendlyByteBuf) {
        this.jobLocation = friendlyByteBuf.readResourceLocation();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.CLIENTBOUND_REMOVE_JOB;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(jobLocation);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsClientPlayer player) {
            player.jobsplus$removeJob(JobInstance.of(jobLocation));
        }
    }
}
