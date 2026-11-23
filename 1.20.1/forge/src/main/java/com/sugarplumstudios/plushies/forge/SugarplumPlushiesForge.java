package com.sugarplumstudios.plushies.forge;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.collection.CollectionEvents;
import com.sugarplumstudios.plushies.event.ModEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SugarplumPlushies.MOD_ID)
public final class SugarplumPlushiesForge {
    public SugarplumPlushiesForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SugarplumPlushies.init();
        ForgePlatform.registerAll(modBus);
        ForgeNetwork.register();

        MinecraftForge.EVENT_BUS.addListener(SugarplumPlushiesForge::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(SugarplumPlushiesForge::onLogin);
        MinecraftForge.EVENT_BUS.addListener(SugarplumPlushiesForge::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(SugarplumPlushiesForge::onChangeDimension);
        MinecraftForge.EVENT_BUS.addListener(SugarplumPlushiesForge::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(SugarplumPlushiesForge::onWandererTrades);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClient.init(modBus);
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
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
            event.setUseBlock(Event.Result.ALLOW);
        }
    }

    private static void onWandererTrades(WandererTradesEvent event) {
        event.getGenericTrades().addAll(ModEvents.genericTrades());
        event.getRareTrades().addAll(ModEvents.rareTrades());
    }
}
