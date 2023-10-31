package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.command.arguments.EnumArgument;
import com.daqem.jobsplus.command.arguments.JobArgument;
import com.daqem.jobsplus.command.arguments.PowerupArgument;
import com.daqem.jobsplus.fabric.data.JobManagerFabric;
import com.daqem.jobsplus.fabric.data.PowerupManagerFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.packs.PackType;

public class JobsPlusFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        JobsPlus.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new JobManagerFabric());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PowerupManagerFabric());

        registerCommandArgumentTypes();
    }

    private void registerCommandArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("job"), JobArgument.class, SingletonArgumentInfo.contextFree(JobArgument::job));
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("powerup"), PowerupArgument.class, SingletonArgumentInfo.contextFree(PowerupArgument::powerup));
        //noinspection rawtypes,unchecked
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("enum"), EnumArgument.class, new EnumArgument.Info());
    }
}
