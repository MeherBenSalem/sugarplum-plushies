package com.sugarplumstudios.plushies.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;

/** Maps the mod's single use() result onto 1.21's item-first interaction result. */
public final class UseBridge {
    private UseBridge() {
    }

    public static ItemInteractionResult toItem(InteractionResult result) {
        return switch (result) {
            case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
            case CONSUME -> ItemInteractionResult.CONSUME;
            case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
            case FAIL -> ItemInteractionResult.FAIL;
            default -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        };
    }
}
