package com.sugarplumstudios.plushies.collection;

import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.SugarplumPlushies;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-player collection progress, saved with the world ({@code data/sugarplumplushies_collection.dat}) so it survives
 * death and is the same on every loader. Each species keeps a 4-bit mask of collected variants.
 */
public final class PlushieCollection extends SavedData {
    public static final SavedDataType<PlushieCollection> TYPE = new SavedDataType<>(SugarplumPlushies.id("collection"),
            PlushieCollection::new, CompoundTag.CODEC.xmap(PlushieCollection::load, data -> data.save(new CompoundTag())),
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
    private static final String COLLECTED = "Collected";
    private static final String CLAIMED = "ClaimedRewards";

    private final Map<UUID, Entry> players = new HashMap<>();

    /** One player's progress. */
    private static final class Entry {
        final int[] masks = new int[PlushieSpecies.values().length];
        int claimed;
    }

    private static PlushieCollection get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    /** The player's progress, or an empty, unsaved entry where there is no server (the client). */
    private static Entry entry(Player player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return new Entry();
        }
        return get(server).players.computeIfAbsent(player.getUUID(), id -> new Entry());
    }

    private static void changed(Player player) {
        MinecraftServer server = player.level().getServer();
        if (server != null) {
            get(server).setDirty();
        }
    }

    public static int mask(Player player, PlushieSpecies species) {
        return entry(player).masks[species.ordinal()];
    }

    public static int[] masks(Player player) {
        return entry(player).masks.clone();
    }

    /** @return true if this variant was not collected before. */
    public static boolean unlock(Player player, PlushieSpecies species, int variant) {
        Entry entry = entry(player);
        int bit = 1 << PlushieSpecies.clampVariant(variant);
        if ((entry.masks[species.ordinal()] & bit) != 0) {
            return false;
        }
        entry.masks[species.ordinal()] |= bit;
        changed(player);
        return true;
    }

    public static int claimed(Player player) {
        return entry(player).claimed;
    }

    public static void setClaimed(Player player, int claimed) {
        entry(player).claimed = claimed;
        changed(player);
    }

    CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        PlushieSpecies[] all = PlushieSpecies.values();
        players.forEach((id, entry) -> {
            CompoundTag player = new CompoundTag();
            player.store("Player", UUIDUtil.CODEC, id);
            CompoundTag collected = new CompoundTag();
            for (int i = 0; i < all.length; i++) {
                if (entry.masks[i] != 0) {
                    collected.putInt(all[i].id(), entry.masks[i]);
                }
            }
            player.put(COLLECTED, collected);
            player.putInt(CLAIMED, entry.claimed);
            list.add(player);
        });
        tag.put("Players", list);
        return tag;
    }

    static PlushieCollection load(CompoundTag tag) {
        PlushieCollection data = new PlushieCollection();
        PlushieSpecies[] all = PlushieSpecies.values();
        for (Tag t : tag.getListOrEmpty("Players")) {
            if (!(t instanceof CompoundTag player)) {
                continue;
            }
            Optional<UUID> uuid = player.read("Player", UUIDUtil.CODEC);
            if (uuid.isEmpty()) {
                continue;
            }
            Entry entry = data.players.computeIfAbsent(uuid.get(), id -> new Entry());
            CompoundTag collected = player.getCompoundOrEmpty(COLLECTED);
            for (int s = 0; s < all.length; s++) {
                entry.masks[s] = collected.getIntOr(all[s].id(), 0) & 0xF;
            }
            entry.claimed = player.getIntOr(CLAIMED, 0);
        }
        return data;
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
