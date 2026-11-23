package com.sugarplumstudios.plushies.block;

import net.minecraft.world.level.block.Block;

/** A lace-topped pedestal that slowly turns its plushie like a turntable. */
public class DisplayStandBlock extends PlushieDisplayBlock {
    private static final SlotPose SLOT = new SlotPose(8.0F, 5.0F, 8.0F, 0.62F, 0.0F);

    public DisplayStandBlock(Properties properties) {
        super(properties,
                Block.box(3.0D, 0.0D, 3.0D, 13.0D, 13.0D, 13.0D),
                Block.box(3.0D, 0.0D, 3.0D, 13.0D, 5.0D, 13.0D));
    }

    @Override
    public int slotCount() {
        return 1;
    }

    @Override
    public SlotPose slotPose(int slot) {
        return SLOT;
    }

    @Override
    public boolean spins() {
        return true;
    }
}
