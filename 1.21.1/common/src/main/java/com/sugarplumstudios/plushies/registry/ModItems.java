package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.platform.RegistryEntry;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.core.registries.Registries;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.PastelColor;
import com.sugarplumstudios.plushies.item.CollectionBookItem;
import com.sugarplumstudios.plushies.item.MaterialItem;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModItems {

    public static final Map<PlushieSpecies, RegistryEntry<PlushieItem>> PLUSHIES = new EnumMap<>(PlushieSpecies.class);

    static {
        for (PlushieSpecies species : PlushieSpecies.values()) {
            PLUSHIES.put(species, Services.PLATFORM.register(Registries.ITEM, species.id(),
                    () -> new PlushieItem(ModBlocks.PLUSHIES.get(species).get(), species, new Item.Properties().stacksTo(16))));
        }
    }

    // Crafting materials
    public static final RegistryEntry<Item> SOFT_FABRIC = material("soft_fabric", false, Rarity.COMMON);
    public static final RegistryEntry<Item> PLUSH_STUFFING = material("plush_stuffing", false, Rarity.COMMON);
    public static final RegistryEntry<Item> BUTTON_EYES = material("button_eyes", false, Rarity.COMMON);
    public static final RegistryEntry<Item> EMBROIDERY_THREAD = material("embroidery_thread", false, Rarity.COMMON);
    public static final RegistryEntry<Item> SATIN_RIBBON = material("satin_ribbon", false, Rarity.COMMON);
    public static final RegistryEntry<Item> SUGAR_STAR = material("sugar_star", true, Rarity.UNCOMMON);

    public static final RegistryEntry<CollectionBookItem> PLUSHIE_COLLECTION_BOOK = Services.PLATFORM.register(Registries.ITEM, "plushie_collection_book",
            () -> new CollectionBookItem(new Item.Properties().stacksTo(1)));

    // Decorative blocks
    public static final RegistryEntry<BlockItem> PLUSHIE_WORKSHOP = blockItem("plushie_workshop", ModBlocks.PLUSHIE_WORKSHOP);
    public static final RegistryEntry<BlockItem> PLUSHIE_SHELF = blockItem("plushie_shelf", ModBlocks.PLUSHIE_SHELF);
    public static final RegistryEntry<BlockItem> DISPLAY_STAND = blockItem("display_stand", ModBlocks.DISPLAY_STAND);
    public static final RegistryEntry<BlockItem> DISPLAY_CABINET = blockItem("display_cabinet", ModBlocks.DISPLAY_CABINET);

    public static final Map<PastelColor, RegistryEntry<BlockItem>> BEDS = new EnumMap<>(PastelColor.class);
    public static final Map<PastelColor, RegistryEntry<BlockItem>> BOXES = new EnumMap<>(PastelColor.class);

    static {
        for (PastelColor color : PastelColor.values()) {
            BEDS.put(color, blockItem(color.id() + "_plushie_bed", ModBlocks.BEDS.get(color)));
            BOXES.put(color, blockItem(color.id() + "_plushie_box", ModBlocks.BOXES.get(color)));
        }
    }

    public static final RegistryEntry<BlockItem> GOLDEN_COLLECTOR_TROPHY = Services.PLATFORM.register(Registries.ITEM, "golden_collector_trophy",
            () -> new BlockItem(ModBlocks.GOLDEN_COLLECTOR_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryEntry<BlockItem> RAINBOW_COLLECTOR_TROPHY = Services.PLATFORM.register(Registries.ITEM, "rainbow_collector_trophy",
            () -> new BlockItem(ModBlocks.RAINBOW_COLLECTOR_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    private ModItems() {
    }

    /** Loads the class, which registers everything above. */
    public static void init() {
    }

    private static RegistryEntry<Item> material(String name, boolean foil, Rarity rarity) {
        return Services.PLATFORM.register(Registries.ITEM, name, () -> new MaterialItem(new Item.Properties().rarity(rarity), foil));
    }

    private static RegistryEntry<BlockItem> blockItem(String name, Supplier<? extends Block> block) {
        return Services.PLATFORM.register(Registries.ITEM, name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
