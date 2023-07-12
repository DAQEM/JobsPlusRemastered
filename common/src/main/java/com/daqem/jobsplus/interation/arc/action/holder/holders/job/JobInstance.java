package com.daqem.jobsplus.interation.arc.action.holder.holders.job;

import com.daqem.arc.api.action.IAction;
import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.action.holder.type.IActionHolderType;
import com.daqem.itemrestrictions.data.ItemRestriction;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.data.serializer.JobsPlusSerializer;
import com.daqem.jobsplus.interation.arc.action.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.interation.arc.action.holder.type.JobsPlusActionHolderType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JobInstance implements IActionHolder {

    private ResourceLocation location;

    private final String name;
    private final int price;
    private final int maxLevel;
    private final String color;
    private final ItemStack iconItem;
    private final String description;
    private final ResourceLocation powerupBackground;
    private final boolean isDefault;
    private final List<IAction> actions;
    private List<PowerupInstance> powerupInstances;
    private List<ItemRestriction> itemRestrictions;

    public JobInstance(String name, int price, int maxLevel, String color, ItemStack iconItem, String description, ResourceLocation powerupBackground, boolean isDefault) {
        this(name, price, maxLevel, color, iconItem, description, powerupBackground, isDefault, new ArrayList<>(), new ArrayList<>());
    }

    public JobInstance(String name, int price, int maxLevel, String color, ItemStack iconItem, String description, ResourceLocation powerupBackground, boolean isDefault, List<IAction> actions, List<PowerupInstance> powerupInstances) {
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

    public List<ItemRestriction> getItemRestrictions() {
        if (itemRestrictions == null) {
            itemRestrictions = new ArrayList<>();
            //TODO: Get item restrictions from manager based on the conditions
        }
        return itemRestrictions;
    }

    public void setPowerups(List<PowerupInstance> powerupInstances) {
        this.powerupInstances = powerupInstances;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public List<IAction> getActions() {
        return actions;
    }

    @Override
    public void addAction(IAction action) {
        actions.add(action);
    }

    @Override
    public IActionHolderType<?> getType() {
        return JobsPlusActionHolderType.JOB_INSTANCE;
    }

    @Nullable
    public static JobInstance of(ResourceLocation location) {
        return JobManager.getInstance().getJobs().get(location);
    }

    public static class Serializer implements JobsPlusSerializer<JobInstance> {

        @Override
        public JobInstance deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            return new JobInstance(
                    GsonHelper.getAsString(json, "name"),
                    GsonHelper.getAsInt(json, "price"),
                    GsonHelper.getAsInt(json, "max_level"),
                    GsonHelper.getAsString(json, "color"),
                    getItemStack(GsonHelper.getAsJsonObject(json, "icon")),
                    GsonHelper.getAsString(json, "description"),
                    new ResourceLocation(GsonHelper.getAsString(json, "background", "minecraft:textures/block/stone.png")),
                    GsonHelper.getAsBoolean(json, "is_default", false));
        }

        public JobInstance fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            JobInstance job = new JobInstance(
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readVarInt(),
                    friendlyByteBuf.readVarInt(),
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readItem(),
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readBoolean()
            );
            job.setLocation(friendlyByteBuf.readResourceLocation());
            return job;
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobInstance jobInstance) {
            friendlyByteBuf.writeUtf(jobInstance.name);
            friendlyByteBuf.writeVarInt(jobInstance.price);
            friendlyByteBuf.writeVarInt(jobInstance.maxLevel);
            friendlyByteBuf.writeUtf(jobInstance.color);
            friendlyByteBuf.writeItem(jobInstance.iconItem);
            friendlyByteBuf.writeUtf(jobInstance.description);
            friendlyByteBuf.writeResourceLocation(jobInstance.powerupBackground);
            friendlyByteBuf.writeBoolean(jobInstance.isDefault);
            friendlyByteBuf.writeResourceLocation(jobInstance.location);
        }
    }
}
