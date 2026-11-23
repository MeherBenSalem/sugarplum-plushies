package com.sugarplumstudios.plushies.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.entity.PlushieBlockEntity;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;

public class PlushieBlockEntityRenderer implements BlockEntityRenderer<PlushieBlockEntity> {
    private final Font font;

    public PlushieBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(PlushieBlockEntity plushie, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        BlockState state = plushie.getBlockState();
        if (!(state.getBlock() instanceof PlushieBlock block)) {
            return;
        }
        PlushieSpecies species = block.getSpecies();
        int variant = state.getValue(PlushieBlock.VARIANT);
        float time = PlushieRenderHelper.time(plushie.getLevel(), partialTick) + plushie.idleOffset();
        float progress = plushie.cuddleProgress(partialTick, species.interaction().duration);

        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees(state.getValue(PlushieBlock.ROTATION))));
        pose.translate(-0.5F, 0.0F, -0.5F);
        PlushieRenderHelper.render(species, variant, pose, buffers, light, overlay, time, progress);
        pose.popPose();

        Component name = plushie.getCustomName();
        if (name != null && shouldShowName(plushie)) {
            renderName(name, species.height() / 16.0F + 0.3F, pose, buffers, light);
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

    private void renderName(Component name, float y, PoseStack pose, MultiBufferSource buffers, int light) {
        Minecraft minecraft = Minecraft.getInstance();
        pose.pushPose();
        pose.translate(0.5F, y, 0.5F);
        pose.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        pose.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = pose.last().pose();
        int background = (int) (minecraft.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        float x = -font.width(name) / 2.0F;
        font.drawInBatch(name, x, 0.0F, 0x20FFFFFF, false, matrix, buffers, Font.DisplayMode.SEE_THROUGH, background, light);
        font.drawInBatch(name, x, 0.0F, 0xFFFFE6F3, false, matrix, buffers, Font.DisplayMode.NORMAL, 0, light);
        pose.popPose();
    }
}
