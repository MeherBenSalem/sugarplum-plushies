package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.menu.PlushieWorkshopMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SugarplumPlushies.MOD_ID);

    public static final RegistryObject<MenuType<PlushieWorkshopMenu>> PLUSHIE_WORKSHOP = MENUS.register("plushie_workshop",
            () -> new MenuType<>(PlushieWorkshopMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {
    }
}
