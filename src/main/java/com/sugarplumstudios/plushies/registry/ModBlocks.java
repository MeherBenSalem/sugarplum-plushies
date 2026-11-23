package com.sugarplumstudios.plushies.registry;

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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SugarplumPlushies.MOD_ID);

    public static final Map<PlushieSpecies, RegistryObject<PlushieBlock>> PLUSHIES = new EnumMap<>(PlushieSpecies.class);

    static {
        for (PlushieSpecies species : PlushieSpecies.values()) {
            PLUSHIES.put(species, BLOCKS.register(species.id(), () -> new PlushieBlock(species)));
        }
    }

    public static final RegistryObject<PlushieWorkshopBlock> PLUSHIE_WORKSHOP = BLOCKS.register("plushie_workshop",
            () -> new PlushieWorkshopBlock(wood(MapColor.COLOR_PINK).strength(2.5F).noOcclusion()));

    public static final RegistryObject<PlushieShelfBlock> PLUSHIE_SHELF = BLOCKS.register("plushie_shelf",
            () -> new PlushieShelfBlock(wood(MapColor.COLOR_PINK).strength(1.5F).noOcclusion()));

    public static final RegistryObject<DisplayStandBlock> DISPLAY_STAND = BLOCKS.register("display_stand",
            () -> new DisplayStandBlock(wood(MapColor.QUARTZ).strength(1.5F).noOcclusion()));

    public static final RegistryObject<DisplayCabinetBlock> DISPLAY_CABINET = BLOCKS.register("display_cabinet",
            () -> new DisplayCabinetBlock(wood(MapColor.COLOR_PINK).strength(1.8F).noOcclusion()
                    .isViewBlocking((state, level, pos) -> false).isSuffocating((state, level, pos) -> false)));

    public static final Map<PastelColor, RegistryObject<PlushieBedBlock>> BEDS = new EnumMap<>(PastelColor.class);
    public static final Map<PastelColor, RegistryObject<PlushieBoxBlock>> BOXES = new EnumMap<>(PastelColor.class);

    static {
        for (PastelColor color : PastelColor.values()) {
            BEDS.put(color, BLOCKS.register(color.id() + "_plushie_bed", () -> new PlushieBedBlock(color,
                    BlockBehaviour.Properties.of().mapColor(color.mapColor()).strength(0.6F).sound(SoundType.WOOL)
                            .noOcclusion().ignitedByLava())));
            BOXES.put(color, BLOCKS.register(color.id() + "_plushie_box", () -> new PlushieBoxBlock(color,
                    BlockBehaviour.Properties.of().mapColor(color.mapColor()).strength(0.8F).sound(SoundType.WOOL)
                            .noOcclusion().ignitedByLava())));
        }
    }

    public static final RegistryObject<CollectorTrophyBlock> GOLDEN_COLLECTOR_TROPHY = BLOCKS.register("golden_collector_trophy",
            () -> new CollectorTrophyBlock(trophy(MapColor.GOLD).lightLevel(state -> 6)));

    public static final RegistryObject<CollectorTrophyBlock> RAINBOW_COLLECTOR_TROPHY = BLOCKS.register("rainbow_collector_trophy",
            () -> new CollectorTrophyBlock(trophy(MapColor.COLOR_MAGENTA).lightLevel(state -> 10)));

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties wood(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).ignitedByLava();
    }

    private static BlockBehaviour.Properties trophy(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).sound(SoundType.METAL).strength(2.0F).noOcclusion();
    }
}
