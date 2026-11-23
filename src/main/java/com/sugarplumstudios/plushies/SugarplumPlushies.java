package com.sugarplumstudios.plushies;

import com.mojang.logging.LogUtils;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.registry.ModBlockEntities;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import com.sugarplumstudios.plushies.registry.ModCreativeTabs;
import com.sugarplumstudios.plushies.registry.ModItems;
import com.sugarplumstudios.plushies.registry.ModMenus;
import com.sugarplumstudios.plushies.registry.ModParticles;
import com.sugarplumstudios.plushies.registry.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SugarplumPlushies.MOD_ID)
public class SugarplumPlushies {
    public static final String MOD_ID = "sugarplumplushies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SugarplumPlushies() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.TYPES.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        ModNetwork.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PlushieConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PlushieConfig.CLIENT_SPEC);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
