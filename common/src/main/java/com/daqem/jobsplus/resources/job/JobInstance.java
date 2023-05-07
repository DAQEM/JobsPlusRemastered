package com.daqem.jobsplus.resources.job;

import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.resources.JobManager;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    private final List<Action> actions;
    private final List<PowerupInstance> powerupInstances;
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

    public void setLocations(ResourceLocation jobLocation) {
        setLocation(jobLocation);
        setPowerupLocations(jobLocation);
    }

    public void setPowerupLocations(ResourceLocation jobLocation) {
        for (PowerupInstance powerupInstance : powerupInstances) {
            powerupInstance.setLocation(new ResourceLocation(jobLocation + "."
                    + powerupInstance.getName()
                    .toLowerCase()
                    .replace(" ", "_")));
        }
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

    public boolean hasPowerup(PowerupInstance powerupInstance) {
        return powerupInstances.contains(powerupInstance);
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
        return "JobInstance{" +
                "name='" + name +
                ", price=" + price +
                ", maxLevel=" + maxLevel +
                ", color='" + color +
                ", iconItem=" + iconItem +
                ", actions=" + actions +
                ", powerups=" + powerupInstances +
                '}';
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
                .registerTypeHierarchyAdapter(Action.class, new Action.ActionSerializer<>())
                .registerTypeHierarchyAdapter(PowerupInstance.class, new PowerupInstance.PowerupSerializer())
                .create();

        @Override
        public JobInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            List<Action> actions = new ArrayList<>();
            jsonObject.get("actions").getAsJsonArray().forEach(jsonElement -> actions.add(GSON.fromJson(jsonElement, Action.class)));
            List<PowerupInstance> powerupInstances = new ArrayList<>();
            jsonObject.get("powerups").getAsJsonArray().forEach(jsonElement -> powerupInstances.add(GSON.fromJson(jsonElement, PowerupInstance.class)));

            return new JobInstance(
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("price").getAsInt(),
                    jsonObject.get("max_level").getAsInt(),
                    jsonObject.get("color").getAsString(),
                    Registry.ITEM.get(new ResourceLocation(jsonObject.get("icon_item").getAsString())),
                    jsonObject.get("description").getAsString(),
                    jsonObject.has("is_default") && jsonObject.get("is_default").getAsBoolean(),
                    actions,
                    powerupInstances);
        }
    }
}
