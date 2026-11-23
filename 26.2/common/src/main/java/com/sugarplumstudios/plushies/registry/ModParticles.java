package com.sugarplumstudios.plushies.registry;

import com.sugarplumstudios.plushies.platform.RegistryEntry;
import com.sugarplumstudios.plushies.platform.Services;
import net.minecraft.core.registries.Registries;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

public final class ModParticles {

    public static final RegistryEntry<SimpleParticleType> PASTEL_HEART = Services.PLATFORM.register(Registries.PARTICLE_TYPE, "pastel_heart", () -> new SimpleParticleType(false) {
    });
    public static final RegistryEntry<SimpleParticleType> TWINKLE = Services.PLATFORM.register(Registries.PARTICLE_TYPE, "twinkle", () -> new SimpleParticleType(false) {
    });
    public static final RegistryEntry<SimpleParticleType> SLEEPY_Z = Services.PLATFORM.register(Registries.PARTICLE_TYPE, "sleepy_z", () -> new SimpleParticleType(false) {
    });

    private ModParticles() {
    }

    /** Loads the class, which registers everything above. */
    public static void init() {
    }
}
