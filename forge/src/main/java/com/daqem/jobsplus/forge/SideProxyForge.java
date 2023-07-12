package com.daqem.jobsplus.forge;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.JobsPlusClient;
import com.daqem.jobsplus.command.arguments.EnumArgument;
import com.daqem.jobsplus.command.arguments.JobArgument;
import com.daqem.jobsplus.command.arguments.PowerupArgument;
import com.daqem.jobsplus.forge.data.JobManagerForge;
import com.daqem.jobsplus.forge.data.PowerupManagerForge;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

public class SideProxyForge {

    SideProxyForge() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(this::onAddReloadListeners);

        registerCommandArgumentTypes();
    }

    private void registerCommandArgumentTypes() {
        DeferredRegister<ArgumentTypeInfo<?, ?>> argTypeRegistry = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, JobsPlus.MOD_ID);
        argTypeRegistry.register("job", () -> ArgumentTypeInfos.registerByClass(JobArgument.class, SingletonArgumentInfo.contextFree(JobArgument::job)));
        argTypeRegistry.register("powerup", () -> ArgumentTypeInfos.registerByClass(PowerupArgument.class, SingletonArgumentInfo.contextFree(PowerupArgument::powerup)));
        argTypeRegistry.register("enum", () -> ArgumentTypeInfos.registerByClass(EnumArgument.class, new EnumArgument.Info()));
        argTypeRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new JobManagerForge());
        event.addListener(new PowerupManagerForge());
    }

    public static class Server extends SideProxyForge {
        Server() {

        }

    }

    public static class Client extends SideProxyForge {

        Client() {
            JobsPlusClient.init();

            registerEvents();
        }

        private void registerEvents() {
            IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            eventBus.addListener(this::registerKeyBindings);
        }

        private void registerKeyBindings(RegisterKeyMappingsEvent event) {
            event.register(JobsPlusClient.OPEN_MENU);
        }
    }
}
