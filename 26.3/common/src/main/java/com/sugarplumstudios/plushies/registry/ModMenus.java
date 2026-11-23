package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.platform.RegistryEntry;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.core.registries.Registries;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.menu.PlushieWorkshopMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {

    public static final RegistryEntry<MenuType<PlushieWorkshopMenu>> PLUSHIE_WORKSHOP = Services.PLATFORM.register(Registries.MENU, "plushie_workshop",
            () -> new MenuType<>(PlushieWorkshopMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {
    }

    /** Loads the class, which registers everything above. */
    public static void init() {
    }
}
