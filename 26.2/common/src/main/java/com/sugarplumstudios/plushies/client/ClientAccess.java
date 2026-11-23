package com.sugarplumstudios.plushies.client;

import com.sugarplumstudios.plushies.client.screen.CollectionBookScreen;
import com.sugarplumstudios.plushies.client.toast.PlushieUnlockToast;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.client.Minecraft;

/** Client-only entry points called from common code only on the client (client-side item use, clientbound messages). */
public final class ClientAccess {
    private ClientAccess() {
    }

    public static void openCollectionBook() {
        Minecraft.getInstance().setScreen(new CollectionBookScreen());
    }

    public static void handleSync(int[] masks, int claimed) {
        ClientCollectionData.set(masks, claimed);
    }

    public static void handleUnlock(int speciesIndex, int variant) {
        PlushieSpecies[] all = PlushieSpecies.values();
        if (speciesIndex < 0 || speciesIndex >= all.length || !PlushieConfig.CLIENT.unlockToasts.get()) {
            return;
        }
        Minecraft.getInstance().gui.toastManager().addToast(new PlushieUnlockToast(PlushieItem.create(all[speciesIndex], variant)));
    }
}
