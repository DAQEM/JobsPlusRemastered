package com.daqem.jobsplus.resources.crafting.restriction.restrictions;

import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.google.gson.*;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TagCraftingRestriction extends CraftingRestriction {

    private final TagKey<Item> tag;

    public TagCraftingRestriction(int requiredLevel, boolean canCraft, boolean canSmelt, boolean canBrew, boolean canEnchant, boolean canRepair, boolean canUseItem, boolean canBreakBlock, boolean canItemBreakBlock, boolean canPlaceBlock, boolean canHurtEntity, TagKey<Item> tag) {
        super(requiredLevel, canCraft, canSmelt, canBrew, canEnchant, canRepair, canUseItem, canBreakBlock, canItemBreakBlock, canPlaceBlock, canHurtEntity);
        this.tag = tag;
    }

    @Override
    public CraftingResult canCraft(CraftingType craftingType, ItemStack itemStack, int level, JobInstance jobInstance) {
        return new CraftingResult(!itemStack.is(tag) || (itemStack.is(tag) && canCraft(craftingType, level)), craftingType, itemStack, getRequiredLevel(), jobInstance);
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = ItemStack.EMPTY;
        if (Registry.ITEM.getTagOrEmpty(tag) instanceof HolderSet.Named<Item> list) {
            if (list.get(0).unwrapKey().isPresent()) {
                itemStack = new ItemStack(Registry.ITEM.get(list.get(0).unwrapKey().get()));
            }
        }
        return itemStack;
    }

    public List<ItemStack> getItemStacks() {
        List<ItemStack> itemStacks = new ArrayList<>();
        if (Registry.ITEM.getTagOrEmpty(tag) instanceof HolderSet.Named<Item> list) {
            for (Holder<Item> item : list) {
                if (item.unwrapKey().isPresent()) {
                    itemStacks.add(new ItemStack(Registry.ITEM.get(item.unwrapKey().get())));
                }
            }
        }
        return itemStacks;
    }

    public List<ItemCraftingRestriction> toItemCraftingRestrictions() {
        List<ItemCraftingRestriction> itemCraftingRestrictions = new ArrayList<>();
        if (Registry.ITEM.getTagOrEmpty(tag) instanceof HolderSet.Named<Item> list) {
            for (Holder<Item> item : list) {
                if (item.unwrapKey().isPresent()) {
                    itemCraftingRestrictions.add(new ItemCraftingRestriction(
                            getRequiredLevel(),
                            isCanCraft(),
                            isCanSmelt(),
                            isCanBrew(),
                            isCanEnchant(),
                            isCanRepair(),
                            isCanUseItem(),
                            isCanBreakBlock(),
                            isCanItemBreakBlock(),
                            isCanPlaceBlock(),
                            isCanHurtEntity(),
                            new ItemInput(item, null)
                    ));
                }
            }
        }
        return itemCraftingRestrictions;
    }

    @Override
    public String toString() {
        return "TagCraftingRestriction{" +
                "tag=" + tag +
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

    public static class Deserializer implements JsonDeserializer<TagCraftingRestriction> {

        @Override
        public TagCraftingRestriction deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new TagCraftingRestriction(
                    GsonHelper.getAsInt(jsonObject, "required_level"),
                    GsonHelper.getAsBoolean(jsonObject, "can_craft", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_smelt", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_brew", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_enchant", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_repair", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_use_item", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_break_block", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_item_break_block", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_place_block", true),
                    GsonHelper.getAsBoolean(jsonObject, "can_hurt_entity", true),
                    TagKey.create(
                            Registry.ITEM_REGISTRY,
                            new ResourceLocation(
                                    GsonHelper.getAsString(jsonObject, "tag")
                            )
                    )
            );
        }
    }
}
