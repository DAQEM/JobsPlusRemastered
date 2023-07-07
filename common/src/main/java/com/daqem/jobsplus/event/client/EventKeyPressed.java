package com.daqem.jobsplus.event.client;

import com.daqem.jobsplus.client.JobsPlusClient;
import com.daqem.jobsplus.client.screen.JobsScreen;
import com.daqem.jobsplus.networking.c2s.PacketOpenMenuC2S;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import net.minecraft.client.gui.screens.Screen;

public class EventKeyPressed {

    public static void registerEvent() {
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            Screen screen = client.screen;
            if (JobsPlusClient.OPEN_MENU.matches(keyCode, scanCode) && action == 1) {
                if (screen instanceof JobsScreen) screen.onClose();
                else if (screen == null) new PacketOpenMenuC2S().sendToServer();
            }
            return EventResult.pass();
        });
    }
}
