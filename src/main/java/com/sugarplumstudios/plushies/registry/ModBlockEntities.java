package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.entity.PlushieBlockEntity;
import com.sugarplumstudios.plushies.block.entity.PlushieBoxBlockEntity;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SugarplumPlushies.MOD_ID);

    public static final RegistryObject<BlockEntityType<PlushieBlockEntity>> PLUSHIE = BLOCK_ENTITIES.register("plushie",
            () -> BlockEntityType.Builder.of(PlushieBlockEntity::new,
                    ModBlocks.PLUSHIES.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

    public static final RegistryObject<BlockEntityType<PlushieDisplayBlockEntity>> PLUSHIE_DISPLAY = BLOCK_ENTITIES.register("plushie_display",
            () -> BlockEntityType.Builder.of(PlushieDisplayBlockEntity::new, displayBlocks()).build(null));

    public static final RegistryObject<BlockEntityType<PlushieBoxBlockEntity>> PLUSHIE_BOX = BLOCK_ENTITIES.register("plushie_box",
            () -> BlockEntityType.Builder.of(PlushieBoxBlockEntity::new,
                    ModBlocks.BOXES.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

    private ModBlockEntities() {
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
