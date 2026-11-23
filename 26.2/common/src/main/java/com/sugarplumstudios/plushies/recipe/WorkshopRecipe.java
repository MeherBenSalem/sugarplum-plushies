package com.sugarplumstudios.plushies.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModRecipes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
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
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
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

    public static final MapCodec<WorkshopRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CountedIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(WorkshopRecipe::getCountedIngredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(WorkshopRecipe::template)
    ).apply(i, WorkshopRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WorkshopRecipe> STREAM_CODEC = StreamCodec.composite(
            CountedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), WorkshopRecipe::getCountedIngredients,
            ItemStackTemplate.STREAM_CODEC, WorkshopRecipe::template,
            WorkshopRecipe::new);

    public static RecipeSerializer<WorkshopRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }

    private final List<CountedIngredient> ingredients;
    private final ItemStackTemplate template;
    // Made on first use: item components are not bound yet while recipes load.
    private ItemStack result;

    public WorkshopRecipe(List<CountedIngredient> ingredients, ItemStackTemplate template) {
        this.ingredients = List.copyOf(ingredients);
        this.template = template;
    }

    /** The client's copy of the workshop recipes, sent by the server when a workshop opens. */
    private static volatile List<WorkshopRecipe> clientRecipes = List.of();

    public static List<WorkshopRecipe> clientRecipes() {
        return clientRecipes;
    }

    public static void setClientRecipes(List<WorkshopRecipe> recipes) {
        clientRecipes = List.copyOf(recipes);
    }

    public ItemStackTemplate template() {
        return template;
    }

    public List<CountedIngredient> getCountedIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        if (result == null) {
            result = template.create();
        }
        return result;
    }

    /**
     * All workshop recipes in a stable order. Only the server has recipes; it sends this
     * list to the client right before it opens the workshop, so both sides share the order.
     */
    public static List<WorkshopRecipe> sorted(RecipeManager manager) {
        List<RecipeHolder<WorkshopRecipe>> holders = new ArrayList<>();
        for (RecipeHolder<?> holder : manager.getRecipes()) {
            if (holder.value() instanceof WorkshopRecipe) {
                @SuppressWarnings("unchecked")
                RecipeHolder<WorkshopRecipe> workshop = (RecipeHolder<WorkshopRecipe>) holder;
                holders.add(workshop);
            }
        }
        holders.sort(Comparator.<RecipeHolder<WorkshopRecipe>>comparingInt(h -> h.value().sortKey())
                .thenComparing(h -> h.id().identifier().toString()));
        List<WorkshopRecipe> list = new ArrayList<>(holders.size());
        holders.forEach(h -> list.add(h.value()));
        return list;
    }

    private int sortKey() {
        if (getResult().getItem() instanceof PlushieItem plushie) {
            return plushie.getSpecies().ordinal() * PlushieSpecies.VARIANT_COUNT + PlushieItem.getVariant(getResult());
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
        if (ItemStack.isSameItemSameComponents(stack, getResult())) {
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
    public ItemStack assemble(RecipeInput input) {
        return getResult().copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<WorkshopRecipe> getSerializer() {
        return ModRecipes.WORKSHOP_SERIALIZER.get();
    }

    @Override
    public RecipeType<WorkshopRecipe> getType() {
        return ModRecipes.WORKSHOP.get();
    }
}
