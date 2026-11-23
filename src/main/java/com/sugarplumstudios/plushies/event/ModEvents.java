package com.sugarplumstudios.plushies.event;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieRarity;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = SugarplumPlushies.MOD_ID)
public final class ModEvents {
    private ModEvents() {
    }

    /**
     * Sneaking normally skips block interaction when holding something. Let sneak +
     * brush (rotate backwards) and sneak + empty main hand (pick up) reach plushies.
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().isShiftKeyDown()) {
            return;
        }
        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        if (!(block instanceof PlushieBlock) && !(block instanceof PlushieDisplayBlock)) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (held.is(Items.BRUSH) || (event.getHand() == InteractionHand.MAIN_HAND && held.isEmpty())) {
            event.setUseBlock(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {
        if (!PlushieConfig.COMMON.wanderingTraderTrades.get()) {
            return;
        }
        event.getGenericTrades().add((trader, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(ModItems.SUGAR_STAR.get()), 3, 10, 0.05F));
        event.getGenericTrades().add((trader, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(ModItems.SOFT_FABRIC.get(), 6), 8, 4, 0.05F));
        PlushieSpecies[] friendly = Arrays.stream(PlushieSpecies.values())
                .filter(species -> species.rarity() == PlushieRarity.COMMON || species.rarity() == PlushieRarity.UNCOMMON)
                .toArray(PlushieSpecies[]::new);
        event.getRareTrades().add((trader, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD, 12),
                        PlushieItem.create(friendly[random.nextInt(friendly.length)], random.nextInt(3)), 1, 15, 0.05F));
    }
}
