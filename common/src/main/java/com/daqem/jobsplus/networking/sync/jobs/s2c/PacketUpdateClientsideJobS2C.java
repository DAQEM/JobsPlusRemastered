package com.daqem.jobsplus.networking.sync.jobs.s2c;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PacketUpdateClientsideJobS2C extends BaseS2CMessage {

    private final CompoundTag jobTag;

    public PacketUpdateClientsideJobS2C(CompoundTag jobTag) {
        this.jobTag = jobTag;
    }

    public PacketUpdateClientsideJobS2C(FriendlyByteBuf friendlyByteBuf) {
        this.jobTag = friendlyByteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_UPDATE_CLIENTSIDE_JOB;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(jobTag);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsClientPlayer clientPlayer) {
            JobsPlus.LOGGER.info("PacketUpdateClientsideJobS2C");
            Job job = Job.fromNBT(clientPlayer, jobTag);
            clientPlayer.getJobs().removeIf(j -> j.getJobInstance().getLocation() == job.getJobInstance().getLocation());
            clientPlayer.getJobs().add(job);
        }
    }
}
