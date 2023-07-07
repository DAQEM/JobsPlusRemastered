package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.s2c.PacketOpenPowerupsMenuS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.JobSerializer;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class PacketOpenPowerupsMenuC2S extends BaseC2SMessage {

    private final JobInstance jobInstance;

    public PacketOpenPowerupsMenuC2S(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }

    public PacketOpenPowerupsMenuC2S(FriendlyByteBuf friendlyByteBuf) {
        this.jobInstance = JobInstance.of(friendlyByteBuf.readResourceLocation());
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_OPEN_POWERUPS_MENU;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(jobInstance.getLocation());
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {

            CompoundTag serverData = new CompoundTag();
            ListTag listTag = JobSerializer.toNBT(serverPlayer.jobsplus$getJobs());

            listTag.addAll(serverPlayer.jobsplus$inactiveJobsToNBT());
            serverData.put(Constants.JOBS, listTag);
            serverData.putInt(Constants.COINS, serverPlayer.jobsplus$getCoins());

            new PacketOpenPowerupsMenuS2C(jobInstance, serverData).sendTo(((JobsServerPlayer) context.getPlayer()).jobsplus$getServerPlayer());
        }
    }
}
