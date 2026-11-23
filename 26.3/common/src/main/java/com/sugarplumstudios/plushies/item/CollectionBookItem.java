package com.sugarplumstudios.plushies.item;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import com.sugarplumstudios.plushies.client.ClientAccess;
import com.sugarplumstudios.plushies.collection.CollectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CollectionBookItem extends Item {
    public CollectionBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
            ClientAccess.openCollectionBook();
        } else if (player instanceof ServerPlayer serverPlayer) {
            CollectionEvents.scan(serverPlayer);
            CollectionEvents.sync(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.sugarplumplushies.plushie_collection_book.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
