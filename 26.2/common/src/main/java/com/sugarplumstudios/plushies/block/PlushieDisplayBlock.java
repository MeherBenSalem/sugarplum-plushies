package com.sugarplumstudios.plushies.block;

import com.mojang.serialization.MapCodec;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import com.sugarplumstudios.plushies.item.PlushieItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Base for furniture that shows plushie items: shelves, stands, beds and cabinets.
 * Right-click with a plushie to display it, right-click empty-handed to cuddle it,
 * sneak + right-click empty-handed to take it back.
 */
public abstract class PlushieDisplayBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    /** Where a plushie sits, in north-facing pixel coordinates. */
    public record SlotPose(float x, float y, float z, float scale, float lean) {
    }

    private final Map<Direction, VoxelShape> shapes;
    private final Map<Direction, VoxelShape> collisionShapes;

    protected PlushieDisplayBlock(Properties properties, VoxelShape northShape, VoxelShape northCollision) {
        super(properties);
        this.shapes = ShapeUtil.horizontal(northShape);
        this.collisionShapes = ShapeUtil.horizontal(northCollision);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public abstract int slotCount();

    public abstract SlotPose slotPose(int slot);

    /** Picks the slot for a click, given north-frame block-local coordinates. */
    protected int slotForHit(double x, double y, double z) {
        return 0;
    }

    /** Whether displayed plushies slowly turn (display stand). */
    public boolean spins() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return collisionShapes.get(state.getValue(FACING));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit) {
        return UseBridge.toItem(use(state, level, pos, player, hand, hit));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof PlushieDisplayBlockEntity display)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        double[] north = ShapeUtil.toNorthFrame(state.getValue(FACING), local.x, local.z);
        int slot = Mth.clamp(slotForHit(north[0], local.y, north[1]), 0, slotCount() - 1);

        if (held.getItem() instanceof PlushieItem) {
            int target = display.getItem(slot).isEmpty() ? slot : display.firstEmptySlot();
            if (target < 0) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                display.setItem(target, held.copyWithCount(1));
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.2F);
            }
            return InteractionResult.SUCCESS;
        }

        if (spins() && held.is(Items.BRUSH)) {
            if (!level.isClientSide()) {
                display.toggleSpinning();
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 0.8F, 1.3F);
            }
            return InteractionResult.SUCCESS;
        }

        if (hand != InteractionHand.MAIN_HAND || !held.isEmpty()) {
            return InteractionResult.PASS;
        }
        int occupied = display.getItem(slot).isEmpty() ? display.firstOccupiedSlot() : slot;
        if (occupied < 0) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, display.removeItem(occupied));
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.4F);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide() && display.tryCuddle(occupied, level.getGameTime())) {
            ItemStack plushie = display.getItem(occupied);
            if (plushie.getItem() instanceof PlushieItem item) {
                level.blockEvent(pos, this, PlushieInteractions.EVENT_CUDDLE, occupied);
                PlushieInteractions.react((ServerLevel) level, pos, slotWorldPosition(state, pos, occupied, item),
                        item.getSpecies(), PlushieItem.getVariant(plushie), player, plushie.getHoverName());
            }
        }
        return InteractionResult.SUCCESS;
    }

    private Vec3 slotWorldPosition(BlockState state, BlockPos pos, int slot, PlushieItem item) {
        SlotPose pose = slotPose(slot);
        double[] world = ShapeUtil.toWorld(state.getValue(FACING), pose.x() / 16.0D, pose.z() / 16.0D);
        double y = (pose.y() + item.getSpecies().height() * pose.scale() * 0.8D) / 16.0D;
        return new Vec3(pos.getX() + world[0], pos.getY() + y, pos.getZ() + world[1]);
    }

    // The shown plushies spill through PlushieDisplayBlockEntity.preRemoveSideEffects.
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof PlushieDisplayBlockEntity display) {
            return Math.round(15.0F * display.occupiedCount() / slotCount());
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlushieDisplayBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends PlushieDisplayBlock> codec() {
        return MapCodec.unit(this);
    }
}
