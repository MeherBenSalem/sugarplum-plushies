package com.sugarplumstudios.plushies.block;

import net.minecraft.util.Prediction;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.LevelReader;
import com.sugarplumstudios.plushies.block.entity.PlushieBlockEntity;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A placed plushie. It is drawn by {@code PlushieBlockEntityRenderer} so that it can
 * turn to any of 16 directions and play its cuddle/idle animations.
 */
public class PlushieBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, PlushieSpecies.VARIANT_COUNT - 1);
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final PlushieSpecies species;
    private final VoxelShape shape;

    public PlushieBlock(PlushieSpecies species) {
        super(ModBlocks.props(species.id())
                .mapColor(species.mapColor())
                .strength(0.4F)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .ignitedByLava()
                .pushReaction(PushReaction.POPPED)
                .lightLevel(state -> species.lightLevel(state.getValue(VARIANT))));
        this.species = species;
        float half = species.width() / 2.0F;
        this.shape = Block.box(8.0F - half, 0.0D, 8.0F - half, 8.0F + half, Math.min(16.0F, species.height()), 8.0F + half);
        registerDefaultState(stateDefinition.any().setValue(VARIANT, 0).setValue(ROTATION, 0).setValue(WATERLOGGED, false));
    }

    public PlushieSpecies getSpecies() {
        return species;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT, ROTATION, WATERLOGGED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()))
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME) && level.getBlockEntity(pos) instanceof PlushieBlockEntity plushie) {
            plushie.setCustomName(stack.getHoverName());
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit) {
        return UseBridge.toItem(use(state, level, pos, player, hand, hit));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (held.is(Items.NAME_TAG) && held.has(DataComponents.CUSTOM_NAME)) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PlushieBlockEntity plushie) {
                plushie.setCustomName(held.getHoverName());
                if (!player.getAbilities().instabuild && PlushieConfig.COMMON.consumeNameTags.get()) {
                    held.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.4F);
                Vec3 at = topCenter(pos);
                ((ServerLevel) level).sendParticles(ModParticles.PASTEL_HEART.get(), at.x, at.y, at.z, 5, 0.25D, 0.15D, 0.25D, 0.0D);
            }
            return InteractionResult.SUCCESS;
        }

        if (held.is(Items.BRUSH)) {
            if (!level.isClientSide()) {
                int step = player.isShiftKeyDown() ? -1 : 1;
                level.setBlock(pos, state.setValue(ROTATION, (state.getValue(ROTATION) + step) & 15), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 0.8F, 1.3F);
            }
            return InteractionResult.SUCCESS;
        }

        if (hand != InteractionHand.MAIN_HAND || !held.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown() && PlushieConfig.COMMON.sneakPickUp.get()) {
            if (!level.isClientSide()) {
                ItemStack stack = PlushieItem.create(species, state.getValue(VARIANT));
                if (level.getBlockEntity(pos) instanceof PlushieBlockEntity plushie && plushie.hasCustomName()) {
                    stack.set(DataComponents.CUSTOM_NAME, plushie.getCustomName());
                }
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.4F);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false, Prediction.SERVER_ONLY);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PlushieBlockEntity plushie
                && plushie.tryCuddle(level.getGameTime())) {
            level.blockEvent(pos, this, PlushieInteractions.EVENT_CUDDLE, 0);
            Vec3 at = Vec3.atBottomCenterOf(pos).add(0.0D, species.height() / 16.0D * 0.8D, 0.0D);
            PlushieInteractions.react((ServerLevel) level, pos, at, species, state.getValue(VARIANT), player, plushie.getDisplayName());
        }
        return InteractionResult.SUCCESS;
    }

    private Vec3 topCenter(BlockPos pos) {
        return Vec3.atBottomCenterOf(pos).add(0.0D, species.height() / 16.0D, 0.0D);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return PlushieItem.create(species, state.getValue(VARIANT));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!PlushieConfig.CLIENT.ambientParticles.get()) {
            return;
        }
        int variant = state.getValue(VARIANT);
        ParticleOptions ambient = species.ambientParticle();
        float chance = species.ambientChance();
        if (species == PlushieSpecies.MOON_CAT && level.isDarkOutside()) {
            chance *= 2.0F;
        }
        if (ambient != null && random.nextFloat() < chance) {
            spawnAround(level, pos, random, ambient);
        }
        if (variant == PlushieSpecies.SPARKLE && random.nextFloat() < 0.35F) {
            spawnAround(level, pos, random, ModParticles.TWINKLE.get());
        }
    }

    private void spawnAround(Level level, BlockPos pos, RandomSource random, ParticleOptions particle) {
        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * species.width() / 16.0D;
        double y = pos.getY() + species.height() / 16.0D * (0.55D + random.nextDouble() * 0.5D);
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * species.width() / 16.0D;
        level.addParticle(particle, x, y, z, 0.0D, 0.01D, 0.0D);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlushieBlockEntity(pos, state);
    }

}
