package com.daqem.jobsplus.resources.job;

import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.crafting.restriction.CraftingRestriction;
import com.daqem.jobsplus.resources.crafting.restriction.restrictions.ItemCraftingRestriction;
import com.daqem.jobsplus.resources.crafting.restriction.restrictions.TagCraftingRestriction;
import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    private final Item iconItem;
    private final String description;
    private final boolean isDefault;
    private List<Action> actions;
    private List<PowerupInstance> powerupInstances;
    private List<CraftingRestriction> craftingRestrictions = null;

    public JobInstance(String name, int price, int maxLevel, String color, Item iconItem, String description, boolean isDefault, List<Action> actions, List<PowerupInstance> powerupInstances) {
        this.name = name;
        this.price = price;
        this.maxLevel = maxLevel;
        this.color = color;
        this.iconItem = iconItem;
        this.description = description;
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
            getPowerupsRecursive(powerupInstance.getPowerups(), powerupInstances);
        }
    }

    public int getColorDecimal() {
        return Integer.parseInt(color.replace("#", ""), 16);
    }

    public Item getIconItem() {
        return iconItem;
    }

    public String getDescription() {
        return description;
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

    public static class JobInstanceSerializer implements JsonDeserializer<JobInstance> {

        private static final Gson GSON = new GsonBuilder()
//                .registerTypeHierarchyAdapter(Action.class, new Action.ActionSerializer<>())
                .registerTypeHierarchyAdapter(PowerupInstance.class, new PowerupInstance.PowerupSerializer())
                .create();

        @Override
        public JobInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

//            List<Action> actions = new ArrayList<>();
//            JsonElement actionsElement = jsonObject.get("actions");
//            if (actionsElement != null && actionsElement.isJsonArray())
//                actionsElement.getAsJsonArray().forEach(jsonElement -> actions.add(GSON.fromJson(jsonElement, Action.class)));

            List<PowerupInstance> powerupInstances = new ArrayList<>();
            JsonElement powerupsElement = jsonObject.get("powerups");
            if (powerupsElement != null && powerupsElement.isJsonArray())
                powerupsElement.getAsJsonArray().forEach(jsonElement -> powerupInstances.add(GSON.fromJson(jsonElement, PowerupInstance.class)));

            return new JobInstance(
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("price").getAsInt(),
                    jsonObject.get("max_level").getAsInt(),
                    jsonObject.get("color").getAsString(),
                    Registry.ITEM.get(new ResourceLocation(jsonObject.get("icon_item").getAsString())),
                    jsonObject.get("description").getAsString(),
                    jsonObject.has("is_default") && jsonObject.get("is_default").getAsBoolean(),
                    new ArrayList<>(),
                    powerupInstances);
        }
    }
}
