package com.daqem.jobsplus.networking;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.networking.c2s.PacketConfirmationC2S;
import com.daqem.jobsplus.networking.c2s.PacketOpenMenuC2S;
import com.daqem.jobsplus.networking.c2s.PacketSyncRequest;
import com.daqem.jobsplus.networking.c2s.PacketTogglePowerUp;
import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.networking.s2c.PacketOpenMenuS2C;
import com.daqem.jobsplus.networking.s2c.PacketSyncResponse;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface JobsPlusNetworking {

    SimpleNetworkManager NETWORK = SimpleNetworkManager.create(JobsPlus.MOD_ID);

    MessageType S2C_OPEN_MENU = NETWORK.registerS2C("s2c_open_menu", PacketOpenMenuS2C::new);
    MessageType S2C_CANT_CRAFT = NETWORK.registerS2C("s2c_cant_craft", PacketCantCraftS2C::new);
    MessageType S2C_SYNC_RESPONSE = NETWORK.registerS2C("s2c_sync_data_packs", PacketSyncResponse::new);

    MessageType C2S_OPEN_MENU = NETWORK.registerC2S("c2s_open_menu", PacketOpenMenuC2S::new);
    MessageType C2S_CONFIRMATION = NETWORK.registerC2S("c2s_confirmation", PacketConfirmationC2S::new);
    MessageType C2S_TOGGLE_POWER_UP = NETWORK.registerC2S("c2s_toggle_power_up", PacketTogglePowerUp::new);
    MessageType C2S_SYNC_REQUEST = NETWORK.registerC2S("c2s_sync_request", PacketSyncRequest::new);

    static void init() {
    }
}
