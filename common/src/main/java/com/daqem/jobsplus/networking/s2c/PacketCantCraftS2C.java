package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.client.screen.JobsCraftingScreen;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;

public class PacketCantCraftS2C extends BaseS2CMessage {

    private final CraftingResult craftingResult;

    public PacketCantCraftS2C(CraftingResult craftingResult) {
        this.craftingResult = craftingResult;
    }

    public PacketCantCraftS2C(FriendlyByteBuf buffer) {
        this.craftingResult = CraftingResult.fromNBT(buffer.readAnySizeNbt());
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_CANT_CRAFT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(craftingResult.toNBT());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof LocalPlayer) {
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof JobsCraftingScreen) {
                ((JobsCraftingScreen) currentScreen).cantCraft(craftingResult);
            }
        }
    }
}
