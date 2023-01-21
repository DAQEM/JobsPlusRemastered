package com.daqem.jobsplus.fabric.client;

import com.daqem.jobsplus.client.JobsPlusClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class JobsPlusFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        JobsPlusClient.init();
        registerKeyBindings();
    }

    private static void registerKeyBindings() {
        KeyBindingHelper.registerKeyBinding(JobsPlusClient.OPEN_MENU);
    }
}
