package com.sugarplumstudios.plushies.collection;

import com.sugarplumstudios.plushies.block.PastelColor;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

/** Collection-book milestones and the rewards they unlock. Rewards are fixed, never random. */
public enum CollectionMilestone {
    CUDDLE_STARTER(Kind.SPECIES, 3),
    SHELF_STYLIST(Kind.SPECIES, 6),
    SWEET_COLLECTOR(Kind.SPECIES, 9),
    PLUSHIE_CURATOR(Kind.SPECIES, 12),
    SUGARPLUM_MASTER(Kind.SPECIES, 15),
    SPARKLE_HUNTER(Kind.SPARKLES, 5),
    GRAND_COLLECTOR(Kind.VARIANTS, PlushieSpecies.TOTAL_VARIANTS);

    public enum Kind {
        SPECIES, VARIANTS, SPARKLES
    }

    private final Kind kind;
    private final int required;
    private final String id;

    CollectionMilestone(Kind kind, int required) {
        this.kind = kind;
        this.required = required;
        this.id = name().toLowerCase(Locale.ROOT);
    }

    public int required() {
        return required;
    }

    public int bit() {
        return 1 << ordinal();
    }

    public int progress(int[] masks) {
        return switch (kind) {
            case SPECIES -> PlushieCollection.speciesCount(masks);
            case VARIANTS -> PlushieCollection.variantCount(masks);
            case SPARKLES -> PlushieCollection.sparkleCount(masks);
        };
    }

    public boolean reached(int[] masks) {
        return progress(masks) >= required;
    }

    public MutableComponent displayName() {
        return Component.translatable("milestone.sugarplumplushies." + id);
    }

    public MutableComponent requirement() {
        return Component.translatable("milestone.sugarplumplushies.requirement." + kind.name().toLowerCase(Locale.ROOT), required);
    }

    public List<ItemStack> rewards() {
        return switch (this) {
            case CUDDLE_STARTER -> List.of(new ItemStack(ModItems.SOFT_FABRIC.get(), 6),
                    new ItemStack(ModItems.PLUSH_STUFFING.get(), 6), new ItemStack(ModItems.BUTTON_EYES.get(), 2));
            case SHELF_STYLIST -> List.of(new ItemStack(ModItems.PLUSHIE_SHELF.get(), 2), new ItemStack(ModItems.DISPLAY_STAND.get()));
            case SWEET_COLLECTOR -> List.of(new ItemStack(ModItems.SUGAR_STAR.get(), 2), new ItemStack(ModItems.DISPLAY_CABINET.get()));
            case PLUSHIE_CURATOR -> List.of(new ItemStack(ModItems.SUGAR_STAR.get(), 4),
                    new ItemStack(ModItems.BEDS.get(PastelColor.PINK).get()));
            case SUGARPLUM_MASTER -> List.of(new ItemStack(ModItems.GOLDEN_COLLECTOR_TROPHY.get()),
                    new ItemStack(ModItems.SUGAR_STAR.get(), 4));
            case SPARKLE_HUNTER -> List.of(new ItemStack(ModItems.SUGAR_STAR.get(), 6));
            case GRAND_COLLECTOR -> List.of(new ItemStack(ModItems.RAINBOW_COLLECTOR_TROPHY.get()));
        };
    }
}
