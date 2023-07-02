package com.daqem.jobsplus.networking.sync.jobs.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.sync.jobs.s2c.PacketSyncJobS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketRequestJobsSyncC2S extends BaseC2SMessage {

    public PacketRequestJobsSyncC2S() {
    }

    public PacketRequestJobsSyncC2S(FriendlyByteBuf ignoredFriendlyByteBuf) {
    }

    @Override
    public MessageType getType() {

        return JobsPlusNetworking.C2S_SYNC_JOBS_REQUEST;
    }

    @Override
    public void write(FriendlyByteBuf ignoredFriendlyByteBuf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            serverPlayer.getJobs().forEach((job) ->
                    new PacketSyncJobS2C(job.toNBT()).sendTo(serverPlayer.getServerPlayer()));
        }
    }
}
