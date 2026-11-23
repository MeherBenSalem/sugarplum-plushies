package com.sugarplumstudios.plushies.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class PlushieConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = common.getLeft();
        COMMON_SPEC = common.getRight();
        Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
    }

    private PlushieConfig() {
    }

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue interactionMessages;
        public final ForgeConfigSpec.BooleanValue interactionParticles;
        public final ForgeConfigSpec.IntValue interactionCooldown;
        public final ForgeConfigSpec.BooleanValue sneakPickUp;
        public final ForgeConfigSpec.BooleanValue consumeNameTags;
        public final ForgeConfigSpec.BooleanValue collectionRewards;
        public final ForgeConfigSpec.IntValue scanInterval;
        public final ForgeConfigSpec.BooleanValue wanderingTraderTrades;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("How plushies react when players play with them").push("interactions");
            interactionMessages = builder
                    .comment("Show a little personality message above the hotbar when a plushie is cuddled.")
                    .define("interactionMessages", true);
            interactionParticles = builder
                    .comment("Spawn hearts, petals, sparkles and other particles when a plushie is cuddled.")
                    .define("interactionParticles", true);
            interactionCooldown = builder
                    .comment("Ticks a plushie needs to rest between cuddles.")
                    .defineInRange("interactionCooldownTicks", 12, 0, 200);
            sneakPickUp = builder
                    .comment("Allow sneak + right-click with an empty hand to pick a placed plushie back up.")
                    .define("sneakPickUp", true);
            consumeNameTags = builder
                    .comment("Use up the name tag when naming a placed plushie (never consumed in creative).")
                    .define("consumeNameTags", true);
            builder.pop();

            builder.comment("The plushie collection book").push("collection");
            collectionRewards = builder
                    .comment("Allow claiming milestone rewards from the collection book.")
                    .define("collectionRewards", true);
            scanInterval = builder
                    .comment("How often (in ticks) player inventories are checked for newly collected plushies.")
                    .defineInRange("inventoryScanIntervalTicks", 20, 5, 200);
            builder.pop();

            builder.comment("World integration").push("world");
            wanderingTraderTrades = builder
                    .comment("Wandering traders sell Sugar Stars, Soft Fabric and the occasional plushie.")
                    .define("wanderingTraderTrades", true);
            builder.pop();
        }
    }

    public enum NameplateMode {
        ALWAYS, LOOKING, NEVER
    }

    public static final class Client {
        public final ForgeConfigSpec.BooleanValue idleAnimations;
        public final ForgeConfigSpec.DoubleValue animationSpeed;
        public final ForgeConfigSpec.BooleanValue ambientParticles;
        public final ForgeConfigSpec.EnumValue<NameplateMode> nameplates;
        public final ForgeConfigSpec.BooleanValue displayStandSpin;
        public final ForgeConfigSpec.DoubleValue displayStandSpinSpeed;
        public final ForgeConfigSpec.BooleanValue unlockToasts;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Plushie rendering").push("rendering");
            idleAnimations = builder
                    .comment("Plushies gently breathe, bob, sway, flap and wag while idle.")
                    .define("idleAnimations", true);
            animationSpeed = builder
                    .comment("Speed multiplier for idle animations.")
                    .defineInRange("animationSpeed", 1.0D, 0.25D, 3.0D);
            ambientParticles = builder
                    .comment("Some plushies (Moon Cat, Sleepy Panda, Cherry Blossom Fox, Sparkle Editions...) emit ambient particles.")
                    .define("ambientParticles", true);
            nameplates = builder
                    .comment("When to show the custom name of a named plushie: ALWAYS, LOOKING (when looked at) or NEVER.")
                    .defineEnum("nameplates", NameplateMode.LOOKING);
            displayStandSpin = builder
                    .comment("Plushies on display stands slowly turn like a turntable (can be toggled per stand with a brush).")
                    .define("displayStandSpin", true);
            displayStandSpinSpeed = builder
                    .comment("Degrees per tick a display stand turns.")
                    .defineInRange("displayStandSpinSpeed", 1.5D, 0.1D, 10.0D);
            builder.pop();

            builder.comment("Collection").push("collection");
            unlockToasts = builder
                    .comment("Show a toast when a new plushie (or variant) is added to your collection.")
                    .define("unlockToasts", true);
            builder.pop();
        }
    }
}
