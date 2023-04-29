package com.daqem.jobsplus.forge.config;

import com.daqem.jobsplus.config.ICommonConfig;
import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class CommonConfigForge implements ICommonConfig {

    public static void init() {
    }

    public static final Supplier<Boolean> isDebug;

    static {
        IConfigBuilder config = ConfigBuilders.newTomlConfig("jobsplus", null, false);
        config.push("debug");
        isDebug = config.comment("if true, debug mode is enabled").define("isDebug", false);
        config.pop();

        config.build();
    }

    @Override
    public boolean isDebug() {
        return isDebug.get();
    }
}
