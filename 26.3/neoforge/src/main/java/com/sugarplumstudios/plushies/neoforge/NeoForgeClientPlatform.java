package com.sugarplumstudios.plushies.neoforge;

import com.sugarplumstudios.plushies.client.ClientPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NeoForgeClientPlatform implements ClientPlatform {
    private static final Map<Identifier, StandaloneModelKey<BlockStateModelPart>> KEYS = new ConcurrentHashMap<>();

    static StandaloneModelKey<BlockStateModelPart> key(Identifier model) {
        return KEYS.computeIfAbsent(model, id -> new StandaloneModelKey<>(id::toString));
    }

    @Override
    public BlockStateModelPart getModel(Identifier model) {
        // Added through ModelEvent.RegisterStandalone.
        return Minecraft.getInstance().getModelManager().getStandaloneModel(key(model));
    }
}
