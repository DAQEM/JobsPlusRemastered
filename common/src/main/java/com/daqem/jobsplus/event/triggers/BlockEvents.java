package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import com.daqem.jobsplus.player.JobsServerPlayer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class BlockEvents {

    public static void registerEvents() {
        BlockEvent.PLACE.register((level, pos, state, placer) -> {
            if (placer instanceof JobsServerPlayer jobsServerPlayer) {

                ItemStack itemStack = jobsServerPlayer.jobsplus$getServerPlayer().getMainHandItem().getItem() instanceof BlockItem ? jobsServerPlayer.jobsplus$getServerPlayer().getMainHandItem() : jobsServerPlayer.jobsplus$getServerPlayer().getOffhandItem();
                CraftingResult placeBlockResult = jobsServerPlayer.jobsplus$canCraft(CraftingType.PLACING_BLOCK, itemStack);
                if (!placeBlockResult.canCraft()) {
                    placeBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
        BlockEvent.BREAK.register((level, pos, state, player, xp) -> {
            if (player instanceof JobsServerPlayer jobsServerPlayer) {
                CraftingResult itemBreakingBlockResult = jobsServerPlayer.jobsplus$canCraft(CraftingType.ITEM_BREAKING_BLOCK, jobsServerPlayer.jobsplus$getServerPlayer().getMainHandItem());
                if (!itemBreakingBlockResult.canCraft()) {
                    itemBreakingBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }

                CraftingResult breakingBlockResult = jobsServerPlayer.jobsplus$canCraft(CraftingType.BREAKING_BLOCK, level.getBlockState(pos).getBlock().asItem().getDefaultInstance());
                if (!breakingBlockResult.canCraft()) {
                    breakingBlockResult.sendHotbarMessage(jobsServerPlayer);
                    return EventResult.interruptFalse();
                }
            }
            return EventResult.pass();
        });
    }
}
