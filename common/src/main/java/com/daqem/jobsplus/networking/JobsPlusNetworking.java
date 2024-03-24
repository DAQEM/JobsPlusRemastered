package com.daqem.jobsplus.networking;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.networking.c2s.*;
import com.daqem.jobsplus.networking.s2c.PacketOpenMenuS2C;
import com.daqem.jobsplus.networking.s2c.PacketOpenPowerupsMenuS2C;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundRemoveJobPacket;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobPacket;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobsPacket;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface JobsPlusNetworking {

    SimpleNetworkManager NETWORK = SimpleNetworkManager.create(JobsPlus.MOD_ID);

    MessageType S2C_OPEN_MENU = NETWORK.registerS2C("s2c_open_menu", PacketOpenMenuS2C::new);
    MessageType S2C_OPEN_POWERUPS_MENU = NETWORK.registerS2C("s2c_open_powerups_menu", PacketOpenPowerupsMenuS2C::new);
    MessageType CLIENTBOUND_UPDATE_JOBS = NETWORK.registerS2C("clientbound_update_jobs", ClientboundUpdateJobsPacket::new);
    MessageType CLIENTBOUND_UPDATE_JOB = NETWORK.registerS2C("clientbound_update_job", ClientboundUpdateJobPacket::new);
    MessageType CLIENTBOUND_REMOVE_JOB = NETWORK.registerS2C("clientbound_remove_job", ClientboundRemoveJobPacket::new);

    MessageType C2S_OPEN_MENU = NETWORK.registerC2S("c2s_open_menu", PacketOpenMenuC2S::new);
    MessageType C2S_OPEN_POWERUPS_MENU = NETWORK.registerC2S("c2s_open_powerups_menu", PacketOpenPowerupsMenuC2S::new);
    MessageType C2S_CONFIRMATION = NETWORK.registerC2S("c2s_confirmation", PacketConfirmationC2S::new);
    MessageType C2S_TOGGLE_POWER_UP = NETWORK.registerC2S("c2s_toggle_power_up", PacketTogglePowerUpC2S::new);
    MessageType C2S_APPROVED_UPDATE = NETWORK.registerC2S("c2s_approved_update", PacketApprovedUpdateC2S::new);

    static void init() {
    }
}
