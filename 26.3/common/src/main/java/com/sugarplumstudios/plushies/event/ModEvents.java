package com.sugarplumstudios.plushies.event;

import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;


/** Loader-neutral event logic; each loader calls these from its own events. */
public final class ModEvents {
    private ModEvents() {
    }

    /**
     * Sneaking normally skips block interaction when holding something. Let sneak + brush (rotate backwards) and
     * sneak + empty main hand (pick up) reach plushies.
     */
    public static boolean allowSneakUse(Player player, Level level, BlockPos pos, InteractionHand hand, ItemStack held) {
        if (!player.isShiftKeyDown()) {
            return false;
        }
        Block block = level.getBlockState(pos).getBlock();
        if (!(block instanceof PlushieBlock) && !(block instanceof PlushieDisplayBlock)) {
            return false;
        }
        return held.is(Items.BRUSH) || (hand == InteractionHand.MAIN_HAND && held.isEmpty());
    }

    /** Runs the plushie block's own interaction for a sneak use that {@link #allowSneakUse} let through. */
    public static InteractionResult sneakUse(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PlushieBlock plushie) {
            return plushie.use(state, level, pos, player, hand, hit);
        }
        if (state.getBlock() instanceof PlushieDisplayBlock display) {
            return display.use(state, level, pos, player, hand, hit);
        }
        return InteractionResult.PASS;
    }

    // Wandering trader trades are data-driven on this Minecraft version and are not added here.
}
