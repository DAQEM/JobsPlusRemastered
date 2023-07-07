package com.daqem.jobsplus.networking.sync.instances.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.data.crafting.CraftingRestrictionManager;
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

import javax.annotation.Nullable;
import java.util.Map;

public class PacketSyncRestrictionS2C extends BaseS2CMessage {

    @Nullable
    private final JsonElement restrictionJson;

    public PacketSyncRestrictionS2C(ResourceLocation location, @Nullable JsonElement restrictionJson) {
        if (restrictionJson != null) {
            restrictionJson.getAsJsonObject().addProperty("location", location.toString());
        }
        this.restrictionJson = restrictionJson;
    }

    public PacketSyncRestrictionS2C(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readAnySizeNbt();
        this.restrictionJson = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, tag).getOrThrow(false, e -> {
        });
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.S2C_SYNC_CRAFTING_RESTRICTIONS;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (restrictionJson == null) {
            buffer.writeNbt(new CompoundTag());
        } else {
            buffer.writeNbt(CompoundTag.CODEC.decode(JsonOps.INSTANCE, restrictionJson).getOrThrow(false, e -> {
            }).getFirst());
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (!Minecraft.getInstance().isLocalServer()) {
            if (restrictionJson != null && restrictionJson.getAsJsonObject().has("restrictions")) {
                ResourceLocation location = new ResourceLocation(restrictionJson.getAsJsonObject().get("location").getAsString());
                restrictionJson.getAsJsonObject().remove("location");
                CraftingRestrictionManager.getInstance().apply(Map.of(location, restrictionJson), false);
            }
        }
    }
}
