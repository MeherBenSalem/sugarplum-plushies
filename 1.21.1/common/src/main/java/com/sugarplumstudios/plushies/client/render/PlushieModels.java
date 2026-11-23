package com.sugarplumstudios.plushies.client.render;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.client.ClientPlatform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Locations of the baked plushie body and part models. They are registered as
 * additional models so shelves, stands and placed plushies can all draw them.
 */
public final class PlushieModels {
    private static final PlushieSpecies[] SPECIES = PlushieSpecies.values();
    private static final ResourceLocation[][] BODIES = new ResourceLocation[SPECIES.length][PlushieSpecies.VARIANT_COUNT];
    private static final ResourceLocation[][][] PARTS = new ResourceLocation[SPECIES.length][PlushieSpecies.VARIANT_COUNT][];

    static {
        for (PlushieSpecies species : SPECIES) {
            List<PlushiePartTable.Part> parts = PlushiePartTable.parts(species);
            for (int variant = 0; variant < PlushieSpecies.VARIANT_COUNT; variant++) {
                String base = "block/plushie/" + species.id() + "/" + species.variantId(variant);
                BODIES[species.ordinal()][variant] = SugarplumPlushies.id(base);
                ResourceLocation[] partLocations = new ResourceLocation[parts.size()];
                for (int i = 0; i < parts.size(); i++) {
                    partLocations[i] = SugarplumPlushies.id(base + "_" + parts.get(i).name());
                }
                PARTS[species.ordinal()][variant] = partLocations;
            }
        }
    }

    private PlushieModels() {
    }

    public static void registerAll(Consumer<ResourceLocation> event) {
        for (PlushieSpecies species : SPECIES) {
            for (int variant = 0; variant < PlushieSpecies.VARIANT_COUNT; variant++) {
                event.accept(BODIES[species.ordinal()][variant]);
                for (ResourceLocation part : PARTS[species.ordinal()][variant]) {
                    event.accept(part);
                }
            }
        }
    }

    public static BakedModel body(PlushieSpecies species, int variant) {
        return ClientPlatform.INSTANCE.getModel(BODIES[species.ordinal()][PlushieSpecies.clampVariant(variant)]);
    }

    public static BakedModel part(PlushieSpecies species, int variant, int index) {
        return ClientPlatform.INSTANCE.getModel(PARTS[species.ordinal()][PlushieSpecies.clampVariant(variant)][index]);
    }
}
