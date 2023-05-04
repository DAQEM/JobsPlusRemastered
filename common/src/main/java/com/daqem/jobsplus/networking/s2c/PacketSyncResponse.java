package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.JobManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public class PacketSyncResponse extends BaseS2CMessage {

    private final JsonElement jobJson;

    public PacketSyncResponse(ResourceLocation location, JsonElement jobJson) {
        jobJson.getAsJsonObject().addProperty("location", location.toString());
        this.jobJson = jobJson;
    }

    public PacketSyncResponse(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readAnySizeNbt();
        if (tag != null) {
            this.jobJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, tag).getOrThrow(false, e -> {
            });
        } else {
            this.jobJson = new JsonObject();
        }
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_RESPONSE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, jobJson).getOrThrow(false, e -> {
        }).getFirst());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (!Minecraft.getInstance().isLocalServer()) {
            ResourceLocation location = new ResourceLocation(jobJson.getAsJsonObject().get("location").getAsString());
            jobJson.getAsJsonObject().remove("location");
            JobManager.getInstance().apply(Map.of(location, jobJson), false);
        }
    }
}
