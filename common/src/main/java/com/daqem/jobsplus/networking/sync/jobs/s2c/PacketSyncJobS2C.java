package com.daqem.jobsplus.networking.sync.jobs.s2c;

import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.player.job.Job;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSyncJobS2C extends BaseS2CMessage {

    private final CompoundTag jobTag;

    public PacketSyncJobS2C(CompoundTag jobTag) {
        this.jobTag = jobTag;
    }

    public PacketSyncJobS2C(FriendlyByteBuf friendlyByteBuf) {
        this.jobTag = friendlyByteBuf.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_JOBS;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(jobTag);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsClientPlayer clientPlayer) {
            clientPlayer.getJobs().add(Job.fromNBT(clientPlayer, jobTag));
        }
    }
}
