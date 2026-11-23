package com.sugarplumstudios.plushies.network;

import net.minecraft.network.FriendlyByteBuf;

/** Anything sent over the mod's channel. */
public interface Message {
    void write(FriendlyByteBuf buf);
}
