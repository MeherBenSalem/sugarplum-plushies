package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.client.ClientAccess;
import net.minecraft.network.FriendlyByteBuf;

/** Server -> client: a plushie variant was just added to the collection (shows a toast). */
public record PlushieUnlockedPacket(int species, int variant) implements ClientboundMessage {
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(species);
        buffer.writeVarInt(variant);
    }

    public static PlushieUnlockedPacket decode(FriendlyByteBuf buffer) {
        return new PlushieUnlockedPacket(buffer.readVarInt(), buffer.readVarInt());
    }

    @Override
    public void handleOnClient() {
        ClientAccess.handleUnlock(species, variant);
    }
}
