package com.sugarplumstudios.plushies.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.RandomSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/** Floating pastel hearts, twinkling stars and sleepy Zzz's. */
public class PastelParticle extends SingleQuadParticle {
    public enum Kind {
        HEART, TWINKLE, SLEEPY
    }

    private final SpriteSet sprites;
    private final Kind kind;
    private final float baseSize;
    private final float swayPhase;

    protected PastelParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
                             SpriteSet sprites, Kind kind) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.kind = kind;
        this.hasPhysics = false;
        this.swayPhase = random.nextFloat() * Mth.TWO_PI;
        switch (kind) {
            case HEART -> {
                this.xd = xd * 0.15D + (random.nextDouble() - 0.5D) * 0.01D;
                this.yd = 0.025D + random.nextDouble() * 0.02D;
                this.zd = zd * 0.15D + (random.nextDouble() - 0.5D) * 0.01D;
                this.lifetime = 24 + random.nextInt(14);
                this.baseSize = 0.11F + random.nextFloat() * 0.05F;
                this.friction = 0.96F;
            }
            case TWINKLE -> {
                this.xd = (random.nextDouble() - 0.5D) * 0.006D;
                this.yd = 0.004D + random.nextDouble() * 0.006D;
                this.zd = (random.nextDouble() - 0.5D) * 0.006D;
                this.lifetime = 18 + random.nextInt(16);
                this.baseSize = 0.07F + random.nextFloat() * 0.06F;
                this.friction = 0.9F;
            }
            default -> {
                this.xd = 0.004D;
                this.yd = 0.018D + random.nextDouble() * 0.008D;
                this.zd = 0.0D;
                this.lifetime = 40 + random.nextInt(20);
                this.baseSize = 0.09F + random.nextFloat() * 0.04F;
                this.friction = 0.98F;
            }
        }
        this.quadSize = baseSize;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (!removed) {
            setSpriteFromAge(sprites);
            if (kind == Kind.SLEEPY) {
                xd += Mth.sin(age * 0.25F + swayPhase) * 0.0015D;
            }
            float life = (float) age / lifetime;
            setAlpha(life > 0.7F ? 1.0F - (life - 0.7F) / 0.3F : 1.0F);
        }
    }

    @Override
    public float getQuadSize(float partialTick) {
        float life = (age + partialTick) / lifetime;
        return switch (kind) {
            case TWINKLE -> baseSize * Mth.sin(Mth.clamp(life, 0.0F, 1.0F) * Mth.PI);
            case HEART -> baseSize * Mth.clamp(life * 6.0F, 0.0F, 1.0F);
            default -> baseSize * (0.6F + 0.6F * Mth.clamp(life, 0.0F, 1.0F));
        };
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return kind == Kind.SLEEPY ? super.getLightCoords(partialTick) : 0xF000F0;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites, Kind kind) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xd, double yd, double zd, RandomSource random) {
            return new PastelParticle(level, x, y, z, xd, yd, zd, sprites, kind);
        }
    }
}
