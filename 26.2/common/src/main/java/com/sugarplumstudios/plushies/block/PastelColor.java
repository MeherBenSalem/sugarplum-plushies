package com.sugarplumstudios.plushies.block;

import net.minecraft.world.level.material.MapColor;

import java.util.Locale;

/** Colours used by plushie beds and plushie boxes. */
public enum PastelColor {
    PINK(MapColor.COLOR_PINK),
    LAVENDER(MapColor.COLOR_PURPLE),
    MINT(MapColor.COLOR_LIGHT_GREEN),
    SKY(MapColor.COLOR_LIGHT_BLUE);

    private final String id;
    private final MapColor mapColor;

    PastelColor(MapColor mapColor) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.mapColor = mapColor;
    }

    public String id() {
        return id;
    }

    public MapColor mapColor() {
        return mapColor;
    }
}
