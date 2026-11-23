package com.sugarplumstudios.plushies.network;

import net.minecraft.server.level.ServerPlayer;

/** A client -> server message. Handled on the server's main thread. */
public interface ServerboundMessage extends Message {

    void handleOnServer(ServerPlayer player);
}
