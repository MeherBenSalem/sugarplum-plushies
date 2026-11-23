package com.sugarplumstudios.plushies.plushie;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public enum PlushieRarity {
    COMMON("common", 0xF3E9F7, 1),
    UNCOMMON("uncommon", 0x9DEBB8, 2),
    RARE("rare", 0x8ECFFF, 3),
    EPIC("epic", 0xD8A6FF, 4),
    SPARKLE("sparkle", 0xFFD36E, 5);

    private final String id;
    private final int color;
    private final int stars;

    PlushieRarity(String id, int color, int stars) {
        this.id = id;
        this.color = color;
        this.stars = stars;
    }

    public String id() {
        return id;
    }

    public int color() {
        return color;
    }

    public int stars() {
        return stars;
    }

    public MutableComponent displayName() {
        return Component.translatable("rarity.sugarplumplushies." + id)
                .withStyle(style -> style.withColor(TextColor.fromRgb(color)));
    }

    public MutableComponent starLine() {
        return Component.literal("★".repeat(stars) + "☆".repeat(5 - stars))
                .withStyle(style -> style.withColor(TextColor.fromRgb(color)));
    }
}
