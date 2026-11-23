package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.platform.RegistryEntry;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.core.registries.Registries;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.entity.PlushieBlockEntity;
import com.sugarplumstudios.plushies.block.entity.PlushieBoxBlockEntity;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public final class ModBlockEntities {

    public static final RegistryEntry<BlockEntityType<PlushieBlockEntity>> PLUSHIE = Services.PLATFORM.register(Registries.BLOCK_ENTITY_TYPE, "plushie",
            () -> Services.PLATFORM.blockEntityType(PlushieBlockEntity::new,
                    ModBlocks.PLUSHIES.values().stream().map(RegistryEntry::get).toArray(Block[]::new)));

    public static final RegistryEntry<BlockEntityType<PlushieDisplayBlockEntity>> PLUSHIE_DISPLAY = Services.PLATFORM.register(Registries.BLOCK_ENTITY_TYPE, "plushie_display",
            () -> Services.PLATFORM.blockEntityType(PlushieDisplayBlockEntity::new, displayBlocks()));

    public static final RegistryEntry<BlockEntityType<PlushieBoxBlockEntity>> PLUSHIE_BOX = Services.PLATFORM.register(Registries.BLOCK_ENTITY_TYPE, "plushie_box",
            () -> Services.PLATFORM.blockEntityType(PlushieBoxBlockEntity::new,
                    ModBlocks.BOXES.values().stream().map(RegistryEntry::get).toArray(Block[]::new)));

    private ModBlockEntities() {
    }

    /** Loads the class, which registers everything above. */
    public static void init() {
    }

    private static Block[] displayBlocks() {
        List<Block> blocks = new ArrayList<>();
        blocks.add(ModBlocks.PLUSHIE_SHELF.get());
        blocks.add(ModBlocks.DISPLAY_STAND.get());
        blocks.add(ModBlocks.DISPLAY_CABINET.get());
        ModBlocks.BEDS.values().forEach(bed -> blocks.add(bed.get()));
        return blocks.toArray(Block[]::new);
    }
}
