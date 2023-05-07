package com.daqem.jobsplus.resources.crafting.restriction;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

public abstract class CraftingRestriction {

    private final int requiredLevel;
    private final boolean canCraft;
    private final boolean canSmelt;
    private final boolean canBrew;
    private final boolean canEnchant;
    private final boolean canRepair;
    private final boolean canUseItem;
    private final boolean canBreakBlock;
    private final boolean canItemBreakBlock;
    private final boolean canPlaceBlock;
    private final boolean canHurtEntity;

    public CraftingRestriction(int requiredLevel, boolean canCraft, boolean canSmelt, boolean canBrew, boolean canEnchant, boolean canRepair, boolean canUseItem, boolean canBreakBlock, boolean canItemBreakBlock, boolean canPlaceBlock, boolean canHurtEntity) {
        this.requiredLevel = requiredLevel;
        this.canCraft = canCraft;
        this.canSmelt = canSmelt;
        this.canBrew = canBrew;
        this.canEnchant = canEnchant;
        this.canRepair = canRepair;
        this.canUseItem = canUseItem;
        this.canBreakBlock = canBreakBlock;
        this.canItemBreakBlock = canItemBreakBlock;
        this.canPlaceBlock = canPlaceBlock;
        this.canHurtEntity = canHurtEntity;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public boolean isCanCraft() {
        return canCraft;
    }

    public boolean isCanSmelt() {
        return canSmelt;
    }

    public boolean isCanBrew() {
        return canBrew;
    }

    public boolean isCanEnchant() {
        return canEnchant;
    }

    public boolean isCanRepair() {
        return canRepair;
    }

    public boolean isCanUseItem() {
        return canUseItem;
    }

    public boolean isCanBreakBlock() {
        return canBreakBlock;
    }

    public boolean isCanItemBreakBlock() {
        return canItemBreakBlock;
    }

    public boolean isCanPlaceBlock() {
        return canPlaceBlock;
    }

    public boolean isCanHurtEntity() {
        return canHurtEntity;
    }

    public abstract CraftingResult canCraft(CraftingType craftingType, ItemStack itemStack, int level, JobInstance jobInstance);

    public boolean canCraft(CraftingType craftingType, int level) {
        return switch (craftingType) {
            case CRAFTING -> canCraft;
            case SMELTING -> canSmelt;
            case BREWING -> canBrew;
            case ENCHANTING -> canEnchant;
            case REPAIRING -> canRepair;
            case USING_ITEM -> canUseItem;
            case BREAKING_BLOCK -> canBreakBlock;
            case ITEM_BREAKING_BLOCK -> canItemBreakBlock;
            case PLACING_BLOCK -> canPlaceBlock;
            case HURTING_ENTITY -> canHurtEntity;
        } || level >= this.requiredLevel;
    }

    public abstract ItemStack getItemStack();

    public static class Deserializer<T extends CraftingRestriction> implements JsonDeserializer<T> {

        private Gson getGson() {
            GsonBuilder builder = new GsonBuilder();

            for (CraftingRestrictionType craftingRestrictionType : CraftingRestrictions.CRAFTING_RESTRICTION_TYPES) {
                builder.registerTypeAdapter(craftingRestrictionType.clazz(), craftingRestrictionType.deserializer());
            }

            return builder.create();
        }

        @Override
        public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject restrictionObject = jsonElement.getAsJsonObject();
            ResourceLocation location;
            if (restrictionObject.has("item")) {
                location = JobsPlus.getId("item");
            } else if (restrictionObject.has("tag")) {
                location = JobsPlus.getId("tag");
            } else {
                throw new JsonParseException("Expected either 'item' or 'tag' in crafting restriction: " + restrictionObject);
            }
            return (T) getGson().fromJson(restrictionObject, CraftingRestrictions.getClass(location));
        }
    }
}
