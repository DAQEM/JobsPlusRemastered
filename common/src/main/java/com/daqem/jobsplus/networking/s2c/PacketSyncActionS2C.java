package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.job.action.ActionManager;
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

public class PacketSyncActionS2C extends BaseS2CMessage {

    private final JsonElement actionJson;

    public PacketSyncActionS2C(ResourceLocation location, JsonElement actionJson) {
        actionJson.getAsJsonObject().addProperty("location", location.toString());
        this.actionJson = actionJson;
    }

    public PacketSyncActionS2C(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readAnySizeNbt();
        this.actionJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, tag).getOrThrow(false, e -> {
        });
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_ACTION;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (actionJson == null) {
            buffer.writeNbt(new CompoundTag());
        } else {
            buffer.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, actionJson).getOrThrow(false, e -> {
            }).getFirst());
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (!Minecraft.getInstance().isLocalServer()) {
            ResourceLocation location = new ResourceLocation(actionJson.getAsJsonObject().get("location").getAsString());
            actionJson.getAsJsonObject().remove("location");
            ActionManager.getInstance().apply(Map.of(location, actionJson), false);

        }
    }
}
