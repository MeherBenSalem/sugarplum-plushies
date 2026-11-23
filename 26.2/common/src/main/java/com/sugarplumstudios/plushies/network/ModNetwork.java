package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Function;

/** Sugarplum Plushies' messages. Each loader registers them with its own networking API. */
public final class ModNetwork {
    public static final String PROTOCOL = "1";

    public record Type<M extends Message>(Identifier id, Class<M> type, Function<FriendlyByteBuf, M> decoder) {
    }

    /** Server -> client. */
    public static final List<Type<? extends ClientboundMessage>> CLIENTBOUND = List.of(
            new Type<>(SugarplumPlushies.id("sync_collection"), SyncCollectionPacket.class, SyncCollectionPacket::decode),
            new Type<>(SugarplumPlushies.id("plushie_unlocked"), PlushieUnlockedPacket.class, PlushieUnlockedPacket::decode),
            new Type<>(SugarplumPlushies.id("workshop_recipes"), WorkshopRecipesPacket.class, WorkshopRecipesPacket::decode));

    /** Client -> server. */
    public static final List<Type<? extends ServerboundMessage>> SERVERBOUND = List.of(
            new Type<>(SugarplumPlushies.id("claim_reward"), ClaimRewardPacket.class, ClaimRewardPacket::decode));

    private ModNetwork() {
    }

    public static Type<?> typeOf(Object message) {
        for (Type<?> type : CLIENTBOUND) {
            if (type.type() == message.getClass()) return type;
        }
        for (Type<?> type : SERVERBOUND) {
            if (type.type() == message.getClass()) return type;
        }
        throw new IllegalArgumentException("Unregistered message " + message.getClass());
    }

    public static void sendTo(ServerPlayer player, ClientboundMessage message) {
        // Fake players (automation mods, GameTest mocks) have no connection; the platforms check the rest.
        if (player.connection == null) return;
        Services.PLATFORM.sendToPlayer(player, message);
    }

    public static void sendToServer(ServerboundMessage message) {
        Services.PLATFORM.sendToServer(message);
    }
}
