package com.daqem.jobsplus.fabric;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.command.arguments.EnumArgument;
import com.daqem.jobsplus.command.arguments.JobArgument;
import com.daqem.jobsplus.command.arguments.PowerupArgument;
import com.daqem.jobsplus.fabric.registry.JobsPlusRegistryFabric;
import com.daqem.jobsplus.fabric.resources.ActionManagerFabric;
import com.daqem.jobsplus.fabric.resources.CraftingRestrictionManagerFabric;
import com.daqem.jobsplus.fabric.resources.JobManagerFabric;
import com.daqem.jobsplus.fabric.resources.PowerupManagerFabric;
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
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CraftingRestrictionManagerFabric());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PowerupManagerFabric());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ActionManagerFabric());

        registerCommandArgumentTypes();
    }

    private void registerCommandArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("job"), JobArgument.class, SingletonArgumentInfo.contextFree(JobArgument::job));
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("powerup"), PowerupArgument.class, SingletonArgumentInfo.contextFree(PowerupArgument::powerup));
        ArgumentTypeRegistry.registerArgumentType(JobsPlus.getId("enum"), EnumArgument.class, new EnumArgument.Info());
    }

    static {
        JobsPlusRegistryFabric.init();
    }
}
