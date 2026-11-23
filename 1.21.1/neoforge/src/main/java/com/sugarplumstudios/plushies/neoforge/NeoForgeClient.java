package com.sugarplumstudios.plushies.neoforge;

import com.sugarplumstudios.plushies.client.PlushiesClient;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.registry.ModMenus;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** Client setup, called from the mod constructor on the physical client only. */
final class NeoForgeClient {
    private NeoForgeClient() {
    }

    static void init(IEventBus modBus) {
        PlushiesClient.init();
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() ->
                PlushiesClient.registerItemProperties(ItemProperties::register)));
        modBus.addListener((RegisterMenuScreensEvent event) ->
                event.register(ModMenus.PLUSHIE_WORKSHOP.get(), PlushieWorkshopScreen::new));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
                PlushiesClient.registerRenderers(event::registerBlockEntityRenderer));
        modBus.addListener((ModelEvent.RegisterAdditional event) ->
                PlushiesClient.registerModels(id -> event.register(ModelResourceLocation.standalone(id))));
        modBus.addListener((RegisterParticleProvidersEvent event) ->
                PlushiesClient.registerParticles((type, factory) -> event.registerSpriteSet(type, factory::create)));
    }
}
