package com.daqem.jobsplus.mixin.crafting.screen;

import com.daqem.jobsplus.client.screen.JobsCraftingScreen;
import com.daqem.jobsplus.resources.JobManager;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreen.class)
public class MixinCraftingScreen extends Screen implements JobsCraftingScreen {

    private Item cantCraftItem = Items.AIR;
    private JobInstance cantCraftJob = null;
    private int cantCraftRequiredLevel = 0;

    protected MixinCraftingScreen(Component component) {
        super(component);
    }

    @Override
    public void cantCraft(CompoundTag cantCraftData) {
        cantCraftItem = Registry.ITEM.get(new ResourceLocation(cantCraftData.getString("ItemLocation")));
        cantCraftJob = JobManager.getInstance().getJob(new ResourceLocation(cantCraftData.getString("JobLocation")));
        cantCraftRequiredLevel = cantCraftData.getInt("RequiredLevel");
    }

    @Inject(at = @At("TAIL"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V")
    private void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (cantCraftItem != Items.AIR && cantCraftJob != null && cantCraftRequiredLevel != 0) {
            renderCantCraftMessage(poseStack, font, width, height, 166, cantCraftItem, cantCraftJob, cantCraftRequiredLevel);
        }
    }
}
