package com.sugarplumstudios.plushies.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/** A client -> server message. Handled on the server's main thread. */
public interface ServerboundMessage {
    void write(FriendlyByteBuf buf);

    void handleOnServer(ServerPlayer player);
}
