package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.command.arguments.EnumArgument;
import com.daqem.jobsplus.command.arguments.JobArgument;
import com.daqem.jobsplus.command.arguments.PowerupArgument;
import com.daqem.jobsplus.config.ICommonConfig;
import com.daqem.jobsplus.fabric.config.CommonConfigFabric;
import com.daqem.jobsplus.fabric.registry.JobsPlusRegistryFabric;
import com.daqem.jobsplus.fabric.resources.JobManagerFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.packs.PackType;

public class JobsPlusFabric implements ModInitializer {

    private static final JobManagerFabric JOB_MANAGER = new JobManagerFabric();
    private static final ICommonConfig COMMON_CONFIG = new CommonConfigFabric();

    @Override
    public void onInitialize() {
        CommonConfigFabric.init();
        JobsPlus.init();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(JOB_MANAGER);

        registerCommandArgumentTypes();
    }

    private void registerCommandArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("job"), JobArgument.class, SingletonArgumentInfo.contextFree(JobArgument::job));
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("powerup"), PowerupArgument.class, SingletonArgumentInfo.contextFree(PowerupArgument::powerup));
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("enum"), EnumArgument.class, new EnumArgument.Info());
    }

    public static JobManagerFabric getJobManager() {
        return JOB_MANAGER;
    }

    public static ICommonConfig getCommonConfig() {
        return COMMON_CONFIG;
    }

    static {
        JobsPlusRegistryFabric.init();
    }
}
