package com.daqem.jobsplus.resources.crafting.restriction.restrictions;

import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemCraftingRestriction extends CraftingRestriction {

    private final Item item;

    public ItemCraftingRestriction(int requiredLevel, boolean canCraft, boolean canSmelt, boolean canBrew, boolean canEnchant, boolean canRepair, boolean canUseItem, boolean canBreakBlock, boolean canItemBreakBlock, boolean canPlaceBlock, boolean canHurtEntity, Item item) {
        super(requiredLevel, canCraft, canSmelt, canBrew, canEnchant, canRepair, canUseItem, canBreakBlock, canItemBreakBlock, canPlaceBlock, canHurtEntity);
        this.item = item;
    }

    @Override
    public CraftingResult canCraft(CraftingType craftingType, ItemStack itemStack, int level, JobInstance jobInstance) {
        return new CraftingResult(!itemStack.is(item) || (itemStack.is(item) && canCraft(craftingType, level)), craftingType, itemStack, getRequiredLevel(), jobInstance);
    }

    @Override
    public String toString() {
        return "ItemCraftingRestriction{" +
                "item=" + item +
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
                    GsonHelper.getAsItem(jsonObject, "item")
            );
        }
    }
}
