package com.sugarplumstudios.plushies.config;

import com.sugarplumstudios.plushies.platform.Services;

/**
 * The mod's two config files, {@code config/sugarplumplushies-common.toml} (server rules) and
 * {@code config/sugarplumplushies-client.toml}. Same layout and keys on every loader and version.
 */
public final class PlushieConfig {
    private static final TomlConfig.Builder COMMON_BUILDER = new TomlConfig.Builder();
    public static final Common COMMON = new Common(COMMON_BUILDER);
    private static final TomlConfig COMMON_FILE = COMMON_BUILDER.build("sugarplumplushies-common.toml");
    private static final TomlConfig.Builder CLIENT_BUILDER = new TomlConfig.Builder();
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    private static final TomlConfig CLIENT_FILE = CLIENT_BUILDER.build("sugarplumplushies-client.toml");

    private PlushieConfig() {
    }

    /** Server rules; read at mod start. */
    public static void loadCommon() {
        COMMON_FILE.load(Services.PLATFORM.configDir());
    }

    /** Client options; read when the client starts. */
    public static void loadClient() {
        CLIENT_FILE.load(Services.PLATFORM.configDir());
    }

    public static final class Common {
        public final TomlConfig.BooleanValue interactionMessages;
        public final TomlConfig.BooleanValue interactionParticles;
        public final TomlConfig.IntValue interactionCooldown;
        public final TomlConfig.BooleanValue sneakPickUp;
        public final TomlConfig.BooleanValue consumeNameTags;
        public final TomlConfig.BooleanValue collectionRewards;
        public final TomlConfig.IntValue scanInterval;
        public final TomlConfig.BooleanValue wanderingTraderTrades;

        Common(TomlConfig.Builder builder) {
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
        public final TomlConfig.BooleanValue idleAnimations;
        public final TomlConfig.DoubleValue animationSpeed;
        public final TomlConfig.BooleanValue ambientParticles;
        public final TomlConfig.EnumValue<NameplateMode> nameplates;
        public final TomlConfig.BooleanValue displayStandSpin;
        public final TomlConfig.DoubleValue displayStandSpinSpeed;
        public final TomlConfig.BooleanValue unlockToasts;

        Client(TomlConfig.Builder builder) {
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
