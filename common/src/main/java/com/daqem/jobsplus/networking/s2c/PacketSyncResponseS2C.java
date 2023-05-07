package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.CraftingRestrictionManager;
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

import javax.annotation.Nullable;
import java.util.Map;

public class PacketSyncResponseS2C extends BaseS2CMessage {

    private final JsonElement jobJson;
    @Nullable
    private final JsonElement restrictionJson;

    public PacketSyncResponseS2C(ResourceLocation location, JsonElement jobJson, @Nullable JsonElement restrictionJson) {
        jobJson.getAsJsonObject().addProperty("location", location.toString());
        if (restrictionJson != null) {
            restrictionJson.getAsJsonObject().addProperty("location", location.toString());
        }
        this.jobJson = jobJson;
        this.restrictionJson = restrictionJson;
    }

    public PacketSyncResponseS2C(FriendlyByteBuf buffer) {
        CompoundTag jobTag = buffer.readAnySizeNbt();
        if (jobTag != null) {
            this.jobJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, jobTag).getOrThrow(false, e -> {
            });
        } else {
            this.jobJson = new JsonObject();
        }

        CompoundTag restrictionTag = buffer.readAnySizeNbt();
        this.restrictionJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, restrictionTag).getOrThrow(false, e -> {
        });
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_RESPONSE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, jobJson).getOrThrow(false, e -> {
        }).getFirst());
        if (restrictionJson == null) {
            buf.writeNbt(new CompoundTag());
        } else {
            buf.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, restrictionJson).getOrThrow(false, e -> {
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

            if (restrictionJson != null && restrictionJson.getAsJsonObject().has("restrictions")) {
                restrictionJson.getAsJsonObject().remove("location");
                CraftingRestrictionManager.getInstance().apply(Map.of(location, restrictionJson), false);
            }
        }
    }
}
