package com.daqem.jobsplus.client;

import com.daqem.jobsplus.event.client.EventKeyPressed;
import com.daqem.jobsplus.event.client.EventSyncRequest;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public class JobsPlusClient {

    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String JOBSPLUS_CATEGORY = "key.categories.jobsplus";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key.jobsplus.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, JOBSPLUS_CATEGORY);

    public static void init() {
        registerEvents();
    }

    private static void registerEvents() {
        EventKeyPressed.registerEvent();
        EventSyncRequest.registerEvent();
    }
}
