package com.sugarplumstudios.plushies.forge;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.network.ClientboundMessage;
import com.sugarplumstudios.plushies.network.ServerboundMessage;
import com.sugarplumstudios.plushies.platform.IPlatform;
import com.sugarplumstudios.plushies.platform.RegistryEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class ForgePlatform implements IPlatform {
    private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();

    /** Hands every deferred register to the mod event bus. Call after {@link SugarplumPlushies#init()}. */
    public static void registerAll(IEventBus modBus) {
        REGISTERS.values().forEach(register -> register.register(modBus));
    }

    @Override
    public String name() {
        return "Forge";
    }

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends R> RegistryEntry<T> register(ResourceKey<? extends Registry<R>> registry, String name, Supplier<T> factory) {
        DeferredRegister<R> register = (DeferredRegister<R>) REGISTERS.computeIfAbsent(registry,
                key -> DeferredRegister.create(registry, SugarplumPlushies.MOD_ID));
        RegistryObject<T> object = register.register(name, factory);
        return new RegistryEntry<>(object.getId(), object);
    }

    @Override
    public CreativeModeTab.Builder creativeTab() {
        return CreativeModeTab.builder();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ClientboundMessage message) {
        if (player instanceof FakePlayer) return;
        ForgeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public void sendToServer(ServerboundMessage message) {
        ForgeNetwork.CHANNEL.sendToServer(message);
    }
}
