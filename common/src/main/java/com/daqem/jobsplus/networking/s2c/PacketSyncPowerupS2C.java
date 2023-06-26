package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class PacketSyncPowerupS2C extends BaseS2CMessage {

    private final JsonElement PowerupJson;

    public PacketSyncPowerupS2C(ResourceLocation location, JsonElement powerupJson) {
        powerupJson.getAsJsonObject().addProperty("location", location.toString());
        this.PowerupJson = powerupJson;
    }

    public PacketSyncPowerupS2C(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readAnySizeNbt();
        this.PowerupJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, tag).getOrThrow(false, e -> {
        });
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_POWERUP;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (PowerupJson == null) {
            buffer.writeNbt(new CompoundTag());
        } else {
            buffer.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, PowerupJson).getOrThrow(false, e -> {
            }).getFirst());
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (!Minecraft.getInstance().isLocalServer()) {
            ResourceLocation location = new ResourceLocation(PowerupJson.getAsJsonObject().get("location").getAsString());
            PowerupJson.getAsJsonObject().remove("location");
            PowerupManager.getInstance().apply(Map.of(location, PowerupJson), false);

        }
    }
}
