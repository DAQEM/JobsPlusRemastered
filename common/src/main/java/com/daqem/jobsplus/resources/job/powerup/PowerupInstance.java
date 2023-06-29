package com.daqem.jobsplus.resources.job.powerup;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.resources.job.action.Action;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PowerupInstance {

    private ResourceLocation location;
    private final ResourceLocation jobLocation;
    private final @Nullable ResourceLocation parentLocation;
    private final String name;
    private final String description;
    private final ItemStack icon;
    private final int price;
    private final int requiredLevel;

    private List<Action> actions = new ArrayList<>();
    private @Nullable PowerupInstance parent;
    private List<PowerupInstance> children = new ArrayList<>();

    public PowerupInstance(ResourceLocation jobLocation, @Nullable ResourceLocation parentLocation, String name, String description, ItemStack icon, int price, int requiredLevel) {
        this.jobLocation = jobLocation;
        this.parentLocation = parentLocation;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.price = price;
        this.requiredLevel = requiredLevel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public ResourceLocation getJobLocation() {
        return jobLocation;
    }

    public @Nullable ResourceLocation getParentLocation() {
        return parentLocation;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public int getPrice() {
        return price;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public List<PowerupInstance> getPowerups() {
        return children;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setParent(@Nullable PowerupInstance parent) {
        this.parent = parent;
    }

    public void setParents() {
        for (PowerupInstance powerupInstance : children) {
            powerupInstance.setParent(this);
            powerupInstance.setParents();
        }
    }

    @Nullable
    public static PowerupInstance of(ResourceLocation location) {
        return PowerupManager.getInstance().getAllPowerups().get(location);
    }

    public @Nullable PowerupInstance getParent() {
        return parent;
    }

    public List<PowerupInstance> getChildren() {
        return children;
    }

    public void setActions(@NotNull List<Action> actions) {
        this.actions = actions;
    }

    public boolean hasChildPowerups() {
        return children != null && !children.isEmpty();
    }

    public void addChild(PowerupInstance powerupInstance) {
        children.add(powerupInstance);
    }

    public static class PowerupSerializer implements JsonDeserializer<PowerupInstance> {

        @Override
        public PowerupInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String parentLocation = GsonHelper.getAsString(jsonObject, "parent", null);

            JsonObject iconObject = GsonHelper.getAsJsonObject(jsonObject, "icon");
            Item icon = GsonHelper.getAsItem(iconObject, "item");
            ItemStack iconStack = new ItemStack(icon);
            if (iconObject.has("tag")) {
                try {
                    iconStack.setTag(TagParser.parseTag(GsonHelper.getAsString(iconObject, "tag")));
                } catch (CommandSyntaxException e) {
                    JobsPlus.LOGGER.error("Error parsing tag for PowerupInstance icon {}: {}", GsonHelper.getAsString(iconObject, "item"), e.getMessage());
                }
            }

            return new PowerupInstance(
                    new ResourceLocation(GsonHelper.getAsString(jsonObject, "job")),
                    parentLocation == null ? null : new ResourceLocation(parentLocation),
                    GsonHelper.getAsString(jsonObject, "name"),
                    GsonHelper.getAsString(jsonObject, "description"),
                    iconStack,
                    GsonHelper.getAsInt(jsonObject, "price"),
                    GsonHelper.getAsInt(jsonObject, "required_level"));
        }
    }
}
