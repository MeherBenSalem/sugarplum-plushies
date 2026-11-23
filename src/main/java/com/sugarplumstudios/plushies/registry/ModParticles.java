package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SugarplumPlushies.MOD_ID);

    public static final RegistryObject<SimpleParticleType> PASTEL_HEART = PARTICLES.register("pastel_heart", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TWINKLE = PARTICLES.register("twinkle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SLEEPY_Z = PARTICLES.register("sleepy_z", () -> new SimpleParticleType(false));

    private ModParticles() {
    }
}
