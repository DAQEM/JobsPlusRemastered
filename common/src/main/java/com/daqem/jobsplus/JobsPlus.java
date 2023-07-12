package com.daqem.jobsplus;

import com.daqem.arc.registry.ArcRegistry;
import com.daqem.jobsplus.event.EventPlayerJoin;
import com.daqem.jobsplus.event.command.EventRegisterCommands;
import com.daqem.jobsplus.interation.arc.action.holder.type.JobsPlusActionHolderType;
import com.daqem.jobsplus.interation.arc.action.serializer.JobsPlusActionSerializer;
import com.daqem.jobsplus.interation.arc.action.type.JobsPlusActionType;
import com.daqem.jobsplus.interation.arc.condition.serializer.JobsPlusConditionSerializer;
import com.daqem.jobsplus.interation.arc.condition.type.JobsPlusConditionType;
import com.daqem.jobsplus.interation.arc.reward.serializer.JobsPlusRewardSerializer;
import com.daqem.jobsplus.interation.arc.reward.type.JobsPlusRewardType;
import com.daqem.jobsplus.networking.JobsPlusNetworking;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class JobsPlus {
    public static final String MOD_ID = "jobsplus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        registerEvents();
        initRegistry();
        JobsPlusNetworking.init();
    }

    private static void initRegistry() {
        ArcRegistry.init();

        JobsPlusActionType.init();
        JobsPlusRewardType.init();
        JobsPlusConditionType.init();
        JobsPlusActionHolderType.init();
        JobsPlusActionSerializer.init();
        JobsPlusRewardSerializer.init();
        JobsPlusConditionSerializer.init();
    }

    private static void registerEvents() {
        EventRegisterCommands.registerEvent();

        EventPlayerJoin.registerEvent();
    }

    public static ResourceLocation getId(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static MutableComponent translatable(String str) {
        return Component.translatable(MOD_ID + "." + str);
    }

    public static MutableComponent translatable(String str, Object... objects) {
        return Component.translatable(MOD_ID + "." + str, objects);
    }

    public static MutableComponent literal(String str) {
        return Component.literal(str);
    }
}
