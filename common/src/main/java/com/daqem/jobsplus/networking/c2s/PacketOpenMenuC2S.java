package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.s2c.PacketOpenMenuS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.JobSerializer;
import com.daqem.jobsplus.player.job.display.JobDisplay;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PacketOpenMenuC2S extends BaseC2SMessage {

    public PacketOpenMenuC2S() {
    }

    public PacketOpenMenuC2S(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_OPEN_MENU;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            CompoundTag serverData = new CompoundTag();
            ListTag listTag = JobSerializer.toNBT(serverPlayer.getJobs());

            listTag.addAll(serverPlayer.inactiveJobsToNBT());
            serverData.put(Constants.JOBS, listTag);
            serverData.putInt(Constants.COINS, serverPlayer.getCoins());
            serverData.putString(Constants.DISPLAY, serverPlayer.getDisplay().map(JobDisplay::getLocationString).orElse(""));

            serverData.putInt(Constants.ACTIVE_LEFT_BUTTON, 0);
            serverData.putInt(Constants.ACTIVE_RIGHT_BUTTON, 0);
            serverData.putInt(Constants.SELECTED_BUTTON, -1);
            serverData.putInt(Constants.SCROLL_OFFSET, 0);
            serverData.putInt(Constants.START_INDEX, 0);

            new PacketOpenMenuS2C(serverData).sendTo((ServerPlayer) serverPlayer);
        }
    }
}
