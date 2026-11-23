package com.sugarplumstudios.plushies.menu;

import com.sugarplumstudios.plushies.recipe.WorkshopRecipe;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import com.sugarplumstudios.plushies.registry.ModMenus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Plushie Workshop menu. The screen picks a pattern and presses "Sew!", which arrives
 * here as a menu button click; ingredients come straight out of the player's inventory.
 */
public class PlushieWorkshopMenu extends AbstractContainerMenu {
    public static final int BULK_OFFSET = 10_000;
    public static final int BULK_LIMIT = 16;
    public static final int INVENTORY_X = 38;
    public static final int INVENTORY_Y = 140;

    private final ContainerLevelAccess access;
    private final Player player;
    private final List<WorkshopRecipe> recipes;

    public PlushieWorkshopMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public PlushieWorkshopMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.PLUSHIE_WORKSHOP.get(), id);
        this.access = access;
        this.player = inventory.player;
        this.recipes = WorkshopRecipe.sorted(player.level().getRecipeManager());

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, INVENTORY_X + col * 18, INVENTORY_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, INVENTORY_X + col * 18, INVENTORY_Y + 58));
        }
    }

    public List<WorkshopRecipe> getRecipes() {
        return recipes;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        boolean bulk = id >= BULK_OFFSET;
        int index = bulk ? id - BULK_OFFSET : id;
        if (index < 0 || index >= recipes.size()) {
            return false;
        }
        WorkshopRecipe recipe = recipes.get(index);
        Inventory inventory = player.getInventory();
        int limit = bulk ? BULK_LIMIT : 1;
        int crafted = 0;
        while (crafted < limit && recipe.canCraft(inventory)) {
            recipe.consume(inventory);
            ItemStack result = recipe.assemble(new WorkshopRecipe.ContainerInput(inventory), player.level().registryAccess());
            if (!inventory.add(result)) {
                player.drop(result, false);
            }
            crafted++;
        }
        if (crafted > 0) {
            access.execute((level, pos) -> level.playSound(null, pos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.1F));
            access.execute((level, pos) -> level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.3F));
            broadcastChanges();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 27) {
            if (!moveItemStackTo(stack, 27, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, 27, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.PLUSHIE_WORKSHOP.get());
    }
}
