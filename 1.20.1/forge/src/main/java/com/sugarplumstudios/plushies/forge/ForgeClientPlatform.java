package com.sugarplumstudios.plushies.forge;

import com.sugarplumstudios.plushies.client.ClientPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public final class ForgeClientPlatform implements ClientPlatform {
    @Override
    public BakedModel getModel(ResourceLocation model) {
        // Added through ModelEvent.RegisterAdditional; Forge looks extra models up by their plain id.
        return Minecraft.getInstance().getModelManager().getModel(model);
    }
}
