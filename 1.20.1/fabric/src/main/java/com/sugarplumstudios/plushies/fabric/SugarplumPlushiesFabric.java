package com.sugarplumstudios.plushies.fabric;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.collection.CollectionEvents;
import com.sugarplumstudios.plushies.event.ModEvents;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.network.ServerboundMessage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public final class SugarplumPlushiesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SugarplumPlushies.init();
        for (ModNetwork.Type<? extends ServerboundMessage> type : ModNetwork.SERVERBOUND) receive(type);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) CollectionEvents.onPlayerTick(player);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CollectionEvents.onJoin(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> CollectionEvents.onJoin(newPlayer));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> CollectionEvents.onJoin(player));

        // Fabric's version of Forge's "use the block even while sneaking" for brushes and empty hands on plushies.
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator() || !ModEvents.allowSneakUse(player, level, hit.getBlockPos(), hand, player.getItemInHand(hand))) {
                return InteractionResult.PASS;
            }
            return level.getBlockState(hit.getBlockPos()).use(level, player, hand, hit);
        });

        TradeOfferHelper.registerWanderingTraderOffers(1, offers -> offers.addAll(ModEvents.genericTrades()));
        TradeOfferHelper.registerWanderingTraderOffers(2, offers -> offers.addAll(ModEvents.rareTrades()));
    }

    private static <M extends ServerboundMessage> void receive(ModNetwork.Type<M> type) {
        // Fabric calls receivers on the network thread: decode there, handle on the server thread.
        ServerPlayNetworking.registerGlobalReceiver(type.id(), (server, player, handler, buf, sender) -> {
            M message = type.decoder().apply(buf);
            server.execute(() -> message.handleOnServer(player));
        });
    }
}
