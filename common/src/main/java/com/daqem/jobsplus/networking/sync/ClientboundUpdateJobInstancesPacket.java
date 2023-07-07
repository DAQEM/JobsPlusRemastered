package com.daqem.jobsplus.networking.sync;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class ClientboundUpdateJobInstancesPacket extends BaseS2CMessage {

    private final List<JobInstance> jobInstances;

    public ClientboundUpdateJobInstancesPacket(List<JobInstance> jobInstances) {
        this.jobInstances = jobInstances;
    }

    public ClientboundUpdateJobInstancesPacket(FriendlyByteBuf friendlyByteBuf) {
        this.jobInstances = friendlyByteBuf.readList(friendlyByteBuf1 ->
                new JobInstance.Serializer().fromNetwork(friendlyByteBuf1));
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.CLIENTBOUND_UPDATE_JOB_INSTANCES;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(jobInstances, (friendlyByteBuf1, jobInstance) ->
                new JobInstance.Serializer().toNetwork(friendlyByteBuf1, jobInstance));
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        JobManager.getInstance().replaceJobs(jobInstances);
    }
}
