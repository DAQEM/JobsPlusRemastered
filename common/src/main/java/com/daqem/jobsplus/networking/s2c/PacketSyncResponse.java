package com.daqem.jobsplus.networking.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.JobManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

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
            String data = tag.getString("data");
            ListTag actions = tag.getList("actions", ListTag.TAG_STRING);
            JsonArray actionsJson = new JsonArray();
            ListTag powerups = tag.getList("powerups", ListTag.TAG_STRING);
            JsonArray powerupsJson = new JsonArray();

            actions.forEach(action -> {
                StringTag actionTag = (StringTag) action;
                actionsJson.add(GsonHelper.parse(actionTag.getAsString()));
            });

            powerups.forEach(powerup -> {
                StringTag powerupTag = (StringTag) powerup;
                powerupsJson.add(GsonHelper.parse(powerupTag.getAsString()));
            });

            JsonObject jsonObject = GsonHelper.parse(data);
            jsonObject.add("actions", actionsJson);
            jsonObject.add("powerups", powerupsJson);

            this.jobJson = jsonObject;
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
        CompoundTag newJobJson = new CompoundTag();
        ListTag actions = new ListTag();
        ListTag powerups = new ListTag();

        jobJson.getAsJsonObject().get("actions").getAsJsonArray().forEach(action -> {
            actions.add(StringTag.valueOf(action.toString()));
        });

        jobJson.getAsJsonObject().remove("actions");

        jobJson.getAsJsonObject().get("powerups").getAsJsonArray().forEach(powerup -> {
            powerups.add(StringTag.valueOf(powerup.toString()));
        });

        jobJson.getAsJsonObject().remove("powerups");

        newJobJson.putString("data", jobJson.toString());
        newJobJson.put("actions", actions);
        newJobJson.put("powerups", powerups);

        buf.writeNbt(newJobJson);
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
