package com.daqem.jobsplus;

import com.daqem.jobsplus.config.server.ServerConfig;
import com.daqem.jobsplus.event.command.EventRegisterCommands;
import com.daqem.jobsplus.event.player.EventPlayerJoin;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.multiloaderconfiglib.MultiLoaderConfigLib;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class JobsPlus {
    public static final String MOD_ID = "jobsplus";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public static void init() {
        registerEvents();
        JobsPlusNetworking.init();
        MultiLoaderConfigLib.addServerConfigs(SERVER_CONFIG);
    }

    private static void registerEvents() {
        EventPlayerJoin.registerEvent();
        EventRegisterCommands.registerEvent();
    }

    public static ResourceLocation getId(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static MutableComponent translatable(String str) {
        return Component.translatable("jobsplus." + str);
    }

    public static MutableComponent translatable(String str, Object... objects) {
        return Component.translatable("jobsplus." + str, objects);
    }

    public static MutableComponent literal(String str) {
        return Component.literal(str);
    }

    public static JobManager getJobManager() {
        return JobsPlusExpectPlatform.getJobManager();
    }
}
