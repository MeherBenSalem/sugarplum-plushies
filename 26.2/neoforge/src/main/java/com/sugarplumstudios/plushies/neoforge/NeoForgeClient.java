package com.sugarplumstudios.plushies.neoforge;

import com.sugarplumstudios.plushies.client.PlushiesClient;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;

/** Client setup, called from the mod constructor on the physical client only. */
final class NeoForgeClient {
    private NeoForgeClient() {
    }

    static void init(IEventBus modBus) {
        PlushiesClient.init();
        modBus.addListener((RegisterMenuScreensEvent event) ->
                event.register(ModMenus.PLUSHIE_WORKSHOP.get(), PlushieWorkshopScreen::new));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
                PlushiesClient.registerRenderers(event::registerBlockEntityRenderer));
        modBus.addListener((ModelEvent.RegisterStandalone event) ->
                PlushiesClient.registerModels(id -> event.register(NeoForgeClientPlatform.key(id),
                        SimpleUnbakedStandaloneModel.simpleModelWrapper(id))));
        modBus.addListener((RegisterParticleProvidersEvent event) ->
                PlushiesClient.registerParticles((type, factory) -> event.registerSpriteSet(type, factory::create)));
    }
}
