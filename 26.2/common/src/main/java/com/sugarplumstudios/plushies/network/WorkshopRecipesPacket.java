package com.sugarplumstudios.plushies.network;

import com.sugarplumstudios.plushies.recipe.WorkshopRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Server -> client: the workshop patterns, in the server's order. Clients no longer receive
 * recipes, so the server sends them right before it opens a Plushie Workshop.
 */
public record WorkshopRecipesPacket(List<WorkshopRecipe> recipes) implements ClientboundMessage {
    private static final StreamCodec<RegistryFriendlyByteBuf, List<WorkshopRecipe>> CODEC =
            WorkshopRecipe.STREAM_CODEC.apply(ByteBufCodecs.list());

    @Override
    public void write(FriendlyByteBuf buffer) {
        CODEC.encode((RegistryFriendlyByteBuf) buffer, recipes);
    }

    public static WorkshopRecipesPacket decode(FriendlyByteBuf buffer) {
        return new WorkshopRecipesPacket(CODEC.decode((RegistryFriendlyByteBuf) buffer));
    }

    @Override
    public void handleOnClient() {
        WorkshopRecipe.setClientRecipes(recipes);
    }
}
