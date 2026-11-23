package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.collection.CollectionEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: claim a collection milestone reward. Validated server-side. */
public record ClaimRewardPacket(int milestone) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(milestone);
    }

    public static ClaimRewardPacket decode(FriendlyByteBuf buffer) {
        return new ClaimRewardPacket(buffer.readVarInt());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            CollectionEvents.claim(player, milestone);
        }
    }
}
