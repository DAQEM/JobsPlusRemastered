package com.daqem.jobsplus.util.chat;

import net.minecraft.ChatFormatting;

public class ChatColor {
    @SuppressWarnings("unused")
    public static String black() {
        return getColor("BLACK");
    }

    @SuppressWarnings("unused")
    public static String darkBlue() {
        return getColor("DARK_BLUE");
    }

    @SuppressWarnings("unused")
    public static String darkGreen() {
        return getColor("DARK_GREEN");
    }

    @SuppressWarnings("unused")
    public static String darkAqua() {
        return getColor("DARK_AQUA");
    }

    @SuppressWarnings("unused")
    public static String darkRed() {
        return getColor("DARK_RED");
    }

    @SuppressWarnings("unused")
    public static String darkPurple() {
        return getColor("DARK_PURPLE");
    }

    @SuppressWarnings("unused")
    public static String gold() {
        return getColor("GOLD");
    }

    @SuppressWarnings("unused")
    public static String gray() {
        return getColor("GRAY");
    }

    public static String darkGray() {
        return getColor("DARK_GRAY");
    }

    @SuppressWarnings("unused")
    public static String blue() {
        return getColor("BLUE");
    }

    @SuppressWarnings("unused")
    public static String green() {
        return getColor("GREEN");
    }

    public static String aqua() {
        return getColor("AQUA");
    }

    @SuppressWarnings("unused")
    public static String red() {
        return getColor("RED");
    }

    @SuppressWarnings("unused")
    public static String lightPurple() {
        return getColor("LIGHT_PURPLE");
    }

    @SuppressWarnings("unused")
    public static String yellow() {
        return getColor("YELLOW");
    }

    public static String white() {
        return getColor("WHITE");
    }

    @SuppressWarnings("ConcatenationWithEmptyString")
    public static String reset() {
        return "" + ChatFormatting.RESET + "";
    }

    @SuppressWarnings("ConcatenationWithEmptyString")
    public static String bold() {
        return "" + ChatFormatting.BOLD + "";
    }


    @SuppressWarnings("unused")
    public static String boldBlack() {
        return getBoldColor("BLACK");
    }

    @SuppressWarnings("unused")
    public static String boldDarkBlue() {
        return getBoldColor("DARK_BLUE");
    }

    @SuppressWarnings("unused")
    public static String boldDarkGreen() {
        return getBoldColor("DARK_GREEN");
    }

    @SuppressWarnings("unused")
    public static String boldDarkAqua() {
        return getBoldColor("DARK_AQUA");
    }

    @SuppressWarnings("unused")
    public static String boldDarkRed() {
        return getBoldColor("DARK_RED");
    }

    @SuppressWarnings("unused")
    public static String boldDarkPurple() {
        return getBoldColor("DARK_PURPLE");
    }

    @SuppressWarnings("unused")
    public static String boldGold() {
        return getBoldColor("GOLD");
    }

    @SuppressWarnings("unused")
    public static String boldGray() {
        return getBoldColor("GRAY");
    }

    @SuppressWarnings("unused")
    public static String boldDarkGray() {
        return getBoldColor("DARK_GRAY");
    }

    @SuppressWarnings("unused")
    public static String boldBlue() {
        return getBoldColor("BLUE");
    }

    public static String boldGreen() {
        return getBoldColor("GREEN");
    }

    @SuppressWarnings("unused")
    public static String boldAqua() {
        return getBoldColor("AQUA");
    }

    public static String boldRed() {
        return getBoldColor("RED");
    }

    @SuppressWarnings("unused")
    public static String boldLightPurple() {
        return getBoldColor("LIGHT_PURPLE");
    }

    @SuppressWarnings("unused")
    public static String boldYellow() {
        return getBoldColor("YELLOW");
    }

    @SuppressWarnings("unused")
    public static String boldWhite() {
        return getBoldColor("WHITE");
    }

    @SuppressWarnings("ConcatenationWithEmptyString")
    public static String getColor(String str) {
        return reset() + ChatFormatting.valueOf(str) + "";
    }

    public static String getBoldColor(String str) {
        return getColor(str) + bold();
    }
}
