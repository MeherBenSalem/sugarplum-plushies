package com.sugarplumstudios.plushies.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.block.ShapeUtil;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Draws the plushies sitting on shelves, stands, beds and in cabinets, fully animated. */
public class PlushieDisplayRenderer implements BlockEntityRenderer<PlushieDisplayBlockEntity> {
    public PlushieDisplayRenderer(BlockEntityRendererProvider.Context context) {
    }

    /** NeoForge asks the renderer for the culling box (no @Override: Fabric has no such hook and uses the block's box). */
    public AABB getRenderBoundingBox(PlushieDisplayBlockEntity display) {
        return display.getRenderBoundingBox();
    }

    @Override
    public void render(PlushieDisplayBlockEntity display, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        BlockState state = display.getBlockState();
        if (!(state.getBlock() instanceof PlushieDisplayBlock block)) {
            return;
        }
        float facing = ShapeUtil.yRotation(state.getValue(PlushieDisplayBlock.FACING));
        float clock = PlushieRenderHelper.time(display.getLevel(), partialTick);
        boolean spin = block.spins() && display.isSpinning() && PlushieConfig.CLIENT.displayStandSpin.get();

        for (int slot = 0; slot < display.size() && slot < block.slotCount(); slot++) {
            ItemStack stack = display.getItem(slot);
            if (!(stack.getItem() instanceof PlushieItem item)) {
                continue;
            }
            PlushieSpecies species = item.getSpecies();
            PlushieDisplayBlock.SlotPose slotPose = block.slotPose(slot);
            float time = clock + display.idleOffset(slot);
            float progress = display.cuddleProgress(slot, partialTick, species.interaction().duration);

            pose.pushPose();
            pose.translate(0.5F, 0.0F, 0.5F);
            pose.mulPose(Axis.YP.rotationDegrees(-facing));
            pose.translate(-0.5F, 0.0F, -0.5F);
            pose.translate(slotPose.x() / 16.0F, slotPose.y() / 16.0F, slotPose.z() / 16.0F);
            if (spin) {
                pose.mulPose(Axis.YP.rotationDegrees(clock * PlushieConfig.CLIENT.displayStandSpinSpeed.get().floatValue()));
            }
            if (slotPose.lean() != 0.0F) {
                pose.mulPose(Axis.XP.rotationDegrees(slotPose.lean()));
            }
            pose.scale(slotPose.scale(), slotPose.scale(), slotPose.scale());
            pose.translate(-0.5F, 0.0F, -0.5F);
            PlushieRenderHelper.render(species, PlushieItem.getVariant(stack), pose, buffers, light, OverlayTexture.NO_OVERLAY, time, progress);
            pose.popPose();
        }
    }
}
