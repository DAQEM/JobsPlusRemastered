package com.daqem.jobsplus.networking;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.networking.c2s.*;
import com.daqem.jobsplus.networking.s2c.*;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface JobsPlusNetworking {

    SimpleNetworkManager NETWORK = SimpleNetworkManager.create(JobsPlus.MOD_ID);

    MessageType S2C_OPEN_MENU = NETWORK.registerS2C("s2c_open_menu", PacketOpenMenuS2C::new);
    MessageType S2C_CANT_CRAFT = NETWORK.registerS2C("s2c_cant_craft", PacketCantCraftS2C::new);
    MessageType S2C_SYNC_JOB = NETWORK.registerS2C("s2c_sync_jobs", PacketSyncJobS2C::new);
    MessageType S2C_SYNC_POWERUP = NETWORK.registerS2C("s2c_sync_powerup", PacketSyncPowerupS2C::new);
    MessageType S2C_SYNC_ACTION = NETWORK.registerS2C("s2c_sync_action", PacketSyncActionS2C::new);
    MessageType S2C_SYNC_CRAFTING_RESTRICTIONS = NETWORK.registerS2C("s2c_sync_crafting_restrictions", PacketSyncRestrictionS2C::new);
    MessageType S2C_OPEN_UPDATE_SCREEN = NETWORK.registerS2C("s2c_open_update_screen", PacketOpenUpdateScreenS2C::new);

    MessageType C2S_OPEN_MENU = NETWORK.registerC2S("c2s_open_menu", PacketOpenMenuC2S::new);
    MessageType C2S_CONFIRMATION = NETWORK.registerC2S("c2s_confirmation", PacketConfirmationC2S::new);
    MessageType C2S_TOGGLE_POWER_UP = NETWORK.registerC2S("c2s_toggle_power_up", PacketTogglePowerUpC2S::new);
    MessageType C2S_SYNC_REQUEST = NETWORK.registerC2S("c2s_sync_request", PacketSyncRequestC2S::new);
    MessageType C2S_APPROVED_UPDATE = NETWORK.registerC2S("c2s_approved_update", PacketApprovedUpdateC2S::new);

    static void init() {
    }
}
