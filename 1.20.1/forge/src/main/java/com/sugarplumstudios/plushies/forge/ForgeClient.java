package com.sugarplumstudios.plushies.forge;

import com.sugarplumstudios.plushies.client.PlushiesClient;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Client setup, called from the mod constructor on the physical client only. */
final class ForgeClient {
    private ForgeClient() {
    }

    static void init(IEventBus modBus) {
        PlushiesClient.init();
        // Forge 1.20.1 has no menu screen event: the screen and item properties are registered during client setup.
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.PLUSHIE_WORKSHOP.get(), PlushieWorkshopScreen::new);
            PlushiesClient.registerItemProperties(ItemProperties::register);
        }));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
                PlushiesClient.registerRenderers(event::registerBlockEntityRenderer));
        modBus.addListener((ModelEvent.RegisterAdditional event) -> PlushiesClient.registerModels(event::register));
        modBus.addListener((RegisterParticleProvidersEvent event) ->
                PlushiesClient.registerParticles((type, factory) -> event.registerSpriteSet(type, factory::create)));
    }
}
