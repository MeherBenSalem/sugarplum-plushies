package com.sugarplumstudios.plushies.block;

import net.minecraft.world.InteractionResult;

/** Maps the mod's single use() result onto the item-first interaction result. */
public final class UseBridge {
    private UseBridge() {
    }

    /** PASS lets the empty-hand interaction run, as it did before the item-first split. */
    public static InteractionResult toItem(InteractionResult result) {
        return result == InteractionResult.PASS ? InteractionResult.TRY_WITH_EMPTY_HAND : result;
    }
}
