package com.sugarplumstudios.plushies.neoforge;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.collection.CollectionEvents;
import com.sugarplumstudios.plushies.event.ModEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

@Mod(SugarplumPlushies.MOD_ID)
public final class SugarplumPlushiesNeoForge {
    public SugarplumPlushiesNeoForge(IEventBus modBus) {
        SugarplumPlushies.init();
        NeoForgePlatform.registerAll(modBus);
        modBus.addListener(NeoForgeNetwork::register);

        NeoForge.EVENT_BUS.addListener(SugarplumPlushiesNeoForge::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SugarplumPlushiesNeoForge::onLogin);
        NeoForge.EVENT_BUS.addListener(SugarplumPlushiesNeoForge::onRespawn);
        NeoForge.EVENT_BUS.addListener(SugarplumPlushiesNeoForge::onChangeDimension);
        NeoForge.EVENT_BUS.addListener(SugarplumPlushiesNeoForge::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(SugarplumPlushiesNeoForge::onWandererTrades);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeClient.init(modBus);
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CollectionEvents.onPlayerTick(player);
        }
    }

    private static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) CollectionEvents.onJoin(player);
    }

    private static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) CollectionEvents.onJoin(player);
    }

    private static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) CollectionEvents.onJoin(player);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (ModEvents.allowSneakUse(event.getEntity(), event.getLevel(), event.getPos(), event.getHand(), event.getItemStack())) {
            event.setUseBlock(TriState.TRUE);
        }
    }

    private static void onWandererTrades(WandererTradesEvent event) {
        event.getGenericTrades().addAll(ModEvents.genericTrades());
        event.getRareTrades().addAll(ModEvents.rareTrades());
    }
}
