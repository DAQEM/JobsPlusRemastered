package com.daqem.jobsplus.networking.sync.instances.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.sync.instances.s2c.*;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.data.crafting.CraftingRestrictionManager;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSyncJobInstancesRequestC2S extends BaseC2SMessage {

    public PacketSyncJobInstancesRequestC2S() {
    }

    public PacketSyncJobInstancesRequestC2S(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public MessageType getType() {

        return JobsPlusNetworking.C2S_SYNC_JOB_INSTANCES_REQUEST;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            try {
                CraftingRestrictionManager.getInstance().getMap().forEach((location, restrictionJson) -> {
                    new PacketSyncRestrictionS2C(location, restrictionJson).sendTo(serverPlayer.jobsplus$getServerPlayer());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
