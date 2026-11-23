package com.sugarplumstudios.plushies.registry;

import net.minecraft.resources.ResourceKey;
import com.sugarplumstudios.plushies.platform.RegistryEntry;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.core.registries.Registries;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.CollectorTrophyBlock;
import com.sugarplumstudios.plushies.block.DisplayCabinetBlock;
import com.sugarplumstudios.plushies.block.DisplayStandBlock;
import com.sugarplumstudios.plushies.block.PastelColor;
import com.sugarplumstudios.plushies.block.PlushieBedBlock;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.PlushieBoxBlock;
import com.sugarplumstudios.plushies.block.PlushieShelfBlock;
import com.sugarplumstudios.plushies.block.PlushieWorkshopBlock;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.EnumMap;
import java.util.Map;

public final class ModBlocks {

    public static final Map<PlushieSpecies, RegistryEntry<PlushieBlock>> PLUSHIES = new EnumMap<>(PlushieSpecies.class);

    static {
        for (PlushieSpecies species : PlushieSpecies.values()) {
            PLUSHIES.put(species, Services.PLATFORM.register(Registries.BLOCK, species.id(), () -> new PlushieBlock(species)));
        }
    }

    public static final RegistryEntry<PlushieWorkshopBlock> PLUSHIE_WORKSHOP = Services.PLATFORM.register(Registries.BLOCK, "plushie_workshop",
            () -> new PlushieWorkshopBlock(wood("plushie_workshop", MapColor.COLOR_PINK).strength(2.5F).noOcclusion()));

    public static final RegistryEntry<PlushieShelfBlock> PLUSHIE_SHELF = Services.PLATFORM.register(Registries.BLOCK, "plushie_shelf",
            () -> new PlushieShelfBlock(wood("plushie_shelf", MapColor.COLOR_PINK).strength(1.5F).noOcclusion()));

    public static final RegistryEntry<DisplayStandBlock> DISPLAY_STAND = Services.PLATFORM.register(Registries.BLOCK, "display_stand",
            () -> new DisplayStandBlock(wood("display_stand", MapColor.QUARTZ).strength(1.5F).noOcclusion()));

    public static final RegistryEntry<DisplayCabinetBlock> DISPLAY_CABINET = Services.PLATFORM.register(Registries.BLOCK, "display_cabinet",
            () -> new DisplayCabinetBlock(wood("display_cabinet", MapColor.COLOR_PINK).strength(1.8F).noOcclusion()
                    .isViewBlocking((state, level, pos, box) -> false).isSuffocating((state, level, pos) -> false)));

    public static final Map<PastelColor, RegistryEntry<PlushieBedBlock>> BEDS = new EnumMap<>(PastelColor.class);
    public static final Map<PastelColor, RegistryEntry<PlushieBoxBlock>> BOXES = new EnumMap<>(PastelColor.class);

    static {
        for (PastelColor color : PastelColor.values()) {
            BEDS.put(color, Services.PLATFORM.register(Registries.BLOCK, color.id() + "_plushie_bed", () -> new PlushieBedBlock(color,
                    props(color.id() + "_plushie_bed").mapColor(color.mapColor()).strength(0.6F).sound(SoundType.WOOL)
                            .noOcclusion().ignitedByLava())));
            BOXES.put(color, Services.PLATFORM.register(Registries.BLOCK, color.id() + "_plushie_box", () -> new PlushieBoxBlock(color,
                    props(color.id() + "_plushie_box").mapColor(color.mapColor()).strength(0.8F).sound(SoundType.WOOL)
                            .noOcclusion().ignitedByLava())));
        }
    }

    public static final RegistryEntry<CollectorTrophyBlock> GOLDEN_COLLECTOR_TROPHY = Services.PLATFORM.register(Registries.BLOCK, "golden_collector_trophy",
            () -> new CollectorTrophyBlock(trophy("golden_collector_trophy", MapColor.GOLD).lightLevel(state -> 6)));

    public static final RegistryEntry<CollectorTrophyBlock> RAINBOW_COLLECTOR_TROPHY = Services.PLATFORM.register(Registries.BLOCK, "rainbow_collector_trophy",
            () -> new CollectorTrophyBlock(trophy("rainbow_collector_trophy", MapColor.COLOR_MAGENTA).lightLevel(state -> 10)));

    private ModBlocks() {
    }

    /** Loads the class, which registers everything above. */
    public static void init() {
    }

    /** Block properties carrying the block's registry id (required since 1.21.2). */
    public static BlockBehaviour.Properties props(String name) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, SugarplumPlushies.id(name)));
    }

    private static BlockBehaviour.Properties wood(String name, MapColor color) {
        return props(name).mapColor(color).sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).ignitedByLava();
    }

    private static BlockBehaviour.Properties trophy(String name, MapColor color) {
        return props(name).mapColor(color).sound(SoundType.METAL).strength(2.0F).noOcclusion();
    }
}
