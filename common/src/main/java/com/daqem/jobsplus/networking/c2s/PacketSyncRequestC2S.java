package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.s2c.PacketSyncResponseS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.CraftingRestrictionManager;
import com.daqem.jobsplus.resources.JobManager;
import com.google.gson.JsonElement;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSyncRequestC2S extends BaseC2SMessage {

    public PacketSyncRequestC2S() {
    }

    public PacketSyncRequestC2S(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.C2S_SYNC_REQUEST;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            try {
                JobManager.getInstance().getMap().forEach((location, jobJson) -> {
                    JsonElement restrictionJson = CraftingRestrictionManager.getInstance().getFromLocation(location);
                    new PacketSyncResponseS2C(location, jobJson, restrictionJson).sendTo(serverPlayer.getServerPlayer());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
