package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            SugarplumPlushies.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(SyncCollectionPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncCollectionPacket::encode)
                .decoder(SyncCollectionPacket::decode)
                .consumerMainThread(SyncCollectionPacket::handle)
                .add();
        CHANNEL.messageBuilder(PlushieUnlockedPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PlushieUnlockedPacket::encode)
                .decoder(PlushieUnlockedPacket::decode)
                .consumerMainThread(PlushieUnlockedPacket::handle)
                .add();
        CHANNEL.messageBuilder(ClaimRewardPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClaimRewardPacket::encode)
                .decoder(ClaimRewardPacket::decode)
                .consumerMainThread(ClaimRewardPacket::handle)
                .add();
    }

    public static void sendTo(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }
}
