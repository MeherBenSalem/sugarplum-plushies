package com.sugarplumstudios.plushies;

import com.mojang.logging.LogUtils;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.registry.ModBlockEntities;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import com.sugarplumstudios.plushies.registry.ModCreativeTabs;
import com.sugarplumstudios.plushies.registry.ModItems;
import com.sugarplumstudios.plushies.registry.ModMenus;
import com.sugarplumstudios.plushies.registry.ModParticles;
import com.sugarplumstudios.plushies.registry.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Sugarplum Plushies, a Sugarplum Studios mod. Loader-independent entry point: each loader calls {@link #init()}
 * from its own mod constructor or initializer.
 */
public final class SugarplumPlushies {
    public static final String MOD_ID = "sugarplumplushies";
    public static final Logger LOGGER = LogUtils.getLogger();

    private SugarplumPlushies() {
    }

    /** Reads the config and registers every particle, block, item, block entity, menu, recipe type and creative tab. */
    public static void init() {
        PlushieConfig.loadCommon();
        ModParticles.init();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModRecipes.init();
        ModCreativeTabs.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
