package com.sugarplumstudios.plushies.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import com.sugarplumstudios.plushies.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A Plushie Workshop pattern: a list of counted ingredients taken straight from the
 * player's inventory, producing one plushie (or other item).
 */
public class WorkshopRecipe implements Recipe<Container> {
    public record CountedIngredient(Ingredient ingredient, int count) {
    }

    private final ResourceLocation id;
    private final List<CountedIngredient> ingredients;
    private final ItemStack result;

    public WorkshopRecipe(ResourceLocation id, List<CountedIngredient> ingredients, ItemStack result) {
        this.id = id;
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
    }

    public List<CountedIngredient> getCountedIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    /** All workshop recipes in a stable order shared by client and server. */
    public static List<WorkshopRecipe> sorted(RecipeManager manager) {
        List<WorkshopRecipe> list = new ArrayList<>(manager.getAllRecipesFor(ModRecipes.WORKSHOP.get()));
        list.sort(Comparator.comparingInt(WorkshopRecipe::sortKey).thenComparing(r -> r.getId().toString()));
        return list;
    }

    private int sortKey() {
        if (result.getItem() instanceof PlushieItem plushie) {
            return plushie.getSpecies().ordinal() * PlushieSpecies.VARIANT_COUNT + PlushieItem.getVariant(result);
        }
        return 100_000;
    }

    /** Number of items in the container matching this ingredient (excluding the recipe's own result). */
    public int countAvailable(Container container, CountedIngredient ingredient) {
        int total = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (usable(container, slot, stack, true) && ingredient.ingredient().test(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public boolean canCraft(Container container) {
        return take(container, false);
    }

    public void consume(Container container) {
        take(container, true);
    }

    /**
     * Walks the container taking ingredients. Treasured plushies (Sparkle Editions or
     * named ones) are only used when nothing else matches.
     */
    private boolean take(Container container, boolean commit) {
        int[] remaining = new int[container.getContainerSize()];
        for (int slot = 0; slot < remaining.length; slot++) {
            remaining[slot] = container.getItem(slot).getCount();
        }
        for (CountedIngredient ingredient : ingredients) {
            int needed = ingredient.count();
            for (int pass = 0; pass < 2 && needed > 0; pass++) {
                for (int slot = 0; slot < remaining.length && needed > 0; slot++) {
                    ItemStack stack = container.getItem(slot);
                    if (remaining[slot] <= 0 || !usable(container, slot, stack, pass == 1) || !ingredient.ingredient().test(stack)) {
                        continue;
                    }
                    int taken = Math.min(remaining[slot], needed);
                    remaining[slot] -= taken;
                    needed -= taken;
                    if (commit) {
                        container.removeItem(slot, taken);
                    }
                }
            }
            if (needed > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean usable(Container container, int slot, ItemStack stack, boolean allowTreasured) {
        if (stack.isEmpty()) {
            return false;
        }
        if (container instanceof Inventory && slot >= Inventory.INVENTORY_SIZE && slot < Inventory.INVENTORY_SIZE + 4) {
            return false; // never take worn armour
        }
        if (ItemStack.isSameItemSameTags(stack, result)) {
            return false; // a Sparkle upgrade never eats another Sparkle of the same plushie
        }
        if (!allowTreasured && stack.getItem() instanceof PlushieItem) {
            return PlushieItem.getVariant(stack) != PlushieSpecies.SPARKLE && !stack.hasCustomHoverName();
        }
        return true;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return canCraft(container);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        ingredients.forEach(ingredient -> list.add(ingredient.ingredient()));
        return list;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModItems.PLUSHIE_WORKSHOP.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WORKSHOP_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.WORKSHOP.get();
    }

    public static class Serializer implements RecipeSerializer<WorkshopRecipe> {
        @Override
        public WorkshopRecipe fromJson(ResourceLocation id, JsonObject json) {
            List<CountedIngredient> ingredients = new ArrayList<>();
            JsonArray array = GsonHelper.getAsJsonArray(json, "ingredients");
            for (JsonElement element : array) {
                JsonObject entry = element.getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(entry.get("ingredient"));
                int count = GsonHelper.getAsInt(entry, "count", 1);
                ingredients.add(new CountedIngredient(ingredient, Math.max(1, count)));
            }
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return new WorkshopRecipe(id, ingredients, result);
        }

        @Override
        public WorkshopRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<CountedIngredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);
                ingredients.add(new CountedIngredient(ingredient, buffer.readVarInt()));
            }
            return new WorkshopRecipe(id, ingredients, buffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WorkshopRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (CountedIngredient ingredient : recipe.ingredients) {
                ingredient.ingredient().toNetwork(buffer);
                buffer.writeVarInt(ingredient.count());
            }
            buffer.writeItem(recipe.result);
        }
    }
}
