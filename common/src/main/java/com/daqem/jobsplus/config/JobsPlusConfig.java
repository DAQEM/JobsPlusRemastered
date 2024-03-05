package com.daqem.jobsplus.config;

import com.daqem.jobsplus.JobsPlus;
import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class JobsPlusConfig {

    public static final Supplier<Boolean> enableDefaultJobs;
    public static final Supplier<Integer> amountOfFreeJobs;
    public static final Supplier<Integer> jobStopRefundPercentage;
    public static final Supplier<Boolean> allowJobStopRefund;

    public static final Supplier<Integer> coinsPerLevelUp;

    public static final Supplier<Boolean> isDebug;

    static {
        IConfigBuilder config = ConfigBuilders.newTomlConfig(JobsPlus.MOD_ID, null, false);

        config.push("jobs");
        enableDefaultJobs = config.comment("if true, the default jobs are enabled. WARNING: setting this to false will erase all the stats for these jobs").define("enable_default_jobs", true);
        amountOfFreeJobs = config.comment("the amount of free jobs a player can have").define("amount_of_free_jobs", 2, 0, Integer.MAX_VALUE);
        jobStopRefundPercentage = config.comment("the percentage of the job cost that is refunded when a player stops a job").define("job_stop_refund_percentage", 50, 0, 100);
        allowJobStopRefund = config.comment("if true, players can stop a job and get a refund").define("allow_job_stop_refund", false);
        config.push("coins");
        coinsPerLevelUp = config.comment("the amount of coins a player gets when they level up a job").define("coins_per_level_up", 1, 0, Integer.MAX_VALUE);
        config.pop();
        config.pop();

        config.push("debug");
        isDebug = config.comment("if true, debug mode is enabled").define("is_debug", false);
        config.pop();

        config.build();
    }

    public static void init() {
    }
}
