package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.PastelColor;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SugarplumPlushies.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sugarplumplushies"))
            .icon(() -> PlushieItem.create(PlushieSpecies.PINK_TEDDY_BEAR, 0))
            .displayItems((params, output) -> {
                output.accept(ModItems.PLUSHIE_WORKSHOP.get());
                output.accept(ModItems.PLUSHIE_COLLECTION_BOOK.get());
                output.accept(ModItems.SOFT_FABRIC.get());
                output.accept(ModItems.PLUSH_STUFFING.get());
                output.accept(ModItems.BUTTON_EYES.get());
                output.accept(ModItems.EMBROIDERY_THREAD.get());
                output.accept(ModItems.SATIN_RIBBON.get());
                output.accept(ModItems.SUGAR_STAR.get());
                output.accept(ModItems.PLUSHIE_SHELF.get());
                output.accept(ModItems.DISPLAY_STAND.get());
                output.accept(ModItems.DISPLAY_CABINET.get());
                for (PastelColor color : PastelColor.values()) {
                    output.accept(ModItems.BEDS.get(color).get());
                }
                for (PastelColor color : PastelColor.values()) {
                    output.accept(ModItems.BOXES.get(color).get());
                }
                output.accept(ModItems.GOLDEN_COLLECTOR_TROPHY.get());
                output.accept(ModItems.RAINBOW_COLLECTOR_TROPHY.get());
                for (PlushieSpecies species : PlushieSpecies.values()) {
                    for (int variant = 0; variant < PlushieSpecies.VARIANT_COUNT; variant++) {
                        output.accept(PlushieItem.create(species, variant));
                    }
                }
            })
            .build());

    private ModCreativeTabs() {
    }
}
