package com.daqem.jobsplus.integration.arc.holder.holders.job;

import com.daqem.arc.api.action.holder.AbstractActionHolder;
import com.daqem.arc.api.action.holder.ActionHolderManager;
import com.daqem.arc.api.action.holder.serializer.IActionHolderSerializer;
import com.daqem.arc.api.action.holder.type.IActionHolderType;
import com.daqem.itemrestrictions.data.ItemRestriction;
import com.daqem.itemrestrictions.data.ItemRestrictionManager;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusConfig;
import com.daqem.jobsplus.data.serializer.JobsPlusSerializer;
import com.daqem.jobsplus.integration.arc.condition.conditions.job.IJobCondition;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.integration.arc.holder.type.JobsPlusActionHolderType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JobInstance extends AbstractActionHolder {

    private final int price;
    private final int maxLevel;
    private final String color;
    private final ItemStack iconItem;
    private final ResourceLocation powerupBackground;
    private final boolean isDefault;

    public JobInstance(ResourceLocation location, int price, int maxLevel, String color, ItemStack iconItem, ResourceLocation powerupBackground, boolean isDefault) {
        this(location, price, maxLevel, color, iconItem, powerupBackground, isDefault, new ArrayList<>());
    }

    public JobInstance(ResourceLocation location, int price, int maxLevel, String color, ItemStack iconItem, ResourceLocation powerupBackground, boolean isDefault, List<PowerupInstance> powerupInstances) {
        super(location);
        this.price = price;
        this.maxLevel = maxLevel;
        this.color = color;
        this.iconItem = iconItem;
        this.powerupBackground = powerupBackground;
        this.isDefault = isDefault;
    }

    public int getPrice() {
        return price;
    }

    public int getStopRefund() {
        return price * JobsPlusConfig.jobStopRefundPercentage.get() / 100;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public MutableComponent getName() {
        return JobsPlus.translatable("job." + location.getNamespace() + "." + location.getPath() + ".name");
    }

    public MutableComponent getDescription() {
        return JobsPlus.translatable("job." + location.getNamespace() + "." + location.getPath() + ".description");
    }

    public List<PowerupInstance> getPowerups() {
        return ActionHolderManager.getInstance().getActionHolders().stream()
                .filter(actionHolder -> actionHolder instanceof PowerupInstance powerupInstance && powerupInstance.getJobLocation().equals(location))
                .map(actionHolder -> (PowerupInstance) actionHolder)
                .collect(Collectors.toList());
    }

    public int getColorDecimal() {
        return Integer.parseInt(color.replace("#", ""), 16);
    }

    public ItemStack getIconItem() {
        return iconItem;
    }

    public ResourceLocation getPowerupBackground() {
        return powerupBackground;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Map<ItemRestriction, Integer> getItemRestrictions() {
        return ItemRestrictionManager.getInstance().getItemRestrictions().stream()
                .filter(itemRestriction -> itemRestriction.getConditions().stream()
                        .anyMatch(condition -> condition instanceof IJobCondition jobCondition && jobCondition.getJobLocation().equals(location)))
                .collect(Collectors.toMap(
                        itemRestriction -> itemRestriction,
                        itemRestriction -> itemRestriction.getConditions().stream()
                                .filter(condition -> condition instanceof IJobCondition)
                                .map(iCondition -> ((IJobCondition) iCondition).getRequiredLevel())
                                .max(Integer::compareTo)
                                .orElse(0)
                ));
    }

    @Override
    public IActionHolderType<?> getType() {
        return JobsPlusActionHolderType.JOB_INSTANCE;
    }

    @Nullable
    public static JobInstance of(ResourceLocation location) {
        return JobManager.getInstance().getJobs().get(location);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JobInstance jobInstance && jobInstance.location.equals(location);
    }

    public static class Serializer implements JobsPlusSerializer<JobInstance>, IActionHolderSerializer<JobInstance> {

        @Override
        public JobInstance deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            return new JobInstance(
                    new ResourceLocation(GsonHelper.getAsString(json, "location")),
                    GsonHelper.getAsInt(json, "price"),
                    GsonHelper.getAsInt(json, "max_level"),
                    GsonHelper.getAsString(json, "color"),
                    getItemStack(GsonHelper.getAsJsonObject(json, "icon")),
                    new ResourceLocation(GsonHelper.getAsString(json, "background", "minecraft:textures/block/stone.png")),
                    GsonHelper.getAsBoolean(json, "is_default", false));
        }

        @Override
        public JobInstance fromJson(ResourceLocation location, JsonObject jsonObject, int i) {
            return null;
        }

        @Override
        public JobInstance fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, int i) {
            return fromNetwork(friendlyByteBuf);
        }

        public JobInstance fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return new JobInstance(
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readVarInt(),
                    friendlyByteBuf.readVarInt(),
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readItem(),
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readBoolean()
            );
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, JobInstance jobInstance) {
            friendlyByteBuf.writeResourceLocation(jobInstance.location);
            friendlyByteBuf.writeVarInt(jobInstance.price);
            friendlyByteBuf.writeVarInt(jobInstance.maxLevel);
            friendlyByteBuf.writeUtf(jobInstance.color);
            friendlyByteBuf.writeItem(jobInstance.iconItem);
            friendlyByteBuf.writeResourceLocation(jobInstance.powerupBackground);
            friendlyByteBuf.writeBoolean(jobInstance.isDefault);
        }
    }
}
