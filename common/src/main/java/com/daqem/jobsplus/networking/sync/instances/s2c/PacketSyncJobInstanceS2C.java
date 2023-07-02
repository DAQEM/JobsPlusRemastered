package com.daqem.jobsplus.networking.sync.instances.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.job.JobManager;
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

public class PacketSyncJobInstanceS2C extends BaseS2CMessage {

    private final JsonElement jobJson;

    public PacketSyncJobInstanceS2C(ResourceLocation location, JsonElement jobJson) {
        jobJson.getAsJsonObject().addProperty("location", location.toString());
        this.jobJson = jobJson;
    }

    public PacketSyncJobInstanceS2C(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readAnySizeNbt();
        this.jobJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, tag).getOrThrow(false, e -> {
        });
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_JOB_INSTANCE;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (jobJson == null) {
            buffer.writeNbt(new CompoundTag());
        } else {
            buffer.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, jobJson).getOrThrow(false, e -> {
            }).getFirst());
        }
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
