package com.sugarplumstudios.plushies.plushie;

import com.sugarplumstudios.plushies.registry.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The fifteen plushies of the Sugarplum collection. Every species has four variants:
 * three colourways and a rare "Sparkle Edition" (always index {@link #SPARKLE}).
 */
public enum PlushieSpecies {
    STRAWBERRY_BUNNY(PlushieRarity.COMMON, InteractAnimation.HOP, IdleAnimation.BREATHE, 9, 15, MapColor.COLOR_PINK, 0,
            "classic", "vanilla", "blueberry"),
    PINK_TEDDY_BEAR(PlushieRarity.COMMON, InteractAnimation.SQUISH, IdleAnimation.BREATHE, 9, 13, MapColor.COLOR_PINK, 0,
            "pink", "honey", "snow"),
    LAVENDER_KITTY(PlushieRarity.COMMON, InteractAnimation.WIGGLE, IdleAnimation.SWAY, 9, 13, MapColor.COLOR_PURPLE, 0,
            "lavender", "peach", "midnight"),
    CLOUD_PUPPY(PlushieRarity.UNCOMMON, InteractAnimation.HOP, IdleAnimation.BOB, 10, 12, MapColor.SNOW, 0,
            "sky", "sunset", "storm"),
    CHERRY_BLOSSOM_FOX(PlushieRarity.UNCOMMON, InteractAnimation.SPIN, IdleAnimation.SWAY, 10, 14, MapColor.COLOR_PINK, 0,
            "sakura", "snow", "twilight"),
    ANGEL_BUNNY(PlushieRarity.RARE, InteractAnimation.FLOAT_UP, IdleAnimation.HOVER, 10, 13, MapColor.SNOW, 5,
            "seraph", "blush", "sky"),
    SLEEPY_PANDA(PlushieRarity.UNCOMMON, InteractAnimation.YAWN, IdleAnimation.SLEEP, 10, 13, MapColor.SNOW, 0,
            "classic", "strawberry", "mint"),
    HEART_BEAR(PlushieRarity.RARE, InteractAnimation.PULSE, IdleAnimation.BREATHE, 10, 13, MapColor.COLOR_PINK, 0,
            "rose", "ruby", "lilac"),
    LITTLE_DUCK(PlushieRarity.COMMON, InteractAnimation.WADDLE, IdleAnimation.WOBBLE, 8, 12, MapColor.COLOR_YELLOW, 0,
            "sunny", "cream", "mint"),
    MOON_CAT(PlushieRarity.RARE, InteractAnimation.TWIRL, IdleAnimation.BREATHE, 10, 11, MapColor.COLOR_BLUE, 7,
            "midnight", "dawn", "dusk"),
    SUGARPLUM_DRAGON(PlushieRarity.EPIC, InteractAnimation.FLAP, IdleAnimation.BREATHE, 10, 14, MapColor.COLOR_PURPLE, 0,
            "sugarplum", "seafoam", "rose_quartz"),
    PASTEL_AXOLOTL(PlushieRarity.UNCOMMON, InteractAnimation.WIGGLE, IdleAnimation.BREATHE, 10, 6, MapColor.COLOR_PINK, 0,
            "blossom", "mint", "lavender"),
    BABY_DEER(PlushieRarity.UNCOMMON, InteractAnimation.NOD, IdleAnimation.BREATHE, 10, 12, MapColor.COLOR_BROWN, 0,
            "fawn", "blossom", "frost"),
    HONEY_BEE(PlushieRarity.COMMON, InteractAnimation.BUZZ, IdleAnimation.HOVER, 9, 12, MapColor.COLOR_YELLOW, 0,
            "honey", "strawberry", "mint"),
    STRAWBERRY_COW(PlushieRarity.RARE, InteractAnimation.HOP, IdleAnimation.BREATHE, 10, 13, MapColor.COLOR_PINK, 0,
            "strawberry", "chocolate", "blueberry");

    public static final int VARIANT_COUNT = 4;
    public static final int SPARKLE = 3;
    public static final int TOTAL_VARIANTS = VARIANT_COUNT * 15;

    private final String id;
    private final PlushieRarity rarity;
    private final InteractAnimation interaction;
    private final IdleAnimation idle;
    private final float width;
    private final float height;
    private final MapColor mapColor;
    private final int light;
    private final String[] variants;

    PlushieSpecies(PlushieRarity rarity, InteractAnimation interaction, IdleAnimation idle, float width, float height,
                   MapColor mapColor, int light, String v0, String v1, String v2) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.rarity = rarity;
        this.interaction = interaction;
        this.idle = idle;
        this.width = width;
        this.height = height;
        this.mapColor = mapColor;
        this.light = light;
        this.variants = new String[]{v0, v1, v2, "sparkle"};
    }

    public String id() {
        return id;
    }

    public PlushieRarity rarity() {
        return rarity;
    }

    public PlushieRarity rarity(int variant) {
        return variant == SPARKLE ? PlushieRarity.SPARKLE : rarity;
    }

    public InteractAnimation interaction() {
        return interaction;
    }

    public IdleAnimation idle() {
        return idle;
    }

    /** Footprint width in pixels (1/16 block). */
    public float width() {
        return width;
    }

    /** Height in pixels (1/16 block). */
    public float height() {
        return height;
    }

    public MapColor mapColor() {
        return mapColor;
    }

    public String variantId(int variant) {
        return variants[clampVariant(variant)];
    }

    public int lightLevel(int variant) {
        return variant == SPARKLE ? Math.max(light, 5) : light;
    }

    public MutableComponent displayName() {
        return Component.translatable("block.sugarplumplushies." + id);
    }

    public MutableComponent variantName(int variant) {
        return Component.translatable("variant.sugarplumplushies." + id + "." + variantId(variant));
    }

    public MutableComponent description() {
        return Component.translatable("plushie.sugarplumplushies." + id + ".desc");
    }

    /**
     * The vanilla sound the plushie makes. Looked up by id: since 26.1 several mob sounds have per-variant events,
     * so a sound that is no longer registered falls back to a soft wool sound.
     */
    public SoundEvent sound() {
        String path = switch (this) {
            case STRAWBERRY_BUNNY, ANGEL_BUNNY -> "entity.rabbit.ambient";
            case PINK_TEDDY_BEAR, HEART_BEAR -> "entity.allay.item_taken";
            case LAVENDER_KITTY -> "entity.cat.ambient";
            case CLOUD_PUPPY -> "entity.wolf.pant";
            case CHERRY_BLOSSOM_FOX -> "entity.fox.ambient";
            case SLEEPY_PANDA -> "entity.panda.sneeze";
            case LITTLE_DUCK -> "entity.chicken.ambient";
            case MOON_CAT -> "entity.cat.purreow";
            case SUGARPLUM_DRAGON -> "entity.ender_dragon.flap";
            case PASTEL_AXOLOTL -> "entity.axolotl.idle_air";
            case BABY_DEER -> "entity.goat.ambient";
            case HONEY_BEE -> "entity.bee.pollinate";
            case STRAWBERRY_COW -> "entity.cow.ambient";
        };
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(path));
        return event != null ? event : BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace("block.wool.hit"));
    }

    public float pitch() {
        return switch (this) {
            case STRAWBERRY_BUNNY, ANGEL_BUNNY -> 1.6F;
            case LITTLE_DUCK -> 1.9F;
            case BABY_DEER, STRAWBERRY_COW -> 1.7F;
            case SUGARPLUM_DRAGON -> 1.8F;
            case CLOUD_PUPPY, CHERRY_BLOSSOM_FOX, LAVENDER_KITTY -> 1.4F;
            case PINK_TEDDY_BEAR, HEART_BEAR -> 1.25F;
            default -> 1.2F;
        };
    }

    public float volume() {
        return this == SUGARPLUM_DRAGON ? 0.45F : 0.8F;
    }

    public ParticleOptions interactParticle() {
        return switch (this) {
            case CHERRY_BLOSSOM_FOX -> ParticleTypes.CHERRY_LEAVES;
            case ANGEL_BUNNY, MOON_CAT, SUGARPLUM_DRAGON -> ModParticles.TWINKLE.get();
            case SLEEPY_PANDA -> ModParticles.SLEEPY_Z.get();
            case LITTLE_DUCK -> ParticleTypes.NOTE;
            case PASTEL_AXOLOTL -> ParticleTypes.BUBBLE_POP;
            case BABY_DEER -> ParticleTypes.HAPPY_VILLAGER;
            case HONEY_BEE -> ParticleTypes.FALLING_HONEY;
            case CLOUD_PUPPY -> ParticleTypes.CLOUD;
            default -> ModParticles.PASTEL_HEART.get();
        };
    }

    public int particleCount() {
        return switch (this) {
            case HEART_BEAR -> 12;
            case CHERRY_BLOSSOM_FOX -> 10;
            case SLEEPY_PANDA -> 3;
            case CLOUD_PUPPY -> 5;
            default -> 6;
        };
    }

    public double particleSpeed() {
        return switch (this) {
            case CLOUD_PUPPY -> 0.01D;
            case LITTLE_DUCK -> 1.0D;
            default -> 0.02D;
        };
    }

    /** A second, species-specific flourish on cuddle (may be null). */
    @Nullable
    public ParticleOptions secondaryParticle() {
        return switch (this) {
            case SUGARPLUM_DRAGON -> ParticleTypes.DRAGON_BREATH;
            case ANGEL_BUNNY -> ParticleTypes.END_ROD;
            case HONEY_BEE, STRAWBERRY_COW, CLOUD_PUPPY -> ModParticles.PASTEL_HEART.get();
            case PASTEL_AXOLOTL -> ParticleTypes.SPLASH;
            default -> null;
        };
    }

    @Nullable
    public ParticleOptions ambientParticle() {
        return switch (this) {
            case MOON_CAT, ANGEL_BUNNY -> ModParticles.TWINKLE.get();
            case SLEEPY_PANDA -> ModParticles.SLEEPY_Z.get();
            case CHERRY_BLOSSOM_FOX -> ParticleTypes.CHERRY_LEAVES;
            case HONEY_BEE -> ParticleTypes.FALLING_NECTAR;
            default -> null;
        };
    }

    public float ambientChance() {
        return switch (this) {
            case MOON_CAT -> 0.45F;
            case SLEEPY_PANDA -> 0.3F;
            case CHERRY_BLOSSOM_FOX -> 0.2F;
            default -> 0.12F;
        };
    }

    public static int clampVariant(int variant) {
        return variant < 0 || variant >= VARIANT_COUNT ? 0 : variant;
    }

    @Nullable
    public static PlushieSpecies byId(String id) {
        for (PlushieSpecies species : values()) {
            if (species.id.equals(id)) {
                return species;
            }
        }
        return null;
    }
}
