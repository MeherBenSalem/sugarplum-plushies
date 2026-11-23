package com.sugarplumstudios.plushies.client;

import com.sugarplumstudios.plushies.plushie.PlushieSpecies;

/** Client-side mirror of the local player's collection, kept fresh by {@code SyncCollectionPacket}. */
public final class ClientCollectionData {
    private static int[] masks = new int[PlushieSpecies.values().length];
    private static int claimed;

    private ClientCollectionData() {
    }

    public static void set(int[] newMasks, int newClaimed) {
        int[] copy = new int[PlushieSpecies.values().length];
        System.arraycopy(newMasks, 0, copy, 0, Math.min(copy.length, newMasks.length));
        masks = copy;
        claimed = newClaimed;
    }

    public static int[] masks() {
        return masks;
    }

    public static int mask(PlushieSpecies species) {
        return masks[species.ordinal()];
    }

    public static boolean has(PlushieSpecies species, int variant) {
        return (mask(species) & (1 << variant)) != 0;
    }

    public static int claimed() {
        return claimed;
    }
}
