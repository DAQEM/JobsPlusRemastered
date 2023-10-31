package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.screen.JobsScreen;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PacketOpenPowerupsMenuS2C extends BaseS2CMessage {

    private final JobInstance jobInstance;
    private final CompoundTag serverData;

    public PacketOpenPowerupsMenuS2C(JobInstance jobInstance, CompoundTag serverData) {
        this.jobInstance = jobInstance;
        this.serverData = serverData;
    }

    public PacketOpenPowerupsMenuS2C(FriendlyByteBuf buffer) {
        this.jobInstance = JobInstance.of(buffer.readResourceLocation());
        this.serverData = buffer.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_OPEN_POWERUPS_MENU;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(jobInstance.getLocation());
        buf.writeNbt(serverData);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof LocalPlayer) {
            try {
                new JobsScreen(serverData).openPowerupsScreenForJobInstance(jobInstance);
            } catch (Exception e) {
                JobsPlus.LOGGER.error("Error opening powerups menu: " + e);
            }
        }
    }
}
