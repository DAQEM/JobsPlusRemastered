package com.daqem.jobsplus.client.chat;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ChatColor implements StringRepresentable {
    CUSTOM("CUSTOM", 't', 99, Integer.parseInt("4D94D9", 16));

    public static final Codec<ChatColor> CODEC = StringRepresentable.fromEnum(ChatColor::values);
    public static final char PREFIX_CODE = '§';
    private static final Map<String, ChatColor> FORMATTING_BY_NAME =
            Arrays.stream(values()).collect(Collectors.toMap((chatFormatting) ->
                    cleanName(chatFormatting.name), (chatFormatting) ->
                    chatFormatting));
    private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    private final String name;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private final int id;
    @Nullable
    private final Integer color;

    private static String cleanName(String string) {
        return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    ChatColor(String string2, char c, int j, @Nullable Integer integer) {
        this(string2, c, false, j, integer);
    }

    ChatColor(String string2, char c, boolean bl) {
        this(string2, c, bl, -1, null);
    }

    ChatColor(String string2, char c, boolean bl, int j, @Nullable Integer integer) {
        this.name = string2;
        this.code = c;
        this.isFormat = bl;
        this.id = j;
        this.color = integer;
        this.toString = "§" + c;
    }

    public char getChar() {
        return this.code;
    }

    public int getId() {
        return this.id;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat;
    }

    @Nullable
    public Integer getColor() {
        return this.color;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    @Nullable
    public static String stripFormatting(@Nullable String string) {
        return string == null ? null : STRIP_FORMATTING_PATTERN.matcher(string).replaceAll("");
    }

    @Nullable
    public static ChatColor getByName(@Nullable String string) {
        return string == null ? null : FORMATTING_BY_NAME.get(cleanName(string));
    }

    @Nullable
    public static ChatColor getById(int i) {

        for (ChatColor chatFormatting : values()) {
            if (chatFormatting.getId() == i) {
                return chatFormatting;
            }
        }

        return null;

    }

    @Nullable
    public static ChatColor getByCode(char c) {
        char d = Character.toString(c).toLowerCase(Locale.ROOT).charAt(0);

        for (ChatColor chatFormatting : values()) {
            if (chatFormatting.code == d) {
                return chatFormatting;
            }
        }

        return null;
    }

    public static Collection<String> getNames(boolean bl, boolean bl2) {
        List<String> list = new ArrayList<>();

        for (ChatColor chatFormatting : values()) {
            if ((!chatFormatting.isColor() || bl) && (!chatFormatting.isFormat() || bl2)) {
                list.add(chatFormatting.getName());
            }
        }

        return list;
    }

    public @NotNull String getSerializedName() {
        return this.getName();
    }
}
