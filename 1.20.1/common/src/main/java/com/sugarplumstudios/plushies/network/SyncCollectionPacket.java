package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.client.ClientAccess;
import net.minecraft.network.FriendlyByteBuf;

/** Server -> client: the player's full collection state. */
public record SyncCollectionPacket(int[] masks, int claimed) implements ClientboundMessage {
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(masks);
        buffer.writeVarInt(claimed);
    }

    public static SyncCollectionPacket decode(FriendlyByteBuf buffer) {
        return new SyncCollectionPacket(buffer.readVarIntArray(), buffer.readVarInt());
    }

    @Override
    public void handleOnClient() {
        ClientAccess.handleSync(masks, claimed);
    }
}
