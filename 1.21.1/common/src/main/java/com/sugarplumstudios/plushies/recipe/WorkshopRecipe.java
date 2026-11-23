package com.sugarplumstudios.plushies.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import com.sugarplumstudios.plushies.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A Plushie Workshop pattern: a list of counted ingredients taken straight from the
 * player's inventory, producing one plushie (or other item).
 */
public class WorkshopRecipe implements Recipe<RecipeInput> {
    public record CountedIngredient(Ingredient ingredient, int count) {
        public CountedIngredient {
            count = Math.max(1, count);
        }

        public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(i -> i.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count)
        ).apply(i, CountedIngredient::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CountedIngredient::ingredient,
                ByteBufCodecs.VAR_INT, CountedIngredient::count,
                CountedIngredient::new);
    }

    /** The player's inventory seen as a recipe input. */
    public record ContainerInput(Container container) implements RecipeInput {
        @Override
        public ItemStack getItem(int slot) {
            return container.getItem(slot);
        }

        @Override
        public int size() {
            return container.getContainerSize();
        }
    }

    private final List<CountedIngredient> ingredients;
    private final ItemStack result;

    public WorkshopRecipe(List<CountedIngredient> ingredients, ItemStack result) {
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
        List<RecipeHolder<WorkshopRecipe>> holders = new ArrayList<>(manager.getAllRecipesFor(ModRecipes.WORKSHOP.get()));
        holders.sort(Comparator.<RecipeHolder<WorkshopRecipe>>comparingInt(h -> h.value().sortKey()).thenComparing(h -> h.id().toString()));
        List<WorkshopRecipe> list = new ArrayList<>(holders.size());
        holders.forEach(h -> list.add(h.value()));
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
        if (ItemStack.isSameItemSameComponents(stack, result)) {
            return false; // a Sparkle upgrade never eats another Sparkle of the same plushie
        }
        if (!allowTreasured && stack.getItem() instanceof PlushieItem) {
            return PlushieItem.getVariant(stack) != PlushieSpecies.SPARKLE && !stack.has(DataComponents.CUSTOM_NAME);
        }
        return true;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return input instanceof ContainerInput container && canCraft(container.container());
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        ingredients.forEach(ingredient -> list.add(ingredient.ingredient()));
        return list;
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
        private static final MapCodec<WorkshopRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                CountedIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(WorkshopRecipe::getCountedIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(WorkshopRecipe::getResult)
        ).apply(i, WorkshopRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, WorkshopRecipe> STREAM_CODEC = StreamCodec.composite(
                CountedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), WorkshopRecipe::getCountedIngredients,
                ItemStack.STREAM_CODEC, WorkshopRecipe::getResult,
                WorkshopRecipe::new);

        @Override
        public MapCodec<WorkshopRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WorkshopRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
