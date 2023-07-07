package com.daqem.jobsplus.networking.sync.jobs;

import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundUpdateJobPacket extends BaseS2CMessage {

    private final Job job;

    public ClientboundUpdateJobPacket(Job job) {
        this.job = job;
    }

    public ClientboundUpdateJobPacket(FriendlyByteBuf friendlyByteBuf) {
        JobsPlayer player = null;
        if (Minecraft.getInstance().player instanceof JobsPlayer jobsPlayer) {
            player = jobsPlayer;
        }
        this.job = Job.Serializer.fromNetwork(friendlyByteBuf, player);
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.CLIENTBOUND_UPDATE_JOB;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        Job.Serializer.toNetwork(buf, job);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsClientPlayer player) {
            player.jobsplus$replaceJob(job);
        }
    }
}
