package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.client.ClientAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server → client: a plushie variant was just added to the collection (shows a toast). */
public record PlushieUnlockedPacket(int species, int variant) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(species);
        buffer.writeVarInt(variant);
    }

    public static PlushieUnlockedPacket decode(FriendlyByteBuf buffer) {
        return new PlushieUnlockedPacket(buffer.readVarInt(), buffer.readVarInt());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAccess.handleUnlock(species, variant));
    }
}
