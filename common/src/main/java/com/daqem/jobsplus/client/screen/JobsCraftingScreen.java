package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

public interface JobsCraftingScreen {

    void cantCraft(CompoundTag cantCraftData);

    default void renderCantCraftMessage(PoseStack poseStack, Font font, int width, int height, int imageHeight, Item item, JobInstance job, int requiredLevel) {
        MutableComponent component = JobsPlus.translatable("inventory.cant_craft", job.getName(), requiredLevel, item.getDefaultInstance().getDisplayName().getString());
        font.draw(poseStack, component.withStyle(ChatFormatting.RED), (width / 2F) - (font.width(component) / 2F), (height - imageHeight) / 4F, 0xFFFFFF);
    }
}
