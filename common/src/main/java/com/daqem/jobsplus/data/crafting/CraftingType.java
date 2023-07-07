package com.daqem.jobsplus.data.crafting;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public enum CraftingType {

    CRAFTING("inventory.cant_craft"),
    SMELTING("inventory.cant_smelt"),
    BREWING("inventory.cant_brew"),
    ENCHANTING("inventory.cant_enchant"),
    REPAIRING("inventory.cant_repair"),
    USING_ITEM("inventory.cant_use_item"),
    BREAKING_BLOCK("inventory.cant_break_block"),
    ITEM_BREAKING_BLOCK("inventory.cant_break_block_with_item"),
    PLACING_BLOCK("inventory.cant_place_block"),
    HURTING_ENTITY("inventory.cant_hurt_entity");

    private final String translationKey;

    CraftingType(String translationKey) {
        this.translationKey = translationKey;
    }

    public MutableComponent getMessage(JobInstance jobInstance, int requiredLevel, ItemStack itemStack) {
        return JobsPlus.translatable(translationKey, jobInstance.getName(), requiredLevel, itemStack.getDisplayName().getString()).withStyle(ChatFormatting.RED);
    }
}
