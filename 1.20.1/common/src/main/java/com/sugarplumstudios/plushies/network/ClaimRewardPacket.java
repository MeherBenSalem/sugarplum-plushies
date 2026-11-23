package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.collection.CollectionEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/** Client -> server: claim a collection milestone reward. Validated server-side. */
public record ClaimRewardPacket(int milestone) implements ServerboundMessage {
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(milestone);
    }

    public static ClaimRewardPacket decode(FriendlyByteBuf buffer) {
        return new ClaimRewardPacket(buffer.readVarInt());
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        CollectionEvents.claim(player, milestone);
    }
}
