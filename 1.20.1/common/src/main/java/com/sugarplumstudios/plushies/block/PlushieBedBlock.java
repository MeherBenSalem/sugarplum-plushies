package com.sugarplumstudios.plushies.block;

import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** A cosy little pet bed where a plushie can nap (and snore tiny Zzz's). */
public class PlushieBedBlock extends PlushieDisplayBlock {
    private static final SlotPose SLOT = new SlotPose(8.0F, 2.5F, 7.5F, 0.62F, 14.0F);

    private final PastelColor color;

    public PlushieBedBlock(PastelColor color, Properties properties) {
        super(properties,
                Block.box(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D),
                Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D));
        this.color = color;
    }

    public PastelColor getColor() {
        return color;
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!PlushieConfig.CLIENT.ambientParticles.get() || random.nextFloat() > 0.4F) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof PlushieDisplayBlockEntity display && display.occupiedCount() > 0) {
            level.addParticle(ModParticles.SLEEPY_Z.get(), pos.getX() + 0.45D + random.nextDouble() * 0.2D,
                    pos.getY() + 0.6D, pos.getZ() + 0.45D + random.nextDouble() * 0.2D, 0.0D, 0.01D, 0.0D);
        }
    }
}
