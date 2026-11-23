package com.sugarplumstudios.plushies.collection;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.network.ModNetwork;
import com.sugarplumstudios.plushies.network.PlushieUnlockedPacket;
import com.sugarplumstudios.plushies.network.SyncCollectionPacket;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Watches player inventories for newly collected plushies and handles reward claims. The loaders call the
 * {@code on*} hooks from their own player events.
 */
public final class CollectionEvents {
    private CollectionEvents() {
    }

    /** End of every server player tick. */
    public static void onPlayerTick(ServerPlayer player) {
        if (player.tickCount % PlushieConfig.COMMON.scanInterval.get() == 0) {
            scan(player);
        }
    }

    public static void scan(ServerPlayer player) {
        boolean changed = false;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof PlushieItem plushie) {
                int variant = PlushieItem.getVariant(stack);
                if (PlushieCollection.unlock(player, plushie.getSpecies(), variant)) {
                    changed = true;
                    ModNetwork.sendTo(player, new PlushieUnlockedPacket(plushie.getSpecies().ordinal(), variant));
                }
            }
        }
        if (changed) {
            sync(player);
            awardAdvancements(player);
        }
    }

    public static void sync(ServerPlayer player) {
        ModNetwork.sendTo(player, new SyncCollectionPacket(PlushieCollection.masks(player), PlushieCollection.claimed(player)));
    }

    public static void claim(ServerPlayer player, int index) {
        CollectionMilestone[] milestones = CollectionMilestone.values();
        if (!PlushieConfig.COMMON.collectionRewards.get() || index < 0 || index >= milestones.length) {
            return;
        }
        CollectionMilestone milestone = milestones[index];
        int claimed = PlushieCollection.claimed(player);
        if ((claimed & milestone.bit()) != 0 || !milestone.reached(PlushieCollection.masks(player))) {
            return;
        }
        PlushieCollection.setClaimed(player, claimed | milestone.bit());
        List<ItemStack> rewards = milestone.rewards();
        for (ItemStack reward : rewards) {
            ItemStack copy = reward.copy();
            if (!player.getInventory().add(copy)) {
                player.drop(copy, false);
            }
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 0.6F, 1.3F);
        player.sendSystemMessage(Component.translatable("message.sugarplumplushies.reward_claimed", milestone.displayName())
                .withStyle(style -> style.withColor(0xFFB3D6)));
        sync(player);
    }

    private static void awardAdvancements(ServerPlayer player) {
        int[] masks = PlushieCollection.masks(player);
        if (PlushieCollection.speciesCount(masks) >= PlushieSpecies.values().length) {
            award(player, "all_species");
        }
        if (PlushieCollection.variantCount(masks) >= PlushieSpecies.TOTAL_VARIANTS) {
            award(player, "complete_collection");
        }
    }

    private static void award(ServerPlayer player, String path) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(SugarplumPlushies.id(path));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }

    /** Login, respawn and dimension change: the client needs the whole collection again. */
    public static void onJoin(ServerPlayer player) {
        sync(player);
    }
}
