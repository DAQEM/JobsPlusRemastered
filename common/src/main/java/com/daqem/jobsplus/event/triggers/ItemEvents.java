package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.action.ActionDataBuilder;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;

public class ItemEvents {

    public static void registerEvents() {
        PlayerEvent.DROP_ITEM.register((player, itemStack) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.DROP_ITEM)
                        .withSpecification(ActionSpecification.ITEM, itemStack.getItem().getItem())
                        .withSpecification(ActionSpecification.ITEM_STACK, itemStack.getItem())
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });
    }

    /**
     * Called when a player uses an item.
     *
     * @param player   - The player that used the item.
     * @param usedItem - The item that was used.
     */
    public static void onUseItem(JobsServerPlayer player, Item usedItem) {
        new ActionDataBuilder(player, Actions.USE_ITEM)
                .withSpecification(ActionSpecification.ITEM, usedItem)
                .build()
                .sendToAction();
    }

    public static void onThrowItem(JobsServerPlayer player, ThrowableItemProjectile thrownItemEntity) {
        new ActionDataBuilder(player, Actions.THROW_ITEM)
                .withSpecification(ActionSpecification.ITEM_STACK, thrownItemEntity.getItem())
                .withSpecification(ActionSpecification.ENTITY, thrownItemEntity)
                .build()
                .sendToAction();
    }
}
