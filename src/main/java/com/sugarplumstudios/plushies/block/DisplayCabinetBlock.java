package com.sugarplumstudios.plushies.block;

import net.minecraft.world.level.block.Block;

/** Glass-fronted miniature cabinet with two shelves of two plushies each. */
public class DisplayCabinetBlock extends PlushieDisplayBlock {
    private static final SlotPose[] SLOTS = {
            new SlotPose(4.75F, 1.5F, 8.25F, 0.4F, 0.0F),
            new SlotPose(11.25F, 1.5F, 8.25F, 0.4F, 0.0F),
            new SlotPose(4.75F, 8.5F, 8.25F, 0.4F, 0.0F),
            new SlotPose(11.25F, 8.5F, 8.25F, 0.4F, 0.0F)
    };

    public DisplayCabinetBlock(Properties properties) {
        super(properties, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
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
        return (y < 0.5D ? 0 : 2) + (x < 0.5D ? 0 : 1);
    }
}
