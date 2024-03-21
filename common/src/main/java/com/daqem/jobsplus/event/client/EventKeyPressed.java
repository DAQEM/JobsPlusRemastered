package com.daqem.jobsplus.event.client;

import com.daqem.arc.api.action.holder.ActionHolderManager;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.client.JobsPlusClient;
import com.daqem.jobsplus.client.screen.JobsScreen;
import com.daqem.jobsplus.config.JobsPlusConfig;
import com.daqem.jobsplus.networking.c2s.PacketOpenMenuC2S;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class EventKeyPressed {

    public static void registerEvent() {
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            Screen screen = client.screen;
            if (JobsPlusClient.OPEN_MENU.matches(keyCode, scanCode) && action == 1) {
                if (screen instanceof JobsScreen) screen.onClose();
                else if (screen == null) new PacketOpenMenuC2S().sendToServer();
            }
            if (JobsPlusConfig.isDebug.get()) {
                if (keyCode == GLFW.GLFW_KEY_P && action == 1) {
                    if (client.player != null) {
                        if (client.player instanceof ArcPlayer arcPlayer) {
                            arcPlayer.arc$getActionHolders().forEach(actionHolder -> {
                                client.player.sendSystemMessage(Component.literal(actionHolder.getLocation().toString()));
                                client.player.sendSystemMessage(Component.literal("actions: " + actionHolder.getActions().size()));
                                client.player.sendSystemMessage(Component.literal(" "));
                            });
                        }
                    }
                }
            }
            return EventResult.pass();
        });
    }
}
