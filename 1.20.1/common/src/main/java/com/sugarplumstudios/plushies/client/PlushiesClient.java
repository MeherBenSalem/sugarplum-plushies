package com.sugarplumstudios.plushies.client;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.client.particle.PastelParticle;
import com.sugarplumstudios.plushies.client.render.PlushieBlockEntityRenderer;
import com.sugarplumstudios.plushies.client.render.PlushieDisplayRenderer;
import com.sugarplumstudios.plushies.client.render.PlushieModels;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.registry.ModBlockEntities;
import com.sugarplumstudios.plushies.registry.ModItems;
import com.sugarplumstudios.plushies.registry.ModParticles;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

/**
 * Loader-neutral client setup. Each loader calls these from its own client events; the workshop screen is
 * registered by the loaders themselves.
 */
public final class PlushiesClient {
    private PlushiesClient() {
    }

    /** Reads the client config. */
    public static void init() {
        PlushieConfig.loadClient();
    }

    public interface RendererSink {
        <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider);
    }

    public static void registerRenderers(RendererSink sink) {
        sink.register(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        sink.register(ModBlockEntities.PLUSHIE_DISPLAY.get(), PlushieDisplayRenderer::new);
    }

    /** The body and part models block entity renderers draw (not referenced by any block state). */
    public static void registerModels(Consumer<ResourceLocation> sink) {
        PlushieModels.registerAll(sink);
    }

    public interface ParticleSink {
        void register(SimpleParticleType type, ParticleProviderFactory factory);
    }

    public interface ParticleProviderFactory {
        ParticleProvider<SimpleParticleType> create(SpriteSet sprites);
    }

    public static void registerParticles(ParticleSink sink) {
        sink.register(ModParticles.PASTEL_HEART.get(), sprites -> new PastelParticle.Provider(sprites, PastelParticle.Kind.HEART));
        sink.register(ModParticles.TWINKLE.get(), sprites -> new PastelParticle.Provider(sprites, PastelParticle.Kind.TWINKLE));
        sink.register(ModParticles.SLEEPY_Z.get(), sprites -> new PastelParticle.Provider(sprites, PastelParticle.Kind.SLEEPY));
    }

    public interface ItemPropertySink {
        void register(Item item, ResourceLocation id, ClampedItemPropertyFunction function);
    }

    /** The plushie items pick their model from the "variant" property. */
    public static void registerItemProperties(ItemPropertySink sink) {
        ResourceLocation variant = SugarplumPlushies.id("variant");
        ModItems.PLUSHIES.values().forEach(item ->
                sink.register(item.get(), variant, (stack, level, entity, seed) -> PlushieItem.getVariant(stack)));
    }
}
