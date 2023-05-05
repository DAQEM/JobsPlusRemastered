package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.job.action.Actions;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.entity.animal.Animal;

public class EntityEvents {

    public static void registerEvents() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.DEATH)
                        .withSpecification(ActionSpecification.DAMAGE_SOURCE, source)
                        .build()
                        .sendToAction();
            } else if (source.getEntity() instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.KILL_ENTITY)
                        .withSpecification(ActionSpecification.ENTITY, entity)
                        .withSpecification(ActionSpecification.EXP_DROP, entity.getExperienceReward())
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });

        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (entity instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.GET_HURT)
                        .withSpecification(ActionSpecification.DAMAGE_SOURCE, source)
                        .withSpecification(ActionSpecification.DAMAGE_AMOUNT, amount)
                        .build()
                        .sendToAction();
            }

            if (source.getEntity() instanceof JobsServerPlayer jobsServerPlayer) {

                CraftingResult placeBlockResult = jobsServerPlayer.canCraft(CraftingType.HURTING_ENTITY, jobsServerPlayer.getServerPlayer().getMainHandItem());
                if (!placeBlockResult.canCraft()) {
                    placeBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }

                new ActionDataBuilder(jobsServerPlayer, Actions.HURT_ENTITY)
                        .withSpecification(ActionSpecification.ENTITY, entity)
                        .withSpecification(ActionSpecification.DAMAGE_AMOUNT, amount)
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });

        EntityEvent.ANIMAL_TAME.register((animal, player) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.TAME_ANIMAL)
                        .withSpecification(ActionSpecification.ENTITY, animal)
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });

        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer) {
                new ActionDataBuilder(jobsServerPlayer, Actions.INTERACT_ENTITY)
                        .withSpecification(ActionSpecification.ITEM_STACK, player.getItemInHand(hand))
                        .withSpecification(ActionSpecification.ITEM, player.getItemInHand(hand).getItem())
                        .withSpecification(ActionSpecification.ENTITY, entity)
                        .build()
                        .sendToAction();
            }
            return EventResult.pass();
        });
    }

    public static void onBreedAnimal(JobsServerPlayer player, Animal animal) {
        new ActionDataBuilder(player, Actions.BREED_ANIMAL)
                .withSpecification(ActionSpecification.ENTITY, animal)
                .build()
                .sendToAction();
    }
}
