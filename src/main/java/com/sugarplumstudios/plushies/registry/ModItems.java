package com.sugarplumstudios.plushies.registry;

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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SugarplumPlushies.MOD_ID);

    public static final Map<PlushieSpecies, RegistryObject<PlushieItem>> PLUSHIES = new EnumMap<>(PlushieSpecies.class);

    static {
        for (PlushieSpecies species : PlushieSpecies.values()) {
            PLUSHIES.put(species, ITEMS.register(species.id(),
                    () -> new PlushieItem(ModBlocks.PLUSHIES.get(species).get(), species, new Item.Properties().stacksTo(16))));
        }
    }

    // Crafting materials
    public static final RegistryObject<Item> SOFT_FABRIC = material("soft_fabric", false, Rarity.COMMON);
    public static final RegistryObject<Item> PLUSH_STUFFING = material("plush_stuffing", false, Rarity.COMMON);
    public static final RegistryObject<Item> BUTTON_EYES = material("button_eyes", false, Rarity.COMMON);
    public static final RegistryObject<Item> EMBROIDERY_THREAD = material("embroidery_thread", false, Rarity.COMMON);
    public static final RegistryObject<Item> SATIN_RIBBON = material("satin_ribbon", false, Rarity.COMMON);
    public static final RegistryObject<Item> SUGAR_STAR = material("sugar_star", true, Rarity.UNCOMMON);

    public static final RegistryObject<CollectionBookItem> PLUSHIE_COLLECTION_BOOK = ITEMS.register("plushie_collection_book",
            () -> new CollectionBookItem(new Item.Properties().stacksTo(1)));

    // Decorative blocks
    public static final RegistryObject<BlockItem> PLUSHIE_WORKSHOP = blockItem("plushie_workshop", ModBlocks.PLUSHIE_WORKSHOP);
    public static final RegistryObject<BlockItem> PLUSHIE_SHELF = blockItem("plushie_shelf", ModBlocks.PLUSHIE_SHELF);
    public static final RegistryObject<BlockItem> DISPLAY_STAND = blockItem("display_stand", ModBlocks.DISPLAY_STAND);
    public static final RegistryObject<BlockItem> DISPLAY_CABINET = blockItem("display_cabinet", ModBlocks.DISPLAY_CABINET);

    public static final Map<PastelColor, RegistryObject<BlockItem>> BEDS = new EnumMap<>(PastelColor.class);
    public static final Map<PastelColor, RegistryObject<BlockItem>> BOXES = new EnumMap<>(PastelColor.class);

    static {
        for (PastelColor color : PastelColor.values()) {
            BEDS.put(color, blockItem(color.id() + "_plushie_bed", ModBlocks.BEDS.get(color)));
            BOXES.put(color, blockItem(color.id() + "_plushie_box", ModBlocks.BOXES.get(color)));
        }
    }

    public static final RegistryObject<BlockItem> GOLDEN_COLLECTOR_TROPHY = ITEMS.register("golden_collector_trophy",
            () -> new BlockItem(ModBlocks.GOLDEN_COLLECTOR_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<BlockItem> RAINBOW_COLLECTOR_TROPHY = ITEMS.register("rainbow_collector_trophy",
            () -> new BlockItem(ModBlocks.RAINBOW_COLLECTOR_TROPHY.get(), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    private ModItems() {
    }

    private static RegistryObject<Item> material(String name, boolean foil, Rarity rarity) {
        return ITEMS.register(name, () -> new MaterialItem(new Item.Properties().rarity(rarity), foil));
    }

    private static RegistryObject<BlockItem> blockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
