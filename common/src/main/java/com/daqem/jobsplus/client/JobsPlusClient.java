package com.daqem.jobsplus.client;

import com.daqem.jobsplus.event.client.EventKeyPressed;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class JobsPlusClient {

    private static final String JOBSPLUS_CATEGORY = "key.categories.jobsplus";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key.jobsplus.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, JOBSPLUS_CATEGORY);

    public static void init() {
        registerEvents();
    }

    private static void registerEvents() {
        EventKeyPressed.registerEvent();
    }
}
