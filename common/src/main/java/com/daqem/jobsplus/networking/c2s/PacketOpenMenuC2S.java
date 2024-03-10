package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.screen.JobsScreen;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.s2c.PacketOpenMenuS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PacketOpenMenuC2S extends BaseC2SMessage {

    private final int activeLeftButton;
    private final int activeRightButton;
    private final int selectedButton;
    private final float scrollOffset;
    private final float scrollOffsetRight;
    private final int startIndex;
    private final int startIndexRight;

    public PacketOpenMenuC2S(JobsScreen jobsScreen) {
        this.activeLeftButton = jobsScreen.getActiveLeftButton();
        this.activeRightButton = jobsScreen.getActiveRightButton();
        this.selectedButton = jobsScreen.getSelectedButton();
        this.scrollOffset = jobsScreen.getScrollOffset();
        this.scrollOffsetRight = jobsScreen.getScrollOffsetRight();
        this.startIndex = jobsScreen.getStartIndex();
        this.startIndexRight = jobsScreen.getStartIndexRight();
    }

    public PacketOpenMenuC2S() {
        this.activeLeftButton = 0;
        this.activeRightButton = 0;
        this.selectedButton = -1;
        this.scrollOffset = 0;
        this.scrollOffsetRight = 0;
        this.startIndex = 0;
        this.startIndexRight = 0;
    }

    public PacketOpenMenuC2S(FriendlyByteBuf friendlyByteBuf) {
        this.activeLeftButton = friendlyByteBuf.readInt();
        this.activeRightButton = friendlyByteBuf.readInt();
        this.selectedButton = friendlyByteBuf.readInt();
        this.scrollOffset = friendlyByteBuf.readFloat();
        this.scrollOffsetRight = friendlyByteBuf.readFloat();
        this.startIndex = friendlyByteBuf.readInt();
        this.startIndexRight = friendlyByteBuf.readInt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_OPEN_MENU;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(activeLeftButton);
        buf.writeInt(activeRightButton);
        buf.writeInt(selectedButton);
        buf.writeFloat(scrollOffset);
        buf.writeFloat(scrollOffsetRight);
        buf.writeInt(startIndex);
        buf.writeInt(startIndexRight);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            try {
                CompoundTag serverData = new CompoundTag();
                ListTag listTag = Job.Serializer.toNBT(serverPlayer.jobsplus$getJobs());

                listTag.addAll(serverPlayer.jobsplus$inactiveJobsToNBT());
                serverData.put(Constants.JOBS, listTag);
                serverData.putInt(Constants.COINS, serverPlayer.jobsplus$getCoins());

                serverData.putInt(Constants.ACTIVE_LEFT_BUTTON, activeLeftButton);
                serverData.putInt(Constants.ACTIVE_RIGHT_BUTTON, activeRightButton);
                serverData.putInt(Constants.SELECTED_BUTTON, selectedButton);
                serverData.putFloat(Constants.SCROLL_OFFSET, scrollOffset);
                serverData.putFloat(Constants.SCROLL_OFFSET_RIGHT, scrollOffsetRight);
                serverData.putInt(Constants.START_INDEX, startIndex);
                serverData.putInt(Constants.START_INDEX_RIGHT, startIndexRight);

                new PacketOpenMenuS2C(serverData).sendTo((ServerPlayer) serverPlayer);
            } catch (Exception e) {
                JobsPlus.LOGGER.error("Error opening Jobs+ menu (server side): " + e);
            }
        }
    }
}
