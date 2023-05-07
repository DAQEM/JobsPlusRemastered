package com.daqem.jobsplus.resources.crafting.restriction.restrictions;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ItemCraftingRestriction extends CraftingRestriction {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final ItemInput itemInput;

    public ItemCraftingRestriction(int requiredLevel, boolean canCraft, boolean canSmelt, boolean canBrew, boolean canEnchant, boolean canRepair, boolean canUseItem, boolean canBreakBlock, boolean canItemBreakBlock, boolean canPlaceBlock, boolean canHurtEntity, ItemInput itemInput) {
        super(requiredLevel, canCraft, canSmelt, canBrew, canEnchant, canRepair, canUseItem, canBreakBlock, canItemBreakBlock, canPlaceBlock, canHurtEntity);
        this.itemInput = itemInput;
    }

    @Override
    public CraftingResult canCraft(CraftingType craftingType, ItemStack itemStack, int level, JobInstance jobInstance) {
        return new CraftingResult(!itemInput.test(itemStack) || (itemInput.test(itemStack) && canCraft(craftingType, level)), craftingType, itemStack, getRequiredLevel(), jobInstance);
    }

    @Override
    public ItemStack getItemStack() {
        try {
            return itemInput.createItemStack(1, false);
        } catch (CommandSyntaxException e) {
            LOGGER.error("Failed to create item stack from item input: {}", itemInput);
            return ItemStack.EMPTY;
        }
    }

    public List<Component> getTooltip(Job job) {
        ChatFormatting color = job.getLevel() >= getRequiredLevel() ? ChatFormatting.GREEN : ChatFormatting.RED;
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(getItemStack().getHoverName().copy().withStyle(style -> style.withColor(job.getLevel() >= getRequiredLevel() ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED)));
        tooltip.add(getRequiredLevel() > 0 ? JobsPlus.translatable("gui.restriction.required_level").withStyle(style -> style.withColor(color)).append(JobsPlus.literal(String.valueOf(getRequiredLevel())).withStyle(style -> style.withColor(ChatFormatting.WHITE))) : JobsPlus.translatable("gui.restriction.no_required_level").withStyle(style -> style.withColor(color)));
        tooltip.add(JobsPlus.literal(""));
        tooltip.add(JobsPlus.translatable("gui.restriction.restriction_types").withStyle(style -> style.withBold(true).withColor(color)));
        if (isCanCraft() && isCanSmelt() && isCanBrew() && isCanEnchant() && isCanRepair() && isCanUseItem() && isCanBreakBlock() && isCanItemBreakBlock() && isCanPlaceBlock() && isCanHurtEntity())
            tooltip.add(JobsPlus.translatable("gui.restriction.no_restrictions"));
        if (!isCanCraft())
            tooltip.add(JobsPlus.translatable("gui.restriction.crafting"));
        if (!isCanSmelt())
            tooltip.add(JobsPlus.translatable("gui.restriction.smelting"));
        if (!isCanBrew())
            tooltip.add(JobsPlus.translatable("gui.restriction.brewing"));
        if (!isCanEnchant())
            tooltip.add(JobsPlus.translatable("gui.restriction.enchanting"));
        if (!isCanRepair())
            tooltip.add(JobsPlus.translatable("gui.restriction.repairing"));
        if (!isCanUseItem())
            tooltip.add(JobsPlus.translatable("gui.restriction.use_right_click"));
        if (!isCanBreakBlock())
            tooltip.add(JobsPlus.translatable("gui.restriction.break_block"));
        if (!isCanItemBreakBlock())
            tooltip.add(JobsPlus.translatable("gui.restriction.break_block_with_item"));
        if (!isCanPlaceBlock())
            tooltip.add(JobsPlus.translatable("gui.restriction.place_block"));
        if (!isCanHurtEntity())
            tooltip.add(JobsPlus.translatable("gui.restriction.hurt_entity"));
        return tooltip;
    }

    @Override
    public String toString() {
        return "ItemCraftingRestriction{" +
                "item=" + itemInput +
                ", requiredLevel=" + getRequiredLevel() +
                ", canCraft=" + isCanCraft() +
                ", canSmelt=" + isCanSmelt() +
                ", canBrew=" + isCanBrew() +
                ", canEnchant=" + isCanEnchant() +
                ", canRepair=" + isCanRepair() +
                ", canUseItem=" + isCanUseItem() +
                ", canBreakBlock=" + isCanBreakBlock() +
                ", canPlaceBlock=" + isCanPlaceBlock() +
                ", canHurtEntity=" + isCanHurtEntity() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<ItemCraftingRestriction> {

        @Override
        public ItemCraftingRestriction deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            CompoundTag compoundTag = null;
            if (jsonObject.has("tag")) {
                try {
                    compoundTag = TagParser.parseTag(GsonHelper.getAsString(jsonObject, "tag"));
                } catch (CommandSyntaxException e) {
                    LOGGER.error("Error parsing tag for ItemCraftingRestriction item {}: {}", GsonHelper.getAsString(jsonObject, "item"), e.getMessage());
                }
            }
            ItemInput itemInput = new ItemInput(GsonHelper.getAsItem(jsonObject, "item").arch$holder(), compoundTag);

            return new ItemCraftingRestriction(
                    GsonHelper.getAsInt(jsonObject, "required_level"),
                    GsonHelper.getAsBoolean(jsonObject, "can_craft", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_smelt", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_brew", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_enchant", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_repair", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_use_item", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_break_block", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_place_block", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_item_break_block", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_hurt_entity", true),
                    itemInput
            );
        }
    }
}
