package com.sugarplumstudios.plushies.client.screen;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.client.ClientCollectionData;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.menu.PlushieWorkshopMenu;
import com.sugarplumstudios.plushies.plushie.PlushieRarity;
import com.sugarplumstudios.plushies.recipe.WorkshopRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Pattern picker for the Plushie Workshop. */
public class PlushieWorkshopScreen extends AbstractContainerScreen<PlushieWorkshopMenu> {
    private static final Identifier TEXTURE = SugarplumPlushies.id("textures/gui/plushie_workshop.png");
    private static final int TEXT = 0x6B3A5B;

    private static final int GRID_X = 10;
    private static final int GRID_Y = 20;
    private static final int COLS = 5;
    private static final int ROWS = 6;
    private static final int CELL = 18;
    private static final int SCROLL_X = 102;
    private static final int SCROLL_W = 8;
    private static final int SCROLL_H = ROWS * CELL;
    private static final int KNOB_H = 15;
    private static final int PREVIEW_X = 118;
    private static final int PREVIEW_Y = 22;
    private static final int INGREDIENT_X = 118;
    private static final int INGREDIENT_Y = 62;
    private static final int INGREDIENT_STEP_X = 22;
    private static final int INGREDIENT_STEP_Y = 20;
    private static final int INGREDIENT_COLS = 5;
    private static final int INGREDIENT_SLOTS = 10;
    private static final int BUTTON_X = 118;
    private static final int BUTTON_Y = 106;
    private static final int BUTTON_W = 108;
    private static final int BUTTON_H = 18;

    private int scrollRow;
    private int selected = -1;
    private boolean draggingScrollbar;

    public PlushieWorkshopScreen(PlushieWorkshopMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 236, 222);
        this.titleLabelX = 10;
        this.titleLabelY = 7;
        this.inventoryLabelX = PlushieWorkshopMenu.INVENTORY_X;
        this.inventoryLabelY = 129;
    }

    private List<WorkshopRecipe> recipes() {
        return menu.getRecipes();
    }

    private int maxScroll() {
        int rows = (recipes().size() + COLS - 1) / COLS;
        return Math.max(0, rows - ROWS);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        renderHoverTooltips(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = leftPos;
        int y = topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
        List<int[]> washes = new ArrayList<>();

        List<WorkshopRecipe> recipes = recipes();
        Inventory inventory = menu.getPlayer().getInventory();
        for (int i = 0; i < COLS * ROWS; i++) {
            int index = scrollRow * COLS + i;
            if (index >= recipes.size()) {
                break;
            }
            int cx = x + GRID_X + (i % COLS) * CELL;
            int cy = y + GRID_Y + (i / COLS) * CELL;
            WorkshopRecipe recipe = recipes.get(index);
            boolean hovered = mouseX >= cx && mouseX < cx + CELL && mouseY >= cy && mouseY < cy + CELL;
            if (index == selected) {
                graphics.fill(cx + 1, cy + 1, cx + CELL - 1, cy + CELL - 1, 0xFFF7A8CB);
            } else if (hovered) {
                graphics.fill(cx + 1, cy + 1, cx + CELL - 1, cy + CELL - 1, 0xFFFBD3E4);
            }
            graphics.item(recipe.getResult(), cx + 1, cy + 1);
            if (!recipe.canCraft(inventory)) {
                washes.add(new int[]{cx + 1, cy + 1, cx + CELL - 1, cy + CELL - 1, 0x88FFF4F8});
            }
            if (recipe.getResult().getItem() instanceof PlushieItem plushie
                    && !ClientCollectionData.has(plushie.getSpecies(), PlushieItem.getVariant(recipe.getResult()))) {
                washes.add(new int[]{cx + CELL - 5, cy + 2, cx + CELL - 2, cy + 5, 0xFFFF6FA8});
            }
        }

        int knobY = y + GRID_Y + (maxScroll() == 0 ? 0 : (int) ((SCROLL_H - KNOB_H) * (scrollRow / (float) maxScroll())));
        int knobColor = maxScroll() == 0 ? 0xFFE9C9D8 : 0xFFE06C9F;
        graphics.fill(x + SCROLL_X, knobY, x + SCROLL_X + SCROLL_W, knobY + KNOB_H, knobColor);
        graphics.fill(x + SCROLL_X + 1, knobY + 1, x + SCROLL_X + SCROLL_W - 1, knobY + 3, 0x55FFFFFF);

        if (selected >= 0 && selected < recipes.size()) {
            renderSelected(graphics, recipes.get(selected), inventory, mouseX, mouseY, washes);
        } else {
            List<FormattedCharSequence> lines = font.split(Component.translatable("gui.sugarplumplushies.workshop.select"), 100);
            for (int i = 0; i < lines.size(); i++) {
                graphics.text(font, lines.get(i), x + PREVIEW_X + 2, y + 52 + i * 10, 0xFF9C6F8E, false);
            }
        }
        // Washes, badges and counts go over the items, so they are drawn in the next stratum.
        graphics.nextStratum();
        for (int[] w : washes) {
            if (w.length == 5) {
                graphics.fill(w[0], w[1], w[2], w[3], w[4]);
            }
        }
        for (Runnable text : pendingText) {
            text.run();
        }
        pendingText.clear();
    }

    private final List<Runnable> pendingText = new ArrayList<>();

    private void renderSelected(GuiGraphicsExtractor graphics, WorkshopRecipe recipe, Inventory inventory, int mouseX, int mouseY,
                                List<int[]> washes) {
        int x = leftPos;
        int y = topPos;
        ItemStack result = recipe.getResult();

        graphics.pose().pushMatrix();
        graphics.pose().translate(x + PREVIEW_X + 2, y + PREVIEW_Y + 2);
        graphics.pose().scale(2.0F, 2.0F);
        graphics.item(result, 0, 0);
        graphics.pose().popMatrix();

        List<FormattedCharSequence> nameLines = font.split(result.getHoverName(), 70);
        for (int i = 0; i < Math.min(2, nameLines.size()); i++) {
            graphics.text(font, nameLines.get(i), x + 156, y + 24 + i * 10, 0xFFFFFFFF, true);
        }
        if (result.getItem() instanceof PlushieItem plushie) {
            PlushieRarity rarity = plushie.getSpecies().rarity(PlushieItem.getVariant(result));
            graphics.text(font, rarity.starLine(), x + 156, y + 46, 0xFFFFFFFF, false);
        }

        List<WorkshopRecipe.CountedIngredient> ingredients = recipe.getCountedIngredients();
        for (int i = 0; i < ingredients.size() && i < INGREDIENT_SLOTS; i++) {
            WorkshopRecipe.CountedIngredient ingredient = ingredients.get(i);
            int sx = x + INGREDIENT_X + (i % INGREDIENT_COLS) * INGREDIENT_STEP_X;
            int sy = y + INGREDIENT_Y + (i / INGREDIENT_COLS) * INGREDIENT_STEP_Y;
            boolean enough = recipe.countAvailable(inventory, ingredient) >= ingredient.count();
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, enough ? 0xFFDDF5E4 : 0xFFFBD5DA);
            ItemStack display = displayStack(ingredient);
            graphics.item(display, sx + 1, sy + 1);
            String count = Integer.toString(ingredient.count());
            pendingText.add(() -> graphics.text(font, count, sx + 18 - font.width(count), sy + 10, enough ? 0xFFFFFFFF : 0xFFFF7A8A, true));
        }

        boolean craftable = recipe.canCraft(inventory);
        boolean hovered = isInButton(mouseX, mouseY);
        int bx = x + BUTTON_X;
        int by = y + BUTTON_Y;
        int border = craftable ? 0xFFB8467E : 0xFFC9A9B9;
        int fill = !craftable ? 0xFFE8D5DE : hovered ? 0xFFFF8CC0 : 0xFFF27AB0;
        graphics.fill(bx + 1, by, bx + BUTTON_W - 1, by + BUTTON_H, border);
        graphics.fill(bx, by + 1, bx + BUTTON_W, by + BUTTON_H - 1, border);
        graphics.fill(bx + 1, by + 1, bx + BUTTON_W - 1, by + BUTTON_H - 1, fill);
        graphics.fill(bx + 2, by + 2, bx + BUTTON_W - 2, by + 5, craftable ? 0x44FFFFFF : 0x22FFFFFF);
        Component label = Component.translatable("gui.sugarplumplushies.workshop.sew");
        graphics.text(font, label, bx + (BUTTON_W - font.width(label)) / 2, by + 5,
                craftable ? 0xFFFFFFFF : 0xFFA58798, craftable);
    }

    private static ItemStack displayStack(WorkshopRecipe.CountedIngredient ingredient) {
        List<Holder<Item>> options = ingredient.ingredient().items().toList();
        if (options.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(options.get((int) ((System.currentTimeMillis() / 1000L) % options.size())));
    }

    private void renderHoverTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        List<WorkshopRecipe> recipes = recipes();
        int cell = cellAt(mouseX, mouseY);
        if (cell >= 0) {
            List<Component> lines = new ArrayList<>(Screen.getTooltipFromItem(minecraft, recipes.get(cell).getResult()));
            WorkshopRecipe recipe = recipes.get(cell);
            if (recipe.getResult().getItem() instanceof PlushieItem plushie
                    && !ClientCollectionData.has(plushie.getSpecies(), PlushieItem.getVariant(recipe.getResult()))) {
                lines.add(Component.translatable("gui.sugarplumplushies.workshop.new").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
            graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
            return;
        }
        if (selected < 0 || selected >= recipes.size()) {
            return;
        }
        WorkshopRecipe recipe = recipes.get(selected);
        List<WorkshopRecipe.CountedIngredient> ingredients = recipe.getCountedIngredients();
        for (int i = 0; i < ingredients.size() && i < INGREDIENT_SLOTS; i++) {
            int sx = leftPos + INGREDIENT_X + (i % INGREDIENT_COLS) * INGREDIENT_STEP_X;
            int sy = topPos + INGREDIENT_Y + (i / INGREDIENT_COLS) * INGREDIENT_STEP_Y;
            if (mouseX >= sx && mouseX < sx + 18 && mouseY >= sy && mouseY < sy + 18) {
                WorkshopRecipe.CountedIngredient ingredient = ingredients.get(i);
                int have = recipe.countAvailable(menu.getPlayer().getInventory(), ingredient);
                List<Component> lines = new ArrayList<>();
                lines.add(displayStack(ingredient).getHoverName());
                lines.add(Component.translatable("gui.sugarplumplushies.workshop.have", have, ingredient.count())
                        .withStyle(have >= ingredient.count() ? ChatFormatting.GREEN : ChatFormatting.RED));
                graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                return;
            }
        }
        if (isInButton(mouseX, mouseY)) {
            graphics.setTooltipForNextFrame(font, Component.translatable("gui.sugarplumplushies.workshop.shift"), mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFF000000 | TEXT, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFF000000 | TEXT, false);
    }

    private int cellAt(double mouseX, double mouseY) {
        double gx = mouseX - leftPos - GRID_X;
        double gy = mouseY - topPos - GRID_Y;
        if (gx < 0 || gy < 0 || gx >= COLS * CELL || gy >= ROWS * CELL) {
            return -1;
        }
        int index = (scrollRow + (int) (gy / CELL)) * COLS + (int) (gx / CELL);
        return index < recipes().size() ? index : -1;
    }

    private boolean isInButton(double mouseX, double mouseY) {
        double bx = mouseX - leftPos - BUTTON_X;
        double by = mouseY - topPos - BUTTON_Y;
        return selected >= 0 && bx >= 0 && by >= 0 && bx < BUTTON_W && by < BUTTON_H;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (MouseButtons.of(event) == MouseButtons.LEFT) {
            int cell = cellAt(mouseX, mouseY);
            if (cell >= 0) {
                selected = cell;
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.2F, 0.4F));
                return true;
            }
            if (isInButton(mouseX, mouseY) && selected < recipes().size()
                    && recipes().get(selected).canCraft(menu.getPlayer().getInventory())) {
                int id = minecraft.hasShiftDown() ? PlushieWorkshopMenu.BULK_OFFSET + selected : selected;
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                return true;
            }
            double sx = mouseX - leftPos - SCROLL_X;
            double sy = mouseY - topPos - GRID_Y;
            if (sx >= 0 && sx < SCROLL_W && sy >= 0 && sy < SCROLL_H && maxScroll() > 0) {
                draggingScrollbar = true;
                scrollTo(mouseY);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (draggingScrollbar) {
            scrollTo(event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingScrollbar = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double delta) {
        scrollRow = Mth.clamp(scrollRow - (int) Math.signum(delta), 0, maxScroll());
        return true;
    }

    private void scrollTo(double mouseY) {
        float fraction = (float) (mouseY - topPos - GRID_Y - KNOB_H / 2.0D) / (SCROLL_H - KNOB_H);
        scrollRow = Mth.clamp(Math.round(fraction * maxScroll()), 0, maxScroll());
    }
}
