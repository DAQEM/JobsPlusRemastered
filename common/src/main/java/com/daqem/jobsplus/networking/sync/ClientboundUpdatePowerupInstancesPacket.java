package com.daqem.jobsplus.networking.sync;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupManager;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class ClientboundUpdatePowerupInstancesPacket extends BaseS2CMessage {

    private final List<PowerupInstance> powerupInstances;

    public ClientboundUpdatePowerupInstancesPacket(List<PowerupInstance> powerupInstances) {
        this.powerupInstances = powerupInstances;
    }

    public ClientboundUpdatePowerupInstancesPacket(FriendlyByteBuf friendlyByteBuf) {
        this.powerupInstances = friendlyByteBuf.readList(friendlyByteBuf1 ->
                new PowerupInstance.Serializer().fromNetwork(friendlyByteBuf1));
    }

    @Override
    public MessageType getType() {
        return JobsPlusNetworking.CLIENTBOUND_UPDATE_POWERUP_INSTANCES;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(powerupInstances, (friendlyByteBuf1, powerupInstance) ->
                new PowerupInstance.Serializer().toNetwork(friendlyByteBuf1, powerupInstance));
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        PowerupManager.getInstance().replacePowerups(powerupInstances);
    }
}

