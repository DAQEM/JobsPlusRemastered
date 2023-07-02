package com.daqem.jobsplus.networking;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.networking.c2s.*;
import com.daqem.jobsplus.networking.s2c.*;
import com.daqem.jobsplus.networking.sync.instances.s2c.PacketApplyPowerupInstancesS2C;
import com.daqem.jobsplus.networking.sync.instances.c2s.PacketSyncJobInstancesRequestC2S;
import com.daqem.jobsplus.networking.sync.instances.s2c.PacketSyncActionS2C;
import com.daqem.jobsplus.networking.sync.instances.s2c.PacketSyncJobInstanceS2C;
import com.daqem.jobsplus.networking.sync.instances.s2c.PacketSyncPowerupInstanceS2C;
import com.daqem.jobsplus.networking.sync.instances.s2c.PacketSyncRestrictionS2C;
import com.daqem.jobsplus.networking.sync.jobs.c2s.PacketRequestJobsSyncC2S;
import com.daqem.jobsplus.networking.sync.jobs.s2c.PacketSyncJobS2C;
import com.daqem.jobsplus.networking.sync.jobs.s2c.PacketUpdateClientsideJobS2C;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface JobsPlusNetworking {

    SimpleNetworkManager NETWORK = SimpleNetworkManager.create(JobsPlus.MOD_ID);

    MessageType S2C_OPEN_MENU = NETWORK.registerS2C("s2c_open_menu", PacketOpenMenuS2C::new);
    MessageType S2C_OPEN_POWERUPS_MENU = NETWORK.registerS2C("s2c_open_powerups_menu", PacketOpenPowerupsMenuS2C::new);
    MessageType S2C_CANT_CRAFT = NETWORK.registerS2C("s2c_cant_craft", PacketCantCraftS2C::new);
    MessageType S2C_SYNC_JOB_INSTANCE = NETWORK.registerS2C("s2c_sync_job_instance", PacketSyncJobInstanceS2C::new);
    MessageType S2C_SYNC_POWERUP_INSTANCE = NETWORK.registerS2C("s2c_sync_powerup_instance", PacketSyncPowerupInstanceS2C::new);
    MessageType S2C_SYNC_ACTION = NETWORK.registerS2C("s2c_sync_action", PacketSyncActionS2C::new);
    MessageType S2C_SYNC_CRAFTING_RESTRICTIONS = NETWORK.registerS2C("s2c_sync_crafting_restrictions", PacketSyncRestrictionS2C::new);
    MessageType S2C_OPEN_UPDATE_SCREEN = NETWORK.registerS2C("s2c_open_update_screen", PacketOpenUpdateScreenS2C::new);
    MessageType S2C_SYNC_JOBS = NETWORK.registerS2C("s2c_sync_jobs", PacketSyncJobS2C::new);
    MessageType S2C_APPLY_POWERUP_INSTANCES = NETWORK.registerS2C("c2s_apply_powerup_instances", PacketApplyPowerupInstancesS2C::new);
    MessageType S2C_UPDATE_CLIENTSIDE_JOB = NETWORK.registerS2C("s2c_update_clientside_job", PacketUpdateClientsideJobS2C::new);

    MessageType C2S_OPEN_MENU = NETWORK.registerC2S("c2s_open_menu", PacketOpenMenuC2S::new);
    MessageType C2S_OPEN_POWERUPS_MENU = NETWORK.registerC2S("c2s_open_powerups_menu", PacketOpenPowerupsMenuC2S::new);
    MessageType C2S_CONFIRMATION = NETWORK.registerC2S("c2s_confirmation", PacketConfirmationC2S::new);
    MessageType C2S_TOGGLE_POWER_UP = NETWORK.registerC2S("c2s_toggle_power_up", PacketTogglePowerUpC2S::new);
    MessageType C2S_SYNC_JOB_INSTANCES_REQUEST = NETWORK.registerC2S("c2s_sync_job_instances_request", PacketSyncJobInstancesRequestC2S::new);
    MessageType C2S_APPROVED_UPDATE = NETWORK.registerC2S("c2s_approved_update", PacketApprovedUpdateC2S::new);
    MessageType C2S_SYNC_JOBS_REQUEST = NETWORK.registerC2S("c2s_sync_jobs_request", PacketRequestJobsSyncC2S::new);

    static void init() {
    }
}
