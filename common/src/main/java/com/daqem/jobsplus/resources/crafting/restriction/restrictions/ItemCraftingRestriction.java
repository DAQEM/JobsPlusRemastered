package com.daqem.jobsplus.resources.crafting.restriction.restrictions;

import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

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
