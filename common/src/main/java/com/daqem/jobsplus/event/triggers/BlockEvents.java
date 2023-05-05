package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.job.action.Actions;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEvents {

    public static void registerEvents() {
        BlockEvent.PLACE.register((level, pos, state, placer) -> {
            if (placer instanceof JobsServerPlayer jobsServerPlayer) {

                ItemStack itemStack = jobsServerPlayer.getServerPlayer().getMainHandItem().getItem() instanceof BlockItem ? jobsServerPlayer.getServerPlayer().getMainHandItem() : jobsServerPlayer.getServerPlayer().getOffhandItem();
                CraftingResult placeBlockResult = jobsServerPlayer.canCraft(CraftingType.PLACING_BLOCK, itemStack);
                if (!placeBlockResult.canCraft()) {
                    placeBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }

                new ActionDataBuilder(jobsServerPlayer, Actions.PLACE_BLOCK)
                        .withSpecification(ActionSpecification.BLOCK_STATE, state)
                        .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                        .withSpecification(ActionSpecification.WORLD, level)
                        .build()
                        .sendToAction();

                if (state.getBlock() instanceof CropBlock) {
                    onPlantCrop(jobsServerPlayer, state, pos, level);
                }
            }
            return EventResult.pass();
        });
        BlockEvent.BREAK.register((level, pos, state, player, xp) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer) {

                CraftingResult itemBreakingBlockResult = jobsServerPlayer.canCraft(CraftingType.ITEM_BREAKING_BLOCK, jobsServerPlayer.getServerPlayer().getMainHandItem());
                if (!itemBreakingBlockResult.canCraft()) {
                    itemBreakingBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }

                CraftingResult breakingBlockResult = jobsServerPlayer.canCraft(CraftingType.BREAKING_BLOCK, level.getBlockState(pos).getBlock().asItem().getDefaultInstance());
                if (!breakingBlockResult.canCraft()) {
                    breakingBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }

                new ActionDataBuilder(jobsServerPlayer, Actions.BREAK_BLOCK)
                        .withSpecification(ActionSpecification.BLOCK_STATE, state)
                        .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                        .withSpecification(ActionSpecification.EXP_DROP, xp == null ? 0 : xp.get())
                        .withSpecification(ActionSpecification.WORLD, level)
                        .build()
                        .sendToAction();

                if (state.getBlock() instanceof CropBlock) {
                    onHarvestCrop(jobsServerPlayer, state, pos, level);
                }
            }
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

    public static void onPlantCrop(JobsServerPlayer player, BlockState state, BlockPos pos, Level level) {
        new ActionDataBuilder(player, Actions.PLANT_CROP)
                .withSpecification(ActionSpecification.BLOCK_STATE, state)
                .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                .withSpecification(ActionSpecification.WORLD, level)
                .build()
                .sendToAction();
    }

    public static void onHarvestCrop(JobsServerPlayer player, BlockState state, BlockPos pos, Level level) {
        new ActionDataBuilder(player, Actions.HARVEST_CROP)
                .withSpecification(ActionSpecification.BLOCK_STATE, state)
                .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                .withSpecification(ActionSpecification.WORLD, level)
                .build()
                .sendToAction();
    }
}
