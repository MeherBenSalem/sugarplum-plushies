package com.sugarplumstudios.plushies.fabric;

import com.sugarplumstudios.plushies.client.PlushiesClient;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.network.ClientboundMessage;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import com.sugarplumstudios.plushies.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class SugarplumPlushiesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlushiesClient.init();
        for (ModNetwork.Type<? extends ClientboundMessage> type : ModNetwork.CLIENTBOUND) receive(type);

        MenuScreens.register(ModMenus.PLUSHIE_WORKSHOP.get(), PlushieWorkshopScreen::new);
        PlushiesClient.registerItemProperties(ItemProperties::register);
        PlushiesClient.registerRenderers(BlockEntityRenderers::register);
        PlushiesClient.registerParticles((type, factory) -> ParticleFactoryRegistry.getInstance().register(type, factory::create));
        List<ResourceLocation> models = new ArrayList<>();
        PlushiesClient.registerModels(models::add);
        ModelLoadingPlugin.register(context -> context.addModels(models));

        // Forge reads each model's render_type; on Fabric the layer is set per block (the cabinet has glass).
        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals(com.sugarplumstudios.plushies.SugarplumPlushies.MOD_ID)) continue;
            BlockRenderLayerMap.INSTANCE.putBlock(block, block == ModBlocks.DISPLAY_CABINET.get() ? RenderType.translucent() : RenderType.cutout());
        }
    }

    private static <M extends ClientboundMessage> void receive(ModNetwork.Type<M> type) {
        // Fabric calls receivers on the network thread: decode there, handle on the client thread.
        ClientPlayNetworking.registerGlobalReceiver(type.id(), (client, handler, buf, sender) -> {
            M message = type.decoder().apply(buf);
            client.execute(message::handleOnClient);
        });
    }
}
