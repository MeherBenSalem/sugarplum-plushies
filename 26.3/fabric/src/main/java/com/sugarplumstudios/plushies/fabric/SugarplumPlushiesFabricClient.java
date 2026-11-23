package com.sugarplumstudios.plushies.fabric;

import com.sugarplumstudios.plushies.client.PlushiesClient;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.network.ClientboundMessage;
import com.sugarplumstudios.plushies.network.MessagePayload;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class SugarplumPlushiesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlushiesClient.init();
        for (ModNetwork.Type<?> type : ModNetwork.CLIENTBOUND) {
            // Fabric calls payload receivers on the client thread.
            ClientPlayNetworking.registerGlobalReceiver(MessagePayload.typeFor(type),
                    (payload, context) -> ((ClientboundMessage) payload.message()).handleOnClient());
        }

        MenuScreens.register(ModMenus.PLUSHIE_WORKSHOP.get(), PlushieWorkshopScreen::new);
        PlushiesClient.registerRenderers(BlockEntityRenderers::register);
        PlushiesClient.registerParticles((type, factory) -> ParticleProviderRegistry.getInstance().register(type, factory::create));
        List<Identifier> models = new ArrayList<>();
        PlushiesClient.registerModels(models::add);
        ModelLoadingPlugin.register(context -> models.forEach(model ->
                context.addModel(FabricClientPlatform.key(model), FabricClientPlatform.unbaked(model))));
        // Block render layers come from each texture's transparency on this Minecraft version.
    }
}
