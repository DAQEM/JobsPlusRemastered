package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEvents {

    public static void registerEvents() {
        BlockEvent.PLACE.register((level, pos, state, placer) -> {
            if (placer instanceof JobsServerPlayer jobsServerPlayer)
                new ActionDataBuilder(jobsServerPlayer, Actions.PLACE_BLOCK)
                        .withSpecification(ActionSpecification.BLOCK_STATE, state)
                        .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                        .withSpecification(ActionSpecification.WORLD, level)
                        .build()
                        .sendToAction();
            return EventResult.pass();
        });

        BlockEvent.BREAK.register((level, pos, state, player, xp) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer)
                new ActionDataBuilder(jobsServerPlayer, Actions.BREAK_BLOCK)
                        .withSpecification(ActionSpecification.BLOCK_STATE, state)
                        .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                        .withSpecification(ActionSpecification.EXP_DROP, xp)
                        .withSpecification(ActionSpecification.WORLD, level)
                        .build()
                        .sendToAction();
            return EventResult.pass();
        });
    }

    public static void onBlockInteract(JobsServerPlayer player, BlockState state, BlockPos pos, Level level) {
        new ActionDataBuilder(player, Actions.INTERACT_BLOCK)
                .withSpecification(ActionSpecification.BLOCK_STATE, state)
                .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                .withSpecification(ActionSpecification.WORLD, level)
                .build()
                .sendToAction();
    }
}
