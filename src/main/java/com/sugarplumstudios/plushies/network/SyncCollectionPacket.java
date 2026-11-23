package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.client.ClientAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server → client: the player's full collection state. */
public record SyncCollectionPacket(int[] masks, int claimed) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(masks);
        buffer.writeVarInt(claimed);
    }

    public static SyncCollectionPacket decode(FriendlyByteBuf buffer) {
        return new SyncCollectionPacket(buffer.readVarIntArray(), buffer.readVarInt());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAccess.handleSync(masks, claimed));
    }
}
