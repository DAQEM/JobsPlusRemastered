package com.daqem.jobsplus.util.chat;

import net.minecraft.ChatFormatting;

public class ChatColor {
    public static String black() {
        return getColor("BLACK");
    }

    public static String darkBlue() {
        return getColor("DARK_BLUE");
    }

    public static String darkGreen() {
        return getColor("DARK_GREEN");
    }

    public static String darkAqua() {
        return getColor("DARK_AQUA");
    }

    public static String darkRed() {
        return getColor("DARK_RED");
    }

    public static String darkPurple() {
        return getColor("DARK_PURPLE");
    }

    public static String gold() {
        return getColor("GOLD");
    }

    public static String gray() {
        return getColor("GRAY");
    }

    public static String darkGray() {
        return getColor("DARK_GRAY");
    }

    public static String blue() {
        return getColor("BLUE");
    }

    public static String green() {
        return getColor("GREEN");
    }

    public static String aqua() {
        return getColor("AQUA");
    }

    public static String red() {
        return getColor("RED");
    }

    public static String lightPurple() {
        return getColor("LIGHT_PURPLE");
    }

    public static String yellow() {
        return getColor("YELLOW");
    }

    public static String white() {
        return getColor("WHITE");
    }

    public static String reset() {
        return "" + ChatFormatting.RESET + "";
    }

    public static String bold() {
        return "" + ChatFormatting.BOLD + "";
    }


    public static String boldBlack() {
        return getBoldColor("BLACK");
    }

    public static String boldDarkBlue() {
        return getBoldColor("DARK_BLUE");
    }

    public static String boldDarkGreen() {
        return getBoldColor("DARK_GREEN");
    }

    public static String boldDarkAqua() {
        return getBoldColor("DARK_AQUA");
    }

    public static String boldDarkRed() {
        return getBoldColor("DARK_RED");
    }

    public static String boldDarkPurple() {
        return getBoldColor("DARK_PURPLE");
    }

    public static String boldGold() {
        return getBoldColor("GOLD");
    }

    public static String boldGray() {
        return getBoldColor("GRAY");
    }

    public static String boldDarkGray() {
        return getBoldColor("DARK_GRAY");
    }

    public static String boldBlue() {
        return getBoldColor("BLUE");
    }

    public static String boldGreen() {
        return getBoldColor("GREEN");
    }

    public static String boldAqua() {
        return getBoldColor("AQUA");
    }

    public static String boldRed() {
        return getBoldColor("RED");
    }

    public static String boldLightPurple() {
        return getBoldColor("LIGHT_PURPLE");
    }

    public static String boldYellow() {
        return getBoldColor("YELLOW");
    }

    public static String boldWhite() {
        return getBoldColor("WHITE");
    }

    public static String getColor(String str) {
        return reset() + ChatFormatting.valueOf(str) + "";
    }

    public static String getBoldColor(String str) {
        return getColor(str) + bold();
    }
}
