package com.sugarplumstudios.plushies.block.entity;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.HolderLookup;
import com.sugarplumstudios.plushies.block.PlushieInteractions;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PlushieBlockEntity extends BlockEntity implements Nameable {
    @Nullable
    private Component customName;
    private long lastCuddle = Long.MIN_VALUE / 2;
    private long animationStart = Long.MIN_VALUE / 2;

    public PlushieBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLUSHIE.get(), pos, state);
    }

    /** Server-side cooldown so plushies can't be spammed. */
    public boolean tryCuddle(long gameTime) {
        if (gameTime - lastCuddle < PlushieConfig.COMMON.interactionCooldown.get()) {
            return false;
        }
        lastCuddle = gameTime;
        return true;
    }

    /** Progress of the current cuddle animation in [0, 1), or -1 when not animating. */
    public float cuddleProgress(float partialTick, int duration) {
        if (level == null) {
            return -1.0F;
        }
        float age = (level.getGameTime() - animationStart) + partialTick;
        return age >= 0.0F && age < duration ? age / duration : -1.0F;
    }

    /** Per-plushie offset so neighbouring plushies don't breathe in lock-step. */
    public float idleOffset() {
        return (worldPosition.hashCode() & 1023) * 0.37F;
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == PlushieInteractions.EVENT_CUDDLE) {
            if (level != null && level.isClientSide()) {
                animationStart = level.getGameTime();
            }
            return true;
        }
        return super.triggerEvent(id, param);
    }

    public void setCustomName(@Nullable Component name) {
        customName = name;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return customName;
    }

    @Override
    public Component getName() {
        return customName != null ? customName : getBlockState().getBlock().getName();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("CustomName", ComponentSerialization.CODEC, customName);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        customName = input.read("CustomName", ComponentSerialization.CODEC).orElse(null);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
