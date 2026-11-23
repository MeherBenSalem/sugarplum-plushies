package com.sugarplumstudios.plushies.collection;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Per-player collection progress, stored in Forge's persisted player data so it
 * survives death. Each species keeps a 4-bit mask of collected variants.
 */
public final class PlushieCollection {
    private static final String COLLECTED = "Collected";
    private static final String CLAIMED = "ClaimedRewards";

    private PlushieCollection() {
    }

    private static CompoundTag data(Player player) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag persisted = persistent.getCompound(Player.PERSISTED_NBT_TAG);
        persistent.put(Player.PERSISTED_NBT_TAG, persisted);
        CompoundTag data = persisted.getCompound(SugarplumPlushies.MOD_ID);
        persisted.put(SugarplumPlushies.MOD_ID, data);
        return data;
    }

    public static int mask(Player player, PlushieSpecies species) {
        return data(player).getCompound(COLLECTED).getInt(species.id());
    }

    public static int[] masks(Player player) {
        CompoundTag collected = data(player).getCompound(COLLECTED);
        PlushieSpecies[] all = PlushieSpecies.values();
        int[] masks = new int[all.length];
        for (int i = 0; i < all.length; i++) {
            masks[i] = collected.getInt(all[i].id());
        }
        return masks;
    }

    /** @return true if this variant was not collected before. */
    public static boolean unlock(Player player, PlushieSpecies species, int variant) {
        CompoundTag data = data(player);
        CompoundTag collected = data.getCompound(COLLECTED);
        int mask = collected.getInt(species.id());
        int bit = 1 << PlushieSpecies.clampVariant(variant);
        if ((mask & bit) != 0) {
            return false;
        }
        collected.putInt(species.id(), mask | bit);
        data.put(COLLECTED, collected);
        return true;
    }

    public static int claimed(Player player) {
        return data(player).getInt(CLAIMED);
    }

    public static void setClaimed(Player player, int claimed) {
        data(player).putInt(CLAIMED, claimed);
    }

    /** Copies collection data between player instances (used on respawn as a safety net). */
    public static void copy(Player from, Player to) {
        CompoundTag source = from.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(SugarplumPlushies.MOD_ID);
        if (!source.isEmpty()) {
            CompoundTag persistent = to.getPersistentData();
            CompoundTag persisted = persistent.getCompound(Player.PERSISTED_NBT_TAG);
            persisted.put(SugarplumPlushies.MOD_ID, source.copy());
            persistent.put(Player.PERSISTED_NBT_TAG, persisted);
        }
    }

    public static int speciesCount(int[] masks) {
        int count = 0;
        for (int mask : masks) {
            if (mask != 0) {
                count++;
            }
        }
        return count;
    }

    public static int variantCount(int[] masks) {
        int count = 0;
        for (int mask : masks) {
            count += Integer.bitCount(mask & 0xF);
        }
        return count;
    }

    public static int sparkleCount(int[] masks) {
        int count = 0;
        for (int mask : masks) {
            if ((mask & (1 << PlushieSpecies.SPARKLE)) != 0) {
                count++;
            }
        }
        return count;
    }
}
