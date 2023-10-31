package com.daqem.jobsplus.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderColor {

    public static void normal() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void red() {
        RenderSystem.setShaderColor(1.0F, 0.35F, 0.35F, 1.0F);
    }

    public static void grayedOut() {
        RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, 1.0F);
    }

    public static void normalSelected() {
        RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 1.0F);
    }

    public static void grayedOutSelected() {
        RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1.0F);
    }

    public static void buttonHover() {
        RenderSystem.setShaderColor(0.6F, 0.6F, 1F, 1);
    }
}

