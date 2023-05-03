package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.client.screen.JobsCraftingScreen;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class PacketCantCraftS2C extends BaseS2CMessage {

    private final CompoundTag cantCraftData;

    public PacketCantCraftS2C(ResourceLocation itemLocation, ResourceLocation jobLocation, int requiredLevel) {
        CompoundTag tag = new CompoundTag();
        tag.putString("ItemLocation", itemLocation.toString());
        tag.putString("JobLocation", jobLocation.toString());
        tag.putInt("RequiredLevel", requiredLevel);
        this.cantCraftData = tag;
    }

    public PacketCantCraftS2C(FriendlyByteBuf buffer) {
        this.cantCraftData = buffer.readAnySizeNbt();
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_CANT_CRAFT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(cantCraftData);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof LocalPlayer) {
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof JobsCraftingScreen) {
                ((JobsCraftingScreen) currentScreen).cantCraft(cantCraftData);
            }
        }
    }
}
