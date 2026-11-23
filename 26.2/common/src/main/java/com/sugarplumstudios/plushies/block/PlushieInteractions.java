package com.sugarplumstudios.plushies.block;

import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** Shared server-side reaction when a plushie (placed or on display) is cuddled. */
public final class PlushieInteractions {
    /** Block event id used to start the cuddle animation on clients. */
    public static final int EVENT_CUDDLE = 1;
    public static final int MESSAGE_COUNT = 3;

    private PlushieInteractions() {
    }

    public static void react(ServerLevel level, BlockPos pos, Vec3 at, PlushieSpecies species, int variant,
                             @Nullable Player player, Component name) {
        RandomSource random = level.getRandom();
        level.playSound(null, pos, species.sound(), SoundSource.BLOCKS, species.volume(),
                species.pitch() * (0.92F + random.nextFloat() * 0.16F));
        level.playSound(null, pos, SoundEvents.WOOL_HIT, SoundSource.BLOCKS, 0.5F, 1.5F);

        if (PlushieConfig.COMMON.interactionParticles.get()) {
            level.sendParticles(species.interactParticle(), at.x, at.y, at.z, species.particleCount(),
                    0.28D, 0.18D, 0.28D, species.particleSpeed());
            ParticleOptions secondary = species.secondaryParticle();
            if (secondary != null) {
                level.sendParticles(secondary, at.x, at.y + 0.1D, at.z, 3, 0.2D, 0.15D, 0.2D, 0.01D);
            }
            if (variant == PlushieSpecies.SPARKLE) {
                level.sendParticles(ModParticles.TWINKLE.get(), at.x, at.y, at.z, 8, 0.35D, 0.3D, 0.35D, 0.0D);
            }
        }

        if (player != null && PlushieConfig.COMMON.interactionMessages.get()) {
            player.displayClientMessage(Component.translatable(
                    "message.sugarplumplushies." + species.id() + "." + random.nextInt(MESSAGE_COUNT), name)
                    .withStyle(style -> style.withColor(0xFFB3D6)), true);
        }
    }
}
