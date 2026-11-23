package com.sugarplumstudios.plushies.client.screen;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.client.ClientCollectionData;
import com.sugarplumstudios.plushies.collection.CollectionMilestone;
import com.sugarplumstudios.plushies.collection.PlushieCollection;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.network.ClaimRewardPacket;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.plushie.PlushieRarity;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** The Plushie Collection book: browse every plushie, track progress and claim rewards. */
public class CollectionBookScreen extends Screen {
    private static final Identifier TEXTURE = SugarplumPlushies.id("textures/gui/collection_book.png");
    private static final int WIDTH = 256;
    private static final int HEIGHT = 180;
    private static final int INK = 0x6B3A5B;
    private static final int SOFT_INK = 0x9C6F8E;

    private static final int GRID_X = 16;
    private static final int GRID_Y = 28;
    private static final int GRID_COLS = 5;
    private static final int CELL_W = 21;
    private static final int CELL_H = 27;
    private static final int TAB_Y = 150;
    private static final int TAB_W = 50;
    private static final int TAB_H = 14;
    private static final int RIGHT_X = 134;
    private static final int RIGHT_CENTER = 190;
    private static final int REWARD_Y = 28;
    private static final int REWARD_STEP = 19;

    private final PlushieSpecies[] species = PlushieSpecies.values();
    private final List<Button> claimButtons = new ArrayList<>();
    private PlushieSpecies selected = PlushieSpecies.STRAWBERRY_BUNNY;
    private boolean rewardsPage;
    private int left;
    private int top;

    public CollectionBookScreen() {
        super(Component.translatable("gui.sugarplumplushies.book.title"));
    }

    @Override
    protected void init() {
        left = (width - WIDTH) / 2;
        top = (height - HEIGHT) / 2;
        rebuildWidgets();
    }

    @Override
    protected void rebuildWidgets() {
        clearWidgets();
        claimButtons.clear();
        if (rewardsPage) {
            CollectionMilestone[] milestones = CollectionMilestone.values();
            for (int i = 0; i < milestones.length; i++) {
                int index = i;
                Button button = Button.builder(Component.translatable("gui.sugarplumplushies.book.claim"), b -> claim(index))
                        .bounds(left + 206, top + REWARD_Y + i * REWARD_STEP + 1, 36, 14)
                        .build();
                claimButtons.add(addRenderableWidget(button));
            }
            updateClaimButtons();
        }
    }

    private void claim(int index) {
        ModNetwork.sendToServer(new ClaimRewardPacket(index));
    }

    private void updateClaimButtons() {
        int[] masks = ClientCollectionData.masks();
        int claimed = ClientCollectionData.claimed();
        CollectionMilestone[] milestones = CollectionMilestone.values();
        for (int i = 0; i < claimButtons.size(); i++) {
            Button button = claimButtons.get(i);
            boolean isClaimed = (claimed & milestones[i].bit()) != 0;
            button.visible = !isClaimed;
            button.active = milestones[i].reached(masks);
        }
    }

    @Override
    public void tick() {
        updateClaimButtons();
    }

    /** Screen.render draws the background (once per frame, it blurs), so the book pages are drawn with it, under the buttons. */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0.0F, 0.0F, WIDTH, HEIGHT, 256, 256);
        renderLeftPage(graphics, mouseX, mouseY);
        if (rewardsPage) {
            renderRewardsPage(graphics);
        } else {
            renderDetailPage(graphics);
        }
        // Undiscovered plushies get a dusky veil drawn over their icons.
        if (!veils.isEmpty()) {
            graphics.nextStratum();
            for (int[] veil : veils) {
                graphics.fill(veil[0], veil[1], veil[2], veil[3], VEIL);
            }
            veils.clear();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderTooltips(graphics, mouseX, mouseY);
    }

    private static final int VEIL = 0xC86B4566;
    private final List<int[]> veils = new ArrayList<>();

    private void renderLeftPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        drawCentered(graphics, title, left + 68, top + 13, INK);

        for (int i = 0; i < species.length; i++) {
            PlushieSpecies entry = species[i];
            int cx = left + GRID_X + (i % GRID_COLS) * CELL_W;
            int cy = top + GRID_Y + (i / GRID_COLS) * CELL_H;
            boolean isSelected = entry == selected && !rewardsPage;
            boolean hovered = mouseX >= cx && mouseX < cx + 18 && mouseY >= cy && mouseY < cy + 18;
            int mask = ClientCollectionData.mask(entry);
            frame(graphics, cx, cy, 18, 18, isSelected ? 0xFFD9558F : hovered ? 0xFFE9A2C2 : 0xFFEBC4D6,
                    mask != 0 ? 0xFFFFEFF6 : 0xFFF3E6EC);
            renderPlushieIcon(graphics, PlushieItem.create(entry, firstOwnedVariant(mask)), cx + 1, cy + 1, mask != 0);
            for (int v = 0; v < PlushieSpecies.VARIANT_COUNT; v++) {
                int dx = cx + 1 + v * 4 + 1;
                int dy = cy + 20;
                boolean owned = (mask & (1 << v)) != 0;
                int color = owned ? (v == PlushieSpecies.SPARKLE ? 0xFFFFC94D : 0xFFE5679F) : 0xFFE3CFD9;
                graphics.fill(dx, dy, dx + 3, dy + 3, color);
            }
        }

        int[] masks = ClientCollectionData.masks();
        int speciesCount = PlushieCollection.speciesCount(masks);
        int variantCount = PlushieCollection.variantCount(masks);
        graphics.text(font, Component.translatable("gui.sugarplumplushies.book.species", speciesCount, species.length),
                left + GRID_X, top + 113, opaque(INK), false);
        graphics.text(font, Component.translatable("gui.sugarplumplushies.book.variants", variantCount, PlushieSpecies.TOTAL_VARIANTS),
                left + GRID_X, top + 123, opaque(INK), false);
        int barX = left + GRID_X;
        int barY = top + 135;
        int barW = 102;
        frame(graphics, barX, barY, barW, 7, 0xFFE3B5CB, 0xFFFFF1F7);
        int filled = Math.round((barW - 2) * variantCount / (float) PlushieSpecies.TOTAL_VARIANTS);
        if (filled > 0) {
            graphics.fill(barX + 1, barY + 1, barX + 1 + filled, barY + 6, 0xFFF07DB1);
            graphics.fill(barX + 1, barY + 1, barX + 1 + filled, barY + 3, 0xFFF9A9CD);
        }

        renderTab(graphics, left + GRID_X, Component.translatable("gui.sugarplumplushies.book.collection"), !rewardsPage, mouseX, mouseY);
        renderTab(graphics, left + GRID_X + TAB_W + 4, Component.translatable("gui.sugarplumplushies.book.rewards"), rewardsPage, mouseX, mouseY);
        if (hasClaimableReward()) {
            int dot = left + GRID_X + TAB_W * 2 + 1;
            graphics.fill(dot, top + TAB_Y - 1, dot + 4, top + TAB_Y + 3, 0xFFFF4F93);
        }
    }

    private void renderTab(GuiGraphicsExtractor graphics, int x, Component label, boolean active, int mouseX, int mouseY) {
        int y = top + TAB_Y;
        boolean hovered = mouseX >= x && mouseX < x + TAB_W && mouseY >= y && mouseY < y + TAB_H;
        frame(graphics, x, y, TAB_W, TAB_H, active ? 0xFFC9578F : 0xFFE3B5CB,
                active ? 0xFFF48DBC : hovered ? 0xFFFCE0EC : 0xFFFFF1F7);
        drawCentered(graphics, label, x + TAB_W / 2, y + 3, active ? 0xFFFFFF : INK);
    }

    private void renderDetailPage(GuiGraphicsExtractor graphics) {
        int mask = ClientCollectionData.mask(selected);
        boolean owned = mask != 0;
        ItemStack hero = PlushieItem.create(selected, firstOwnedVariant(mask));

        graphics.pose().pushMatrix();
        graphics.pose().translate(left + RIGHT_CENTER - 24, top + 14);
        graphics.pose().scale(3.0F, 3.0F);
        graphics.item(hero, 0, 0);
        graphics.pose().popMatrix();
        if (!owned) {
            veils.add(new int[]{left + RIGHT_CENTER - 24, top + 14, left + RIGHT_CENTER + 24, top + 62});
        }

        MutableComponent name = owned ? selected.displayName() : Component.translatable("gui.sugarplumplushies.book.unknown");
        PlushieRarity rarity = selected.rarity();
        drawCentered(graphics, name.withStyle(style -> style.withColor(darken(rarity.color()))), left + RIGHT_CENTER, top + 67, INK);
        drawCentered(graphics, rarity.starLine().append(" ").append(rarity.displayName()), left + RIGHT_CENTER, top + 78, INK);

        Component description = owned ? selected.description() : Component.translatable("gui.sugarplumplushies.book.hint");
        List<FormattedCharSequence> lines = font.split(description, 104);
        for (int i = 0; i < Math.min(5, lines.size()); i++) {
            graphics.text(font, lines.get(i), left + RIGHT_X + 4, top + 92 + i * 9, opaque(SOFT_INK), false);
        }

        for (int v = 0; v < PlushieSpecies.VARIANT_COUNT; v++) {
            int ix = left + RIGHT_CENTER - 50 + v * 26;
            int iy = top + 143;
            boolean has = (mask & (1 << v)) != 0;
            frame(graphics, ix - 1, iy - 1, 18, 18, v == PlushieSpecies.SPARKLE ? 0xFFF0C55A : 0xFFEBC4D6, has ? 0xFFFFEFF6 : 0xFFF3E6EC);
            renderPlushieIcon(graphics, PlushieItem.create(selected, v), ix, iy, has);
            if (has) {
                graphics.text(font, "✔", ix + 12, iy + 10, opaque(0x3FB06A), false);
            }
        }
    }

    private void renderRewardsPage(GuiGraphicsExtractor graphics) {
        drawCentered(graphics, Component.translatable("gui.sugarplumplushies.book.rewards_title"), left + RIGHT_CENTER, top + 13, INK);
        int[] masks = ClientCollectionData.masks();
        int claimed = ClientCollectionData.claimed();
        CollectionMilestone[] milestones = CollectionMilestone.values();
        for (int i = 0; i < milestones.length; i++) {
            CollectionMilestone milestone = milestones[i];
            int y = top + REWARD_Y + i * REWARD_STEP;
            boolean reached = milestone.reached(masks);
            boolean isClaimed = (claimed & milestone.bit()) != 0;
            graphics.pose().pushMatrix();
            graphics.pose().translate(left + RIGHT_X + 2, y + 1);
            graphics.pose().scale(0.8F, 0.8F);
            graphics.text(font, milestone.displayName(), 0, 0, opaque(reached ? INK : SOFT_INK), false);
            graphics.pose().popMatrix();
            graphics.pose().pushMatrix();
            graphics.pose().translate(left + RIGHT_X + 2, y + 9);
            graphics.pose().scale(0.7F, 0.7F);
            int progress = Math.min(milestone.progress(masks), milestone.required());
            graphics.text(font, Component.translatable("gui.sugarplumplushies.book.progress", progress, milestone.required()),
                    0, 0, opaque(reached ? 0x3FA066 : SOFT_INK), false);
            graphics.pose().popMatrix();
            List<ItemStack> rewards = milestone.rewards();
            if (!rewards.isEmpty()) {
                graphics.item(rewards.get(0), left + 186, y);
            }
            if (isClaimed) {
                graphics.text(font, Component.translatable("gui.sugarplumplushies.book.claimed"), left + 208, y + 4, opaque(0x3FA066), false);
            }
        }
    }

    private void renderTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (int i = 0; i < species.length; i++) {
            int cx = left + GRID_X + (i % GRID_COLS) * CELL_W;
            int cy = top + GRID_Y + (i / GRID_COLS) * CELL_H;
            if (mouseX >= cx && mouseX < cx + 18 && mouseY >= cy && mouseY < cy + 18) {
                int mask = ClientCollectionData.mask(species[i]);
                List<Component> lines = new ArrayList<>();
                lines.add(mask != 0 ? species[i].displayName() : Component.translatable("gui.sugarplumplushies.book.unknown"));
                lines.add(Component.translatable("gui.sugarplumplushies.book.variant_count", Integer.bitCount(mask), PlushieSpecies.VARIANT_COUNT)
                        .withStyle(ChatFormatting.GRAY));
                graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                return;
            }
        }
        if (rewardsPage) {
            CollectionMilestone[] milestones = CollectionMilestone.values();
            for (int i = 0; i < milestones.length; i++) {
                int y = top + REWARD_Y + i * REWARD_STEP;
                if (mouseX >= left + RIGHT_X && mouseX < left + 204 && mouseY >= y && mouseY < y + REWARD_STEP - 1) {
                    List<Component> lines = new ArrayList<>();
                    lines.add(milestones[i].displayName().withStyle(ChatFormatting.LIGHT_PURPLE));
                    lines.add(milestones[i].requirement().withStyle(ChatFormatting.GRAY));
                    lines.add(Component.translatable("gui.sugarplumplushies.book.reward_list").withStyle(ChatFormatting.GOLD));
                    for (ItemStack reward : milestones[i].rewards()) {
                        lines.add(Component.literal(" " + reward.getCount() + "x ").append(reward.getHoverName()).withStyle(ChatFormatting.WHITE));
                    }
                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                    return;
                }
            }
        } else {
            for (int v = 0; v < PlushieSpecies.VARIANT_COUNT; v++) {
                int ix = left + RIGHT_CENTER - 50 + v * 26;
                int iy = top + 143;
                if (mouseX >= ix && mouseX < ix + 16 && mouseY >= iy && mouseY < iy + 16) {
                    boolean has = ClientCollectionData.has(selected, v);
                    int color = selected.rarity(v).color();
                    List<Component> lines = new ArrayList<>();
                    lines.add(selected.variantName(v).withStyle(style -> style.withColor(color)));
                    lines.add(Component.translatable(has ? "gui.sugarplumplushies.book.collected" : "gui.sugarplumplushies.book.not_collected")
                            .withStyle(has ? ChatFormatting.GREEN : ChatFormatting.GRAY));
                    if (v == PlushieSpecies.SPARKLE && !has) {
                        lines.add(Component.translatable("gui.sugarplumplushies.book.sparkle_hint").withStyle(ChatFormatting.GOLD));
                    }
                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                    return;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (event.button() == 0) {
            for (int i = 0; i < species.length; i++) {
                int cx = left + GRID_X + (i % GRID_COLS) * CELL_W;
                int cy = top + GRID_Y + (i / GRID_COLS) * CELL_H;
                if (mouseX >= cx && mouseX < cx + 18 && mouseY >= cy && mouseY < cy + 18) {
                    selected = species[i];
                    if (rewardsPage) {
                        rewardsPage = false;
                        rebuildWidgets();
                    }
                    playClick();
                    return true;
                }
            }
            int tabY = top + TAB_Y;
            if (mouseY >= tabY && mouseY < tabY + TAB_H) {
                int collectionX = left + GRID_X;
                int rewardsX = collectionX + TAB_W + 4;
                if (mouseX >= collectionX && mouseX < collectionX + TAB_W && rewardsPage) {
                    rewardsPage = false;
                    rebuildWidgets();
                    playPageTurn();
                    return true;
                }
                if (mouseX >= rewardsX && mouseX < rewardsX + TAB_W && !rewardsPage) {
                    rewardsPage = true;
                    rebuildWidgets();
                    playPageTurn();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private boolean hasClaimableReward() {
        int[] masks = ClientCollectionData.masks();
        int claimed = ClientCollectionData.claimed();
        for (CollectionMilestone milestone : CollectionMilestone.values()) {
            if ((claimed & milestone.bit()) == 0 && milestone.reached(masks)) {
                return true;
            }
        }
        return false;
    }

    private void renderPlushieIcon(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y, boolean revealed) {
        graphics.item(stack, x, y);
        if (!revealed) {
            veils.add(new int[]{x, y, x + 16, y + 16});
        }
    }

    private static int opaque(int rgb) {
        return 0xFF000000 | rgb;
    }

    private static int firstOwnedVariant(int mask) {
        for (int v = 0; v < PlushieSpecies.VARIANT_COUNT; v++) {
            if ((mask & (1 << v)) != 0) {
                return v;
            }
        }
        return 0;
    }

    private static void frame(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int border, int fill) {
        graphics.fill(x + 1, y, x + w - 1, y + h, border);
        graphics.fill(x, y + 1, x + w, y + h - 1, border);
        graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
    }

    private void drawCentered(GuiGraphicsExtractor graphics, Component text, int centerX, int y, int color) {
        graphics.text(font, text, centerX - font.width(text) / 2, y, opaque(color), false);
    }

    private static int darken(int rgb) {
        int r = (int) (((rgb >> 16) & 0xFF) * 0.62F);
        int g = (int) (((rgb >> 8) & 0xFF) * 0.62F);
        int b = (int) ((rgb & 0xFF) * 0.62F);
        return (r << 16) | (g << 8) | b;
    }

    private void playClick() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.3F, 0.35F));
    }

    private void playPageTurn() {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
