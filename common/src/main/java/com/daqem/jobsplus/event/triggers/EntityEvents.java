package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;

public class EntityEvents {

    public static void registerEvents() {
        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (source.getEntity() instanceof JobsServerPlayer jobsServerPlayer) {
                CraftingResult placeBlockResult = jobsServerPlayer.jobsplus$canCraft(CraftingType.HURTING_ENTITY, jobsServerPlayer.jobsplus$getServerPlayer().getMainHandItem());
                if (!placeBlockResult.canCraft()) {
                    placeBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
    }
}
