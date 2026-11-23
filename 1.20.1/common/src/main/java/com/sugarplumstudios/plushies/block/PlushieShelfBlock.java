package com.sugarplumstudios.plushies.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

/** Wall shelf with room for two plushies side by side. */
public class PlushieShelfBlock extends PlushieDisplayBlock {
    private static final SlotPose[] SLOTS = {
            new SlotPose(4.25F, 3.5F, 11.75F, 0.5F, 0.0F),
            new SlotPose(11.75F, 3.5F, 11.75F, 0.5F, 0.0F)
    };

    public PlushieShelfBlock(Properties properties) {
        super(properties,
                Block.box(0.5D, 0.0D, 7.75D, 15.5D, 11.0D, 16.0D),
                Shapes.or(Block.box(0.5D, 1.25D, 7.75D, 15.5D, 3.5D, 16.0D),
                        Block.box(2.0D, 0.0D, 14.0D, 3.5D, 2.0D, 16.0D),
                        Block.box(12.5D, 0.0D, 14.0D, 14.0D, 2.0D, 16.0D)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        Direction facing = face.getAxis().isHorizontal() ? face : context.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public int slotCount() {
        return SLOTS.length;
    }

    @Override
    public SlotPose slotPose(int slot) {
        return SLOTS[slot];
    }

    @Override
    protected int slotForHit(double x, double y, double z) {
        return x < 0.5D ? 0 : 1;
    }
}
