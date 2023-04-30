package com.daqem.jobsplus.config;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class JobsPlusCommonConfig {

    public static void init() {
    }

    public static final Supplier<Boolean> isDebug;

    public static final Supplier<Integer> amountOfFreeJobs;
    public static final Supplier<Integer> jobStopRefundPercentage;

    static {
        IConfigBuilder config = ConfigBuilders.newTomlConfig("jobsplus", null, false);
        config.push("debug");
        isDebug = config.comment("if true, debug mode is enabled").define("is_debug", false);
        config.pop();

        config.push("jobs");
        amountOfFreeJobs = config.comment("the amount of free jobs a player can have").define("amount_of_free_jobs", 2, 0, Integer.MAX_VALUE);
        jobStopRefundPercentage = config.comment("the percentage of the job cost that is refunded when a player stops a job").define("job_stop_refund_percentage", 50, 0, 100);
        config.pop();

        config.build();
    }
}
