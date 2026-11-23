package com.sugarplumstudios.plushies.event;

import net.minecraft.world.item.trading.ItemCost;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieRarity;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;

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

    /** Wandering trader offers from the common (level 1) pool; empty when disabled in the config. */
    public static List<VillagerTrades.ItemListing> genericTrades() {
        if (!PlushieConfig.COMMON.wanderingTraderTrades.get()) {
            return List.of();
        }
        return List.of(
                (trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModItems.SUGAR_STAR.get()), 3, 10, 0.05F),
                (trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(ModItems.SOFT_FABRIC.get(), 6), 8, 4, 0.05F));
    }

    /** Wandering trader offers from the rare (level 2) pool; empty when disabled in the config. */
    public static List<VillagerTrades.ItemListing> rareTrades() {
        if (!PlushieConfig.COMMON.wanderingTraderTrades.get()) {
            return List.of();
        }
        PlushieSpecies[] friendly = Arrays.stream(PlushieSpecies.values())
                .filter(species -> species.rarity() == PlushieRarity.COMMON || species.rarity() == PlushieRarity.UNCOMMON)
                .toArray(PlushieSpecies[]::new);
        return List.of((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 12),
                PlushieItem.create(friendly[random.nextInt(friendly.length)], random.nextInt(3)), 1, 15, 0.05F));
    }
}
