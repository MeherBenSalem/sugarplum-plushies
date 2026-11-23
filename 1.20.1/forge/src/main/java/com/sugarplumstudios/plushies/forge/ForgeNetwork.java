package com.sugarplumstudios.plushies.forge;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.network.ClientboundMessage;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.network.ServerboundMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** The mod's Forge channel, unchanged from 1.0 (sugarplumplushies:main, protocol 1, same message ids). */
final class ForgeNetwork {
    static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            SugarplumPlushies.id("main"), () -> ModNetwork.PROTOCOL, ModNetwork.PROTOCOL::equals, ModNetwork.PROTOCOL::equals);

    private ForgeNetwork() {
    }

    static void register() {
        int id = 0;
        for (ModNetwork.Type<? extends ClientboundMessage> type : ModNetwork.CLIENTBOUND) clientbound(type, id++);
        for (ModNetwork.Type<? extends ServerboundMessage> type : ModNetwork.SERVERBOUND) serverbound(type, id++);
    }

    private static <M extends ClientboundMessage> void clientbound(ModNetwork.Type<M> type, int id) {
        CHANNEL.messageBuilder(type.type(), id, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundMessage::write)
                .decoder(type.decoder())
                .consumerMainThread((message, context) -> {
                    message.handleOnClient();
                    context.get().setPacketHandled(true);
                })
                .add();
    }

    private static <M extends ServerboundMessage> void serverbound(ModNetwork.Type<M> type, int id) {
        CHANNEL.messageBuilder(type.type(), id, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundMessage::write)
                .decoder(type.decoder())
                .consumerMainThread((message, context) -> {
                    ServerPlayer player = context.get().getSender();
                    if (player != null) message.handleOnServer(player);
                    context.get().setPacketHandled(true);
                })
                .add();
    }
}
