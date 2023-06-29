package com.daqem.jobsplus.resources.job;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.crafting.restriction.restrictions.ItemCraftingRestriction;
import com.daqem.jobsplus.resources.crafting.restriction.restrictions.TagCraftingRestriction;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JobInstance {

    private ResourceLocation location;

    private final String name;
    private final int price;
    private final int maxLevel;
    private final String color;
    private final ItemStack iconItem;
    private final String description;
    private final ResourceLocation powerupBackground;
    private final boolean isDefault;
    private List<Action> actions;
    private List<PowerupInstance> powerupInstances;
    private List<CraftingRestriction> craftingRestrictions = null;

    public JobInstance(String name, int price, int maxLevel, String color, ItemStack iconItem, String description, ResourceLocation powerupBackground, boolean isDefault) {
        this(name, price, maxLevel, color, iconItem, description, powerupBackground, isDefault, new ArrayList<>(), new ArrayList<>());
    }

    public JobInstance(String name, int price, int maxLevel, String color, ItemStack iconItem, String description, ResourceLocation powerupBackground, boolean isDefault, List<Action> actions, List<PowerupInstance> powerupInstances) {
        this.name = name;
        this.price = price;
        this.maxLevel = maxLevel;
        this.color = color;
        this.iconItem = iconItem;
        this.description = description;
        this.powerupBackground = powerupBackground;
        this.isDefault = isDefault;
        this.actions = actions;
        this.powerupInstances = powerupInstances;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public int getPrice() {
        return price;
    }

    public int getStopRefund() {
        return price * JobsPlusCommonConfig.jobStopRefundPercentage.get() / 100;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getName() {
        return name;
    }

    public List<PowerupInstance> getPowerups() {
        return powerupInstances;
    }

    public List<PowerupInstance> getAllPowerups() {
        List<PowerupInstance> powerups = new ArrayList<>();
        getPowerupsRecursive(powerupInstances, powerups);
        return powerups;
    }

    private void getPowerupsRecursive(List<PowerupInstance> powerups, List<PowerupInstance> powerupInstances) {
        for (PowerupInstance powerupInstance : powerups) {
            powerupInstances.add(powerupInstance);
            if (powerupInstance.hasChildPowerups()) {
                getPowerupsRecursive(powerupInstance.getPowerups(), powerupInstances);
            }
        }
    }

    public int getColorDecimal() {
        return Integer.parseInt(color.replace("#", ""), 16);
    }

    public ItemStack getIconItem() {
        return iconItem;
    }

    public String getDescription() {
        return description;
    }

    public ResourceLocation getPowerupBackground() {
        return powerupBackground;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(@NotNull List<Action> actions) {
        this.actions = actions;
    }

    public void setCraftingRestrictions(List<CraftingRestriction> craftingRestrictions) {
        this.craftingRestrictions = craftingRestrictions;
    }

    public CraftingResult canCraft(CraftingType craftingType, ItemStack itemStack, int level) {
        if (craftingRestrictions == null || craftingRestrictions.isEmpty()) {
            return new CraftingResult(true);
        }
        for (CraftingRestriction craftingRestriction : craftingRestrictions) {
            CraftingResult craftingResult = craftingRestriction.canCraft(craftingType, itemStack, level, this);
            if (!craftingResult.canCraft()) {
                return craftingResult;
            }
        }
        return new CraftingResult(true);
    }

    @Override
    public String toString() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("price", price);
        json.addProperty("maxLevel", maxLevel);
        json.addProperty("color", color);
        json.addProperty("iconItem", iconItem.getDescriptionId());
        json.add("actions", GsonHelper.parseArray(new Gson().toJson(actions)));
        json.add("powerups", GsonHelper.parseArray(new Gson().toJson(powerupInstances)));
        return json.toString();
    }

    public String toShortString() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("price", price);
        return json.toString();
    }

    @Nullable
    public static JobInstance of(ResourceLocation location) {
        return JobManager.getInstance().getJobs().get(location);
    }

    public List<CraftingRestriction> getCraftingRestrictions() {
        return craftingRestrictions != null ? craftingRestrictions : new ArrayList<>();
    }

    public List<ItemCraftingRestriction> getItemCraftingRestrictions() {
        if (craftingRestrictions == null) {
            return new ArrayList<>();
        }
        List<ItemCraftingRestriction> itemCraftingRestrictions = new ArrayList<>();
        for (CraftingRestriction craftingRestriction : craftingRestrictions) {
            if (craftingRestriction instanceof ItemCraftingRestriction) {
                itemCraftingRestrictions.add((ItemCraftingRestriction) craftingRestriction);
            }
            if (craftingRestriction instanceof TagCraftingRestriction tagCraftingRestriction) {
                itemCraftingRestrictions.addAll(tagCraftingRestriction.toItemCraftingRestrictions());
            }
        }
        return itemCraftingRestrictions;
    }

    public void setPowerups(List<PowerupInstance> powerupInstances) {
        this.powerupInstances = powerupInstances;
    }

    public static class JobInstanceSerializer implements JsonDeserializer<JobInstance> {

        @Override
        public JobInstance deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();

            JsonObject iconObject = GsonHelper.getAsJsonObject(json, "icon");
            Item icon = GsonHelper.getAsItem(iconObject, "item");
            ItemStack iconStack = new ItemStack(icon);
            iconStack.setCount(GsonHelper.getAsInt(iconObject, "count", 1));
            if (iconObject.has("tag")) {
                try {
                    iconStack.setTag(TagParser.parseTag(GsonHelper.getAsString(iconObject, "tag")));
                } catch (CommandSyntaxException e) {
                    JobsPlus.LOGGER.error("Error parsing tag for PowerupInstance icon {}: {}", GsonHelper.getAsString(iconObject, "item"), e.getMessage());
                }
            }

            return new JobInstance(
                    GsonHelper.getAsString(json, "name"),
                    GsonHelper.getAsInt(json, "price", 10),
                    GsonHelper.getAsInt(json, "max_level", 100),
                    GsonHelper.getAsString(json, "color"),
                    iconStack,
                    GsonHelper.getAsString(json, "description"),
                    new ResourceLocation(GsonHelper.getAsString(json, "background", "minecraft:textures/block/stone.png")),
                    GsonHelper.getAsBoolean(json, "is_default", false));
        }
    }
}
