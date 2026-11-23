package com.sugarplumstudios.plushies.plushie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;

/** Subtle looping motion a plushie performs while sitting around. {@code t} is in ticks. */
public enum IdleAnimation {
    BREATHE,
    BOB,
    SWAY,
    SLEEP,
    HOVER,
    WOBBLE;

    public void apply(PoseStack pose, float t) {
        switch (this) {
            case BREATHE -> breathe(pose, t, 0.08F, 0.018F);
            case BOB -> pose.translate(0.0F, 0.012F * (1.0F + Mth.sin(t * 0.1F)), 0.0F);
            case SWAY -> {
                pose.mulPose(Axis.ZP.rotationDegrees(2.2F * Mth.sin(t * 0.05F)));
                breathe(pose, t, 0.08F, 0.012F);
            }
            case SLEEP -> {
                float s = Mth.sin(t * 0.045F);
                pose.scale(1.0F + 0.012F * s, 1.0F + 0.03F * s, 1.0F + 0.012F * s);
            }
            case HOVER -> pose.translate(0.0F, 0.035F + 0.03F * Mth.sin(t * 0.09F), 0.0F);
            case WOBBLE -> pose.mulPose(Axis.ZP.rotationDegrees(2.5F * Mth.sin(t * 0.11F)));
        }
    }

    private static void breathe(PoseStack pose, float t, float speed, float amount) {
        float s = Mth.sin(t * speed);
        pose.scale(1.0F - amount * 0.45F * s, 1.0F + amount * s, 1.0F - amount * 0.45F * s);
    }
}
