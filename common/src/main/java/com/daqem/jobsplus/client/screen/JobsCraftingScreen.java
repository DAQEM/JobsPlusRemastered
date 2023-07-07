package com.daqem.jobsplus.client.screen;

import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;

public interface JobsCraftingScreen {

    void cantCraft(CraftingResult craftingResult);

    default void renderCantCraftMessage(PoseStack poseStack, Font font, int width, int height, int imageHeight, CraftingResult craftingResult) {
        MutableComponent component = craftingResult.getMessage();
        font.draw(poseStack, component, (width / 2F) - (font.width(component) / 2F), (height - imageHeight) / 4F, 0xFFFFFF);
    }
}
