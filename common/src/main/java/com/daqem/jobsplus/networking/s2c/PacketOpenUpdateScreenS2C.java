package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.client.screen.ConfirmationScreen;
import com.daqem.jobsplus.client.screen.JobsScreen;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.utils.ConfirmationMessageType;
import com.daqem.jobsplus.resources.job.JobInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PacketOpenUpdateScreenS2C extends BaseS2CMessage {

    private final CompoundTag serverData;

    public PacketOpenUpdateScreenS2C(CompoundTag serverData) {
        this.serverData = serverData;
    }

    public PacketOpenUpdateScreenS2C(FriendlyByteBuf buffer) {
        this.serverData = buffer.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_OPEN_UPDATE_SCREEN;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(serverData);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof LocalPlayer) {
            JobsScreen jobsScreen = new JobsScreen(serverData);
            Minecraft.getInstance().setScreen(new ConfirmationScreen(
                    jobsScreen,
                    ConfirmationMessageType.JOBS_PLUS_UPDATE,
                    (JobInstance) null));
        }
    }
}
