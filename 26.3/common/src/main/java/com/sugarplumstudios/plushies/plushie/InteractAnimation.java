package com.sugarplumstudios.plushies.plushie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;

/**
 * Whole-body animation a plushie plays when cuddled. Applied around the plushie's
 * bottom centre; {@code p} runs from 0 to 1 over {@link #duration} ticks.
 */
public enum InteractAnimation {
    HOP(24),
    SQUISH(22),
    SPIN(28),
    WIGGLE(24),
    NOD(26),
    FLOAT_UP(34),
    PULSE(30),
    WADDLE(30),
    BUZZ(28),
    YAWN(40),
    TWIRL(32),
    FLAP(30);

    public final int duration;

    InteractAnimation(int duration) {
        this.duration = duration;
    }

    public void apply(PoseStack pose, float p) {
        float env = Mth.sin(p * Mth.PI);
        float fade = 1.0F - p;
        switch (this) {
            case HOP -> {
                float bounce = Math.abs(Mth.sin(p * Mth.PI * 2.0F));
                pose.translate(0.0F, bounce * 0.2F * (1.0F - 0.4F * p), 0.0F);
                float stretch = 1.0F + 0.08F * bounce;
                float squeeze = 1.0F / Mth.sqrt(stretch);
                pose.scale(squeeze, stretch, squeeze);
            }
            case SQUISH -> {
                float wave = Mth.sin(p * Mth.PI * 3.0F) * fade;
                pose.scale(1.0F + 0.14F * wave, 1.0F - 0.22F * wave, 1.0F + 0.14F * wave);
            }
            case SPIN -> {
                pose.translate(0.0F, 0.08F * env, 0.0F);
                pose.rotate(Axis.YP.rotationDegrees(360.0F * easeInOut(p)));
            }
            case WIGGLE -> pose.rotate(Axis.ZP.rotationDegrees(14.0F * Mth.sin(p * Mth.PI * 6.0F) * fade));
            case NOD -> pose.rotate(Axis.XP.rotationDegrees(-12.0F * Mth.sin(p * Mth.PI * 4.0F) * fade));
            case FLOAT_UP -> {
                pose.translate(0.0F, 0.28F * env, 0.0F);
                pose.rotate(Axis.ZP.rotationDegrees(6.0F * Mth.sin(p * Mth.PI * 4.0F) * env));
            }
            case PULSE -> {
                float beat = 1.0F + 0.14F * Math.abs(Mth.sin(p * Mth.PI * 4.0F)) * fade;
                pose.scale(beat, beat, beat);
            }
            case WADDLE -> {
                pose.translate(0.0F, 0.04F * Math.abs(Mth.sin(p * Mth.PI * 6.0F)) * env, 0.0F);
                pose.rotate(Axis.YP.rotationDegrees(8.0F * Mth.sin(p * Mth.PI * 3.0F) * env));
                pose.rotate(Axis.ZP.rotationDegrees(12.0F * Mth.sin(p * Mth.PI * 6.0F) * env));
            }
            case BUZZ -> pose.translate(0.025F * Mth.sin(p * 90.0F) * env, 0.12F * env, 0.025F * Mth.cos(p * 70.0F) * env);
            case YAWN -> {
                float e2 = env * env;
                pose.rotate(Axis.XP.rotationDegrees(6.0F * env));
                pose.scale(1.0F - 0.06F * e2, 1.0F + 0.16F * e2, 1.0F - 0.06F * e2);
            }
            case TWIRL -> {
                pose.translate(0.0F, 0.12F * env, 0.0F);
                pose.rotate(Axis.YP.rotationDegrees(360.0F * easeInOut(p)));
                pose.rotate(Axis.ZP.rotationDegrees(8.0F * Mth.sin(p * Mth.PI * 2.0F) * env));
            }
            case FLAP -> {
                pose.translate(0.0F, 0.18F * env + 0.04F * Mth.sin(p * Mth.PI * 8.0F) * env, 0.0F);
                pose.rotate(Axis.XP.rotationDegrees(4.0F * Mth.sin(p * Mth.PI * 8.0F) * env));
            }
        }
    }

    /** Envelope used to make wings, tails and gills move faster and wider while cuddled. */
    public static float partEnvelope(float p) {
        return p < 0.0F ? 0.0F : Mth.sin(p * Mth.PI);
    }

    private static float easeInOut(float p) {
        return p < 0.5F ? 2.0F * p * p : 1.0F - (-2.0F * p + 2.0F) * (-2.0F * p + 2.0F) / 2.0F;
    }
}
