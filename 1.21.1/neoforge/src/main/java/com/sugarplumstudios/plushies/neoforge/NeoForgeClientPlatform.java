package com.sugarplumstudios.plushies.neoforge;

import com.sugarplumstudios.plushies.client.ClientPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public final class NeoForgeClientPlatform implements ClientPlatform {
    @Override
    public BakedModel getModel(ResourceLocation model) {
        // Added through ModelEvent.RegisterAdditional as standalone models.
        return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(model));
    }
}
