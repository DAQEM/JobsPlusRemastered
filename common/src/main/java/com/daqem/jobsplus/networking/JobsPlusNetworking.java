package com.daqem.jobsplus.networking;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.networking.c2s.*;
import com.daqem.jobsplus.networking.s2c.*;
import com.daqem.jobsplus.networking.sync.ClientboundUpdateJobInstancesPacket;
import com.daqem.jobsplus.networking.sync.ClientboundUpdatePowerupInstancesPacket;
import com.daqem.jobsplus.networking.sync.instances.c2s.PacketSyncJobInstancesRequestC2S;
import com.daqem.jobsplus.networking.sync.instances.s2c.PacketSyncRestrictionS2C;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobPacket;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobsPacket;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface JobsPlusNetworking {

    SimpleNetworkManager NETWORK = SimpleNetworkManager.create(JobsPlus.MOD_ID);

    MessageType S2C_OPEN_MENU = NETWORK.registerS2C("s2c_open_menu", PacketOpenMenuS2C::new);
    MessageType S2C_OPEN_POWERUPS_MENU = NETWORK.registerS2C("s2c_open_powerups_menu", PacketOpenPowerupsMenuS2C::new);
    MessageType S2C_CANT_CRAFT = NETWORK.registerS2C("s2c_cant_craft", PacketCantCraftS2C::new);
    MessageType S2C_SYNC_CRAFTING_RESTRICTIONS = NETWORK.registerS2C("s2c_sync_crafting_restrictions", PacketSyncRestrictionS2C::new);
    MessageType S2C_OPEN_UPDATE_SCREEN = NETWORK.registerS2C("s2c_open_update_screen", PacketOpenUpdateScreenS2C::new);
    MessageType CLIENTBOUND_UPDATE_JOB_INSTANCES = NETWORK.registerS2C("clientbound_update_job_instances", ClientboundUpdateJobInstancesPacket::new);
    MessageType CLIENTBOUND_UPDATE_POWERUP_INSTANCES = NETWORK.registerS2C("clientbound_update_powerup_instances", ClientboundUpdatePowerupInstancesPacket::new);
    MessageType CLIENTBOUND_UPDATE_JOBS = NETWORK.registerS2C("clientbound_update_jobs", ClientboundUpdateJobsPacket::new);
    MessageType CLIENTBOUND_UPDATE_JOB = NETWORK.registerS2C("clientbound_update_job", ClientboundUpdateJobPacket::new);

    MessageType C2S_OPEN_MENU = NETWORK.registerC2S("c2s_open_menu", PacketOpenMenuC2S::new);
    MessageType C2S_OPEN_POWERUPS_MENU = NETWORK.registerC2S("c2s_open_powerups_menu", PacketOpenPowerupsMenuC2S::new);
    MessageType C2S_CONFIRMATION = NETWORK.registerC2S("c2s_confirmation", PacketConfirmationC2S::new);
    MessageType C2S_TOGGLE_POWER_UP = NETWORK.registerC2S("c2s_toggle_power_up", PacketTogglePowerUpC2S::new);
    MessageType C2S_SYNC_JOB_INSTANCES_REQUEST = NETWORK.registerC2S("c2s_sync_job_instances_request", PacketSyncJobInstancesRequestC2S::new);
    MessageType C2S_APPROVED_UPDATE = NETWORK.registerC2S("c2s_approved_update", PacketApprovedUpdateC2S::new);

    static void init() {
    }
}
