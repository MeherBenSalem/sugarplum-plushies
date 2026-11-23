package com.sugarplumstudios.plushies.client;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.client.particle.PastelParticle;
import com.sugarplumstudios.plushies.client.render.PlushieBlockEntityRenderer;
import com.sugarplumstudios.plushies.client.render.PlushieDisplayRenderer;
import com.sugarplumstudios.plushies.client.render.PlushieModels;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.registry.ModBlockEntities;
import com.sugarplumstudios.plushies.registry.ModItems;
import com.sugarplumstudios.plushies.registry.ModMenus;
import com.sugarplumstudios.plushies.registry.ModParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SugarplumPlushies.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.PLUSHIE_WORKSHOP.get(), PlushieWorkshopScreen::new);
            ResourceLocation variant = SugarplumPlushies.id("variant");
            for (RegistryObject<PlushieItem> item : ModItems.PLUSHIES.values()) {
                ItemProperties.register(item.get(), variant, (stack, level, entity, seed) -> PlushieItem.getVariant(stack));
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE_DISPLAY.get(), PlushieDisplayRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        PlushieModels.registerAll(event);
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.PASTEL_HEART.get(), sprites -> new PastelParticle.Provider(sprites, PastelParticle.Kind.HEART));
        event.registerSpriteSet(ModParticles.TWINKLE.get(), sprites -> new PastelParticle.Provider(sprites, PastelParticle.Kind.TWINKLE));
        event.registerSpriteSet(ModParticles.SLEEPY_Z.get(), sprites -> new PastelParticle.Provider(sprites, PastelParticle.Kind.SLEEPY));
    }
}
