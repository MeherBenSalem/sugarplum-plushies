package com.sugarplumstudios.plushies.client;

import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.resources.Identifier;

/** Client-only loader services. */
public interface ClientPlatform {
    ClientPlatform INSTANCE = Services.load(ClientPlatform.class);

    /** A model registered with {@link PlushiesClient#registerModels}, baked without rotation. */
    BlockStateModelPart getModel(Identifier model);
}
