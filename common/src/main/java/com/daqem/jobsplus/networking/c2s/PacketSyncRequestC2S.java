package com.daqem.jobsplus.networking.c2s;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.networking.s2c.PacketSyncActionS2C;
import com.daqem.jobsplus.networking.s2c.PacketSyncJobS2C;
import com.daqem.jobsplus.networking.s2c.PacketSyncPowerupS2C;
import com.daqem.jobsplus.networking.s2c.PacketSyncRestrictionS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingRestrictionManager;
import com.daqem.jobsplus.resources.job.JobManager;
import com.daqem.jobsplus.resources.job.action.ActionManager;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
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
                    new PacketSyncJobS2C(location, jobJson).sendTo(serverPlayer.getServerPlayer());
                });

                PowerupManager.getInstance().getMap().forEach((location, powerupJson) -> {
                    new PacketSyncPowerupS2C(location, powerupJson).sendTo(serverPlayer.getServerPlayer());
                });

                ActionManager.getInstance().getMap().forEach((location, actionJson) -> {
                    new PacketSyncActionS2C(location, actionJson).sendTo(serverPlayer.getServerPlayer());
                });

                CraftingRestrictionManager.getInstance().getMap().forEach((location, restrictionJson) -> {
                    new PacketSyncRestrictionS2C(location, restrictionJson).sendTo(serverPlayer.getServerPlayer());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
