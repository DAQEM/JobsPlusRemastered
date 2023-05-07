package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.client.screen.JobsScreen;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PacketOpenMenuS2C extends BaseS2CMessage {

    private final CompoundTag serverData;

    public PacketOpenMenuS2C(CompoundTag serverData) {
        this.serverData = serverData;
    }

    public PacketOpenMenuS2C(FriendlyByteBuf buffer) {
        this.serverData = buffer.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_OPEN_MENU;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(serverData);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof LocalPlayer) {
            try {
                Minecraft.getInstance().setScreen(new JobsScreen(serverData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
