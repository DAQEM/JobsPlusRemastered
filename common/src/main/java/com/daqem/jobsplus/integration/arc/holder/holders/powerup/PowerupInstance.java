package com.daqem.jobsplus.integration.arc.holder.holders.powerup;

import com.daqem.arc.api.action.data.ActionData;
import com.daqem.arc.api.action.holder.AbstractActionHolder;
import com.daqem.arc.api.action.holder.serializer.IActionHolderSerializer;
import com.daqem.arc.api.action.holder.type.IActionHolderType;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.data.serializer.JobsPlusSerializer;
import com.daqem.jobsplus.integration.arc.holder.type.JobsPlusActionHolderType;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
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
import java.util.List;

public class PowerupInstance extends AbstractActionHolder {

    private final ResourceLocation jobLocation;
    private final @Nullable ResourceLocation parentLocation;
    private final String name;
    private final String description;
    private final ItemStack icon;
    private final int price;
    private final int requiredLevel;

    public PowerupInstance(ResourceLocation location, ResourceLocation jobLocation, @Nullable ResourceLocation parentLocation, String name, String description, ItemStack icon, int price, int requiredLevel) {
        super(location);
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

    @Override
    public IActionHolderType<?> getType() {
        return JobsPlusActionHolderType.POWERUP_INSTANCE;
    }

    @Nullable
    public static PowerupInstance of(ResourceLocation location) {
        return PowerupManager.getInstance().getAllPowerups().get(location);
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

    public PowerupInstance getParent() {
        return parentLocation == null ? null : PowerupManager.getInstance().getAllPowerups().get(parentLocation);
    }

    public List<PowerupInstance> getChildren() {
        return PowerupManager.getInstance().getAllPowerups().values().stream()
                .filter(powerupInstance -> powerupInstance.getParentLocation() != null && powerupInstance.getParentLocation().equals(this.getLocation()))
                .toList();
    }

    public static class Serializer implements JobsPlusSerializer<PowerupInstance>, IActionHolderSerializer<PowerupInstance> {

        @Override
        public PowerupInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String parentLocation = GsonHelper.getAsString(jsonObject, "parent", null);
            return new PowerupInstance(
                    new ResourceLocation(GsonHelper.getAsString(jsonObject, "location")),
                    new ResourceLocation(GsonHelper.getAsString(jsonObject, "job")),
                    parentLocation == null ? null : new ResourceLocation(parentLocation),
                    GsonHelper.getAsString(jsonObject, "name"),
                    GsonHelper.getAsString(jsonObject, "description"),
                    getItemStack(GsonHelper.getAsJsonObject(jsonObject, "icon")),
                    GsonHelper.getAsInt(jsonObject, "price"),
                    GsonHelper.getAsInt(jsonObject, "required_level"));
        }

        @Override
        public PowerupInstance fromJson(ResourceLocation location, JsonObject jsonObject, int i) {
            return null;
        }

        @Override
        public PowerupInstance fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf, int i) {
            return fromNetwork(friendlyByteBuf);
        }

        public PowerupInstance fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return new PowerupInstance(
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readResourceLocation(),
                    friendlyByteBuf.readBoolean() ? friendlyByteBuf.readResourceLocation() : null,
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readUtf(),
                    friendlyByteBuf.readItem(),
                    friendlyByteBuf.readInt(),
                    friendlyByteBuf.readInt()
            );
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PowerupInstance powerupInstance) {
            friendlyByteBuf.writeResourceLocation(powerupInstance.getLocation());
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
        }
    }
}
