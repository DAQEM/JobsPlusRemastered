package com.daqem.jobsplus.resources.crafting;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.JobInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CraftingResult {

    private final boolean canCraft;
    private final CraftingType craftingType;
    private final ItemStack itemStack;
    private final int requiredLevel;
    private final JobInstance jobInstance;

    public CraftingResult(boolean canCraft) {
        this.canCraft = canCraft;
        this.craftingType = null;
        this.itemStack = null;
        this.requiredLevel = 0;
        this.jobInstance = null;
    }

    public CraftingResult(boolean canCraft, CraftingType craftingType, ItemStack itemStack, int requiredLevel, JobInstance jobInstance) {
        this.canCraft = canCraft;
        this.craftingType = craftingType;
        this.itemStack = itemStack;
        this.requiredLevel = requiredLevel;
        this.jobInstance = jobInstance;
    }

    public boolean canCraft() {
        return canCraft;
    }

    public MutableComponent getMessage() {
        if (craftingType == null || itemStack == null || jobInstance == null) {
            return JobsPlus.literal("");
        }
        return craftingType.getMessage(jobInstance, requiredLevel, itemStack);
    }

    public CraftingType getCraftingType() {
        return craftingType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void sendHotbarMessage(JobsServerPlayer serverPlayer) {
        if (craftingType == null || itemStack == null || jobInstance == null) {
            return;
        }
        if (JobsPlusCommonConfig.showRestrictionMessage.get()) {
            serverPlayer.getServerPlayer().sendSystemMessage(getMessage(), true);
        }
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("canCraft", canCraft);
        tag.putInt("requiredLevel", requiredLevel);
        if (craftingType != null) tag.putString("craftingType", craftingType.name());
        if (itemStack != null) itemStack.save(tag);
        if (jobInstance != null) tag.putString("jobInstance", jobInstance.getLocation().toString());
        return tag;
    }

    public static CraftingResult fromNBT(CompoundTag tag) {
        CraftingType craftingType = null;
        if (!tag.getString("craftingType").isEmpty()) {
            craftingType = CraftingType.valueOf(tag.getString("craftingType"));
        }
        return new CraftingResult(
                tag.getBoolean("canCraft"),
                craftingType,
                ItemStack.of(tag),
                tag.getInt("requiredLevel"),
                JobManager.getInstance().getJob(new ResourceLocation(tag.getString("jobInstance")))
        );
    }
}
