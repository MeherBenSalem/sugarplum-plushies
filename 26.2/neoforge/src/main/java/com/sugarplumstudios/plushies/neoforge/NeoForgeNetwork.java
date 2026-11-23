package com.sugarplumstudios.plushies.neoforge;

import com.sugarplumstudios.plushies.network.ClientboundMessage;
import com.sugarplumstudios.plushies.network.MessagePayload;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.network.ServerboundMessage;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

final class NeoForgeNetwork {
    private NeoForgeNetwork() {
    }

    static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ModNetwork.PROTOCOL);
        // Handlers run on the main thread of the receiving side.
        for (ModNetwork.Type<?> type : ModNetwork.CLIENTBOUND) {
            registrar.playToClient(MessagePayload.typeFor(type), MessagePayload.codec(type),
                    (payload, context) -> ((ClientboundMessage) payload.message()).handleOnClient());
        }
        for (ModNetwork.Type<?> type : ModNetwork.SERVERBOUND) {
            registrar.playToServer(MessagePayload.typeFor(type), MessagePayload.codec(type), (payload, context) -> {
                if (context.player() instanceof ServerPlayer player) {
                    ((ServerboundMessage) payload.message()).handleOnServer(player);
                }
            });
        }
    }
}
