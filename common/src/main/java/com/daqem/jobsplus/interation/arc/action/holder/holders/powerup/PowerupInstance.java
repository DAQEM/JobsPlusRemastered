package com.daqem.jobsplus.interation.arc.action.holder.holders.powerup;

import com.daqem.arc.api.action.IAction;
import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.action.holder.type.IActionHolderType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.data.serializer.JobsPlusSerializer;
import com.daqem.jobsplus.interation.arc.action.holder.type.JobsPlusActionHolderType;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PowerupInstance implements IActionHolder {

    private ResourceLocation location;
    private final ResourceLocation jobLocation;
    private final @Nullable ResourceLocation parentLocation;
    private final String name;
    private final String description;
    private final ItemStack icon;
    private final int price;
    private final int requiredLevel;

    private final List<IAction> actions = new ArrayList<>();
    private @Nullable PowerupInstance parent;
    private final List<PowerupInstance> children = new ArrayList<>();

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

    public void setParent(@Nullable PowerupInstance parent) {
        this.parent = parent;
    }

    public @Nullable PowerupInstance getParent() {
        return parent;
    }

    public List<PowerupInstance> getChildren() {
        return children;
    }

    public boolean hasChildPowerups() {
        return !children.isEmpty();
    }

    public void addChild(PowerupInstance powerupInstance) {
        children.add(powerupInstance);
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public List<IAction> getActions() {
        return actions;
    }

    @Override
    public void addAction(IAction action) {
        actions.add(action);
    }

    @Override
    public IActionHolderType<?> getType() {
        return JobsPlusActionHolderType.POWERUP_INSTANCE;
    }

    @Nullable
    public static PowerupInstance of(ResourceLocation location) {
        return PowerupManager.getInstance().getAllPowerups().get(location);
    }

    public void clearActions() {
        actions.clear();
    }

    @Override
    public boolean passedHolderCondition(ActionData actionData) {
        ArcPlayer arcPlayer = actionData.getPlayer();
        if (arcPlayer instanceof JobsPlayer jobsPlayer) {
            Job job = jobsPlayer.jobsplus$getJobs().stream().filter(job1 -> job1.getJobInstance().getLocation().equals(this.getJobLocation())).findFirst().orElse(null);
            if (job != null) {
                Powerup powerup = job.getPowerupManager().getAllPowerups().stream().filter(powerup1 -> powerup1.getPowerupInstance().getLocation().equals(this.getLocation())).findFirst().orElse(null);
                if (powerup != null) {
                    return powerup.getPowerupState() == PowerupState.ACTIVE;
                }
            }
        }
        return false;
    }

    public static class Serializer implements JobsPlusSerializer<PowerupInstance> {

        @Override
        public PowerupInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String parentLocation = GsonHelper.getAsString(jsonObject, "parent", null);
            return new PowerupInstance(
                    new ResourceLocation(GsonHelper.getAsString(jsonObject, "job")),
                    parentLocation == null ? null : new ResourceLocation(parentLocation),
                    GsonHelper.getAsString(jsonObject, "name"),
                    GsonHelper.getAsString(jsonObject, "description"),
                    getItemStack(GsonHelper.getAsJsonObject(jsonObject, "icon")),
                    GsonHelper.getAsInt(jsonObject, "price"),
                    GsonHelper.getAsInt(jsonObject, "required_level"));
        }

        public PowerupInstance fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            PowerupInstance powerup = new PowerupInstance(
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readBoolean() ? friendlyByteBuf.readResourceLocation() : null,
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readItem(),
                    friendlyByteBuf.readInt(),
                    friendlyByteBuf.readInt()
            );
            powerup.setLocation(friendlyByteBuf.readResourceLocation());
            return powerup;
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PowerupInstance powerupInstance) {
            friendlyByteBuf.writeResourceLocation(powerupInstance.getJobLocation());
            friendlyByteBuf.writeBoolean(powerupInstance.getParentLocation() != null);
            if (powerupInstance.getParentLocation() != null) {
                friendlyByteBuf.writeResourceLocation(powerupInstance.getParentLocation());
            }
            friendlyByteBuf.writeUtf(powerupInstance.getName());
            friendlyByteBuf.writeUtf(powerupInstance.getDescription());
            friendlyByteBuf.writeItem(powerupInstance.getIcon());
            friendlyByteBuf.writeInt(powerupInstance.getPrice());
            friendlyByteBuf.writeInt(powerupInstance.getRequiredLevel());
            friendlyByteBuf.writeResourceLocation(powerupInstance.getLocation());
        }
    }
}
