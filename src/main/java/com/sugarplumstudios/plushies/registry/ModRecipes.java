package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.recipe.WorkshopRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, SugarplumPlushies.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SugarplumPlushies.MOD_ID);

    public static final RegistryObject<RecipeType<WorkshopRecipe>> WORKSHOP = TYPES.register("plushie_workshop",
            () -> RecipeType.simple(SugarplumPlushies.id("plushie_workshop")));

    public static final RegistryObject<RecipeSerializer<WorkshopRecipe>> WORKSHOP_SERIALIZER = SERIALIZERS.register("plushie_workshop",
            WorkshopRecipe.Serializer::new);

    private ModRecipes() {
    }
}
