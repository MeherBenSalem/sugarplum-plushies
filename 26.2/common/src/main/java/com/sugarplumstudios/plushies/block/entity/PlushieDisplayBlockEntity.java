package com.sugarplumstudios.plushies.block.entity;

import net.minecraft.world.Containers;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.HolderLookup;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.block.PlushieInteractions;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;

public class PlushieDisplayBlockEntity extends BlockEntity {
    private final NonNullList<ItemStack> items;
    private final long[] animationStart;
    private final long[] lastCuddle;
    private boolean spinning = true;

    public PlushieDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLUSHIE_DISPLAY.get(), pos, state);
        int size = state.getBlock() instanceof PlushieDisplayBlock display ? display.slotCount() : 1;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.animationStart = new long[size];
        this.lastCuddle = new long[size];
        Arrays.fill(animationStart, Long.MIN_VALUE / 2);
        Arrays.fill(lastCuddle, Long.MIN_VALUE / 2);
    }

    /** Spills the shown plushies when the display is broken. */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, items);
        }
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public int size() {
        return items.size();
    }

    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        markUpdated();
    }

    public ItemStack removeItem(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        markUpdated();
        return stack;
    }

    public int firstEmptySlot() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int firstOccupiedSlot() {
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int occupiedCount() {
        int count = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public void toggleSpinning() {
        spinning = !spinning;
        markUpdated();
    }

    public boolean tryCuddle(int slot, long gameTime) {
        if (gameTime - lastCuddle[slot] < PlushieConfig.COMMON.interactionCooldown.get()) {
            return false;
        }
        lastCuddle[slot] = gameTime;
        return true;
    }

    public float cuddleProgress(int slot, float partialTick, int duration) {
        if (level == null || slot < 0 || slot >= animationStart.length) {
            return -1.0F;
        }
        float age = (level.getGameTime() - animationStart[slot]) + partialTick;
        return age >= 0.0F && age < duration ? age / duration : -1.0F;
    }

    public float idleOffset(int slot) {
        return ((worldPosition.hashCode() + slot * 131) & 1023) * 0.37F;
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == PlushieInteractions.EVENT_CUDDLE) {
            if (level != null && level.isClientSide() && param >= 0 && param < animationStart.length) {
                animationStart[param] = level.getGameTime();
            }
            return true;
        }
        return super.triggerEvent(id, param);
    }

    private void markUpdated() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items, true);
        tag.putBoolean("Spinning", spinning);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(input, items);
        spinning = !tag.contains("Spinning") || tag.getBoolean("Spinning");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Forge's render bounds hook (not an override on Fabric, where the default bounds are used). */
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(0.25D).expandTowards(0.0D, 0.5D, 0.0D);
    }
}
