package com.sugarplumstudios.plushies.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.entity.PlushieBlockEntity;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlushieBlockEntityRenderer implements BlockEntityRenderer<PlushieBlockEntity, PlushieBlockEntityRenderer.State> {
    private final Font font;

    public PlushieBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PlushieBlockEntity plushie, State state, float partialTick, Vec3 camera,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(plushie, state, partialTick, camera, breakProgress);
        BlockState block = plushie.getBlockState();
        if (!(block.getBlock() instanceof PlushieBlock plushieBlock)) {
            state.species = null;
            return;
        }
        PlushieSpecies species = plushieBlock.getSpecies();
        state.species = species;
        state.variant = block.getValue(PlushieBlock.VARIANT);
        state.rotation = RotationSegment.convertToDegrees(block.getValue(PlushieBlock.ROTATION));
        state.time = PlushieRenderHelper.time(plushie.getLevel(), partialTick) + plushie.idleOffset();
        state.progress = plushie.cuddleProgress(partialTick, species.interaction().duration);
        Component name = plushie.getCustomName();
        state.name = name != null && shouldShowName(plushie) ? name : null;
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.species == null) {
            return;
        }
        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-state.rotation));
        pose.translate(-0.5F, 0.0F, -0.5F);
        PlushieRenderHelper.submit(state.species, state.variant, pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY,
                state.time, state.progress);
        pose.popPose();

        if (state.name != null) {
            submitName(state.name, state.species.height() / 16.0F + 0.3F, pose, collector, camera, state.lightCoords);
        }
    }

    private static boolean shouldShowName(PlushieBlockEntity plushie) {
        return switch (PlushieConfig.CLIENT.nameplates.get()) {
            case ALWAYS -> true;
            case NEVER -> false;
            case LOOKING -> {
                HitResult hit = Minecraft.getInstance().hitResult;
                yield hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK
                        && blockHit.getBlockPos().equals(plushie.getBlockPos());
            }
        };
    }

    private void submitName(Component name, float y, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera, int light) {
        pose.pushPose();
        pose.translate(0.5F, y, 0.5F);
        pose.mulPose(camera.orientation);
        pose.scale(0.025F, -0.025F, 0.025F);
        int background = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        float x = -font.width(name) / 2.0F;
        collector.submitText(pose, x, 0.0F, name.getVisualOrderText(), false, Font.DisplayMode.SEE_THROUGH, light,
                0x20FFFFFF, background, 0);
        collector.submitText(pose, x, 0.0F, name.getVisualOrderText(), false, Font.DisplayMode.NORMAL, light,
                0xFFFFE6F3, 0, 0);
        pose.popPose();
    }

    public static class State extends BlockEntityRenderState {
        @Nullable
        PlushieSpecies species;
        int variant;
        float rotation;
        float time;
        float progress;
        @Nullable
        Component name;
    }
}
