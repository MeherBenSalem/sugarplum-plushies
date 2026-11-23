package com.sugarplumstudios.plushies.client.toast;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

/** "New plushie collected!" pop-up. */
public class PlushieUnlockToast implements Toast {
    private static final Identifier TEXTURE = SugarplumPlushies.id("textures/gui/toast.png");
    private static final long DURATION = 5000L;

    private final ItemStack icon;
    private boolean playedSound;
    private Visibility visibility = Visibility.SHOW;

    public PlushieUnlockToast(ItemStack icon) {
        this.icon = icon;
    }

    @Override
    public Visibility getWantedVisibility() {
        return visibility;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs) {
        if (!playedSound && fullyVisibleForMs > 0L) {
            playedSound = true;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 1.4F, 1.0F));
        }
        visibility = fullyVisibleForMs >= DURATION ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0.0F, 0.0F, width(), height(), 256, 256);
        graphics.text(font, Component.translatable("toast.sugarplumplushies.unlocked"), 30, 7, 0xFFB04A86, false);
        graphics.text(font, font.substrByWidth(icon.getHoverName(), 125).getString(), 30, 18, 0xFF5B3254, false);
        graphics.fakeItem(icon, 8, 8);
    }
}
