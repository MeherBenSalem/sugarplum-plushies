package com.sugarplumstudios.plushies.client.toast;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

/** "New plushie collected!" pop-up. */
public class PlushieUnlockToast implements Toast {
    private static final ResourceLocation TEXTURE = SugarplumPlushies.id("textures/gui/toast.png");
    private static final long DURATION = 5000L;

    private final ItemStack icon;
    private boolean playedSound;

    public PlushieUnlockToast(ItemStack icon) {
        this.icon = icon;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long timeSinceVisible) {
        graphics.blit(TEXTURE, 0, 0, 0, 0, width(), height());
        Font font = component.getMinecraft().font;
        graphics.drawString(font, Component.translatable("toast.sugarplumplushies.unlocked"), 30, 7, 0xB04A86, false);
        graphics.drawString(font, font.substrByWidth(icon.getHoverName(), 125).getString(), 30, 18, 0x5B3254, false);
        graphics.renderFakeItem(icon, 8, 8);
        if (!playedSound && timeSinceVisible > 0L) {
            playedSound = true;
            component.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 1.4F, 1.0F));
        }
        return timeSinceVisible >= DURATION ? Visibility.HIDE : Visibility.SHOW;
    }
}
