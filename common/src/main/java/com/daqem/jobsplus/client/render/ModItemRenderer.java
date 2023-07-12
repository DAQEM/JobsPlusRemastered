package com.daqem.jobsplus.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ModItemRenderer {

    @SuppressWarnings("deprecation")
    private static void renderGuiItem(ItemRenderer itemRenderer, ItemStack itemStack, int x, int y, BakedModel bakedModel, float scale) {
        itemRenderer.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(x, y, 100.0F + itemRenderer.blitOffset);
        poseStack.translate(8.0, 8.0, 0.0);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack2 = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl = !bakedModel.usesBlockLight();
        if (bl) {
            Lighting.setupForFlatItems();
        }

        itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack2, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) {
            Lighting.setupFor3DItems();
        }

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void renderAndDecorateItem(ItemRenderer itemRenderer, ItemStack itemStack, int x, int y, float scale) {
        ModItemRenderer.tryRenderGuiItem(itemRenderer, Minecraft.getInstance().player, itemStack, x, y, scale);
    }

    private static void tryRenderGuiItem(ItemRenderer itemRenderer, @Nullable LivingEntity livingEntity, ItemStack itemStack, int x, int y, float scale) {
        if (!itemStack.isEmpty()) {
            BakedModel bakedModel = itemRenderer.getModel(itemStack, null, livingEntity, 0);
            itemRenderer.blitOffset = itemRenderer.blitOffset + 50.0F;

            try {
                ModItemRenderer.renderGuiItem(itemRenderer, itemStack, x, y, bakedModel, scale);
            } catch (Throwable var11) {
                CrashReport crashReport = CrashReport.forThrowable(var11, "Rendering item");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
                crashReportCategory.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
                crashReportCategory.setDetail("Item Damage", () -> String.valueOf(itemStack.getDamageValue()));
                crashReportCategory.setDetail("Item NBT", () -> String.valueOf(itemStack.getTag()));
                crashReportCategory.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
                throw new ReportedException(crashReport);
            }

            itemRenderer.blitOffset = itemRenderer.blitOffset - 50.0F;
        }
    }

}
