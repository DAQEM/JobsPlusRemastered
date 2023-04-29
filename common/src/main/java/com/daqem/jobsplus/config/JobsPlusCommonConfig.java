package com.daqem.jobsplus.config;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class JobsPlusCommonConfig {

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
}
