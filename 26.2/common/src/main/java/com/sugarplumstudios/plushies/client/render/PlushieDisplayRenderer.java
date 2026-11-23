package com.sugarplumstudios.plushies.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.block.ShapeUtil;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Draws the plushies sitting on shelves, stands, beds and in cabinets, fully animated. */
public class PlushieDisplayRenderer implements BlockEntityRenderer<PlushieDisplayBlockEntity, PlushieDisplayRenderer.State> {
    public PlushieDisplayRenderer(BlockEntityRendererProvider.Context context) {
    }

    /** NeoForge asks the renderer for the culling box (no @Override: Fabric has no such hook and uses the block's box). */
    public AABB getRenderBoundingBox(PlushieDisplayBlockEntity display) {
        return display.getRenderBoundingBox();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PlushieDisplayBlockEntity display, State state, float partialTick, Vec3 camera,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(display, state, partialTick, camera, breakProgress);
        state.slots.clear();
        BlockState block = display.getBlockState();
        if (!(block.getBlock() instanceof PlushieDisplayBlock displayBlock)) {
            return;
        }
        state.facing = ShapeUtil.yRotation(block.getValue(PlushieDisplayBlock.FACING));
        state.clock = PlushieRenderHelper.time(display.getLevel(), partialTick);
        state.spin = displayBlock.spins() && display.isSpinning() && PlushieConfig.CLIENT.displayStandSpin.get();
        state.spinSpeed = PlushieConfig.CLIENT.displayStandSpinSpeed.get().floatValue();
        for (int slot = 0; slot < display.size() && slot < displayBlock.slotCount(); slot++) {
            ItemStack stack = display.getItem(slot);
            if (!(stack.getItem() instanceof PlushieItem item)) {
                continue;
            }
            PlushieSpecies species = item.getSpecies();
            state.slots.add(new Slot(species, PlushieItem.getVariant(stack), displayBlock.slotPose(slot),
                    state.clock + display.idleOffset(slot),
                    display.cuddleProgress(slot, partialTick, species.interaction().duration)));
        }
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (Slot slot : state.slots) {
            PlushieDisplayBlock.SlotPose slotPose = slot.pose();
            pose.pushPose();
            pose.translate(0.5F, 0.0F, 0.5F);
            pose.mulPose(Axis.YP.rotationDegrees(-state.facing));
            pose.translate(-0.5F, 0.0F, -0.5F);
            pose.translate(slotPose.x() / 16.0F, slotPose.y() / 16.0F, slotPose.z() / 16.0F);
            if (state.spin) {
                pose.mulPose(Axis.YP.rotationDegrees(state.clock * state.spinSpeed));
            }
            if (slotPose.lean() != 0.0F) {
                pose.mulPose(Axis.XP.rotationDegrees(slotPose.lean()));
            }
            pose.scale(slotPose.scale(), slotPose.scale(), slotPose.scale());
            pose.translate(-0.5F, 0.0F, -0.5F);
            PlushieRenderHelper.submit(slot.species(), slot.variant(), pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY,
                    slot.time(), slot.progress());
            pose.popPose();
        }
    }

    record Slot(PlushieSpecies species, int variant, PlushieDisplayBlock.SlotPose pose, float time, float progress) {
    }

    public static class State extends BlockEntityRenderState {
        final List<Slot> slots = new ArrayList<>();
        float facing;
        float clock;
        boolean spin;
        float spinSpeed;
    }
}
