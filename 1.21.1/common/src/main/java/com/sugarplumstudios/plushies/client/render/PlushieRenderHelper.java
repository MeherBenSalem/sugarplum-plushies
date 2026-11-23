package com.sugarplumstudios.plushies.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.plushie.InteractAnimation;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Draws a plushie (body plus animated parts) inside the unit cube with its bottom
 * centre at (0.5, 0, 0.5). Callers position, rotate and scale the pose first.
 */
public final class PlushieRenderHelper {
    private PlushieRenderHelper() {
    }

    public static float time(@Nullable Level level, float partialTick) {
        return level == null ? 0.0F : (level.getGameTime() % 72000L) + partialTick;
    }

    /**
     * @param time     animation clock in ticks
     * @param progress cuddle animation progress in [0, 1), or negative when idle
     */
    public static void render(PlushieSpecies species, int variant, PoseStack pose, MultiBufferSource buffers,
                              int light, int overlay, float time, float progress) {
        PlushieConfig.Client config = PlushieConfig.CLIENT;
        boolean idle = config.idleAnimations.get();
        float t = idle ? time * config.animationSpeed.get().floatValue() : 0.0F;
        float envelope = InteractAnimation.partEnvelope(progress);

        VertexConsumer consumer = buffers.getBuffer(Sheets.cutoutBlockSheet());
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        if (idle) {
            species.idle().apply(pose, t);
        }
        if (progress >= 0.0F) {
            species.interaction().apply(pose, progress);
        }
        pose.translate(-0.5F, 0.0F, -0.5F);

        renderer.renderModel(pose.last(), consumer, null, PlushieModels.body(species, variant),
                1.0F, 1.0F, 1.0F, light, overlay);

        List<PlushiePartTable.Part> parts = PlushiePartTable.parts(species);
        for (int i = 0; i < parts.size(); i++) {
            PlushiePartTable.Part part = parts.get(i);
            float px = part.pivotX() / 16.0F;
            float py = part.pivotY() / 16.0F;
            float pz = part.pivotZ() / 16.0F;
            pose.pushPose();
            pose.translate(px, py, pz);
            if (part.axis() != null && part.amplitude() != 0.0F) {
                float angle = part.amplitude() * Mth.sin(t * part.speed() + part.phase());
                if (envelope > 0.0F) {
                    angle += part.amplitude() * part.boost() * envelope * Mth.sin(time * part.speed() * 4.0F + part.phase());
                }
                pose.mulPose(part.axis().rotationDegrees(angle));
            }
            if (part.spin() != 0.0F) {
                float extraTurn = progress >= 0.0F ? 360.0F * progress * Math.signum(part.spin()) : 0.0F;
                pose.mulPose(Axis.YP.rotationDegrees(t * part.spin() + extraTurn));
            }
            if (part.bobAmplitude() != 0.0F) {
                pose.translate(0.0F, part.bobAmplitude() / 16.0F * Mth.sin(t * part.bobSpeed()), 0.0F);
            }
            pose.translate(-px, -py, -pz);
            renderer.renderModel(pose.last(), consumer, null, PlushieModels.part(species, variant, i),
                    1.0F, 1.0F, 1.0F, light, overlay);
            pose.popPose();
        }
        pose.popPose();
    }
}
