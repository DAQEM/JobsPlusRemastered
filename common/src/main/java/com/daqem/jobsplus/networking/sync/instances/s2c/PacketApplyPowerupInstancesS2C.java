package com.daqem.jobsplus.networking.sync.instances.s2c;

import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.job.powerup.PowerupManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class PacketApplyPowerupInstancesS2C extends BaseS2CMessage {

    public PacketApplyPowerupInstancesS2C() {
    }

    public PacketApplyPowerupInstancesS2C(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public MessageType getType() {

        return JobsPlusNetworking.S2C_APPLY_POWERUP_INSTANCES;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        try {
            PowerupManager instance = PowerupManager.getInstance();
            instance.apply(instance.getMap(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
