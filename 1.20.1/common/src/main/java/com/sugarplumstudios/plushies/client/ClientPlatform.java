package com.sugarplumstudios.plushies.client;

import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/** Client-only loader services. */
public interface ClientPlatform {
    ClientPlatform INSTANCE = Services.load(ClientPlatform.class);

    /** A model registered with {@link PlushiesClient#registerModels}. */
    BakedModel getModel(ResourceLocation model);
}
