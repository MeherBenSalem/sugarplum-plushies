package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.platform.RegistryEntry;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.core.registries.Registries;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.recipe.WorkshopRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SugarplumPlushies.MOD_ID);

    public static final RegistryEntry<RecipeType<WorkshopRecipe>> WORKSHOP = Services.PLATFORM.register(Registries.RECIPE_TYPE, "plushie_workshop",
            () -> RecipeType.simple(SugarplumPlushies.id("plushie_workshop")));

    public static final RegistryEntry<RecipeSerializer<WorkshopRecipe>> WORKSHOP_SERIALIZER = Services.PLATFORM.register(Registries.RECIPE_SERIALIZER, "plushie_workshop",
            WorkshopRecipe.Serializer::new);

    private ModRecipes() {
    }

    /** Loads the class, which registers everything above. */
    public static void init() {
    }
}
