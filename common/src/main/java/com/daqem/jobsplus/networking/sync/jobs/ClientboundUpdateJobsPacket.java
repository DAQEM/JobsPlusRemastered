package com.daqem.jobsplus.networking.sync.jobs;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class ClientboundUpdateJobsPacket extends BaseS2CMessage {

    private final List<Job> jobs;

    public ClientboundUpdateJobsPacket(List<Job> jobs) {
        this.jobs = jobs;
    }

    public ClientboundUpdateJobsPacket(FriendlyByteBuf friendlyByteBuf) {
        JobsPlayer player;
        if (Minecraft.getInstance().player instanceof JobsPlayer jobsPlayer) {
            player = jobsPlayer;
        } else {
            player = null;
        }
        this.jobs = friendlyByteBuf.readList(friendlyByteBuf1 -> Job.Serializer.fromNetwork(friendlyByteBuf1, player));
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.CLIENTBOUND_UPDATE_JOBS;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(jobs, Job.Serializer::toNetwork);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsClientPlayer player) {
            player.jobsplus$replaceJobs(jobs);
        }
    }
}
