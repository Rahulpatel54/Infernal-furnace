package com.yourname.infernal_furnace.block;

import com.yourname.infernal_furnace.InfernalFurnaceMod;
import com.yourname.infernal_furnace.block.entity.InfernalFurnaceBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;

public class InfernalFurnaceBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public InfernalFurnaceBlock(Settings settings) {
        super(settings);
        // Default blockstate: facing north, unlit
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    // Place facing toward the player (vanilla furnace behavior)
    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    // Handle right-click — ignite with Flint & Steel, extinguish with shovel
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ItemActionResult.SUCCESS;

        boolean lit = state.get(LIT);

        // Ignite with Flint & Steel
        if (!lit && stack.isOf(Items.FLINT_AND_STEEL)) {
            world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            // Spawn flame particles on the front face
            Direction facing = state.get(FACING);
            double fx = pos.getX() + 0.5 + facing.getOffsetX() * 0.5;
            double fy = pos.getY() + 0.5;
            double fz = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.5;
            for (int i = 0; i < 5; i++) {
                world.addParticle(ParticleTypes.FLAME,
                        fx + (world.random.nextDouble() - 0.5) * 0.3,
                        fy + (world.random.nextDouble() - 0.5) * 0.3,
                        fz + (world.random.nextDouble() - 0.5) * 0.3,
                        0, 0.05, 0);
            }
            // Damage the Flint & Steel
            stack.damage(1, player,
                    hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return ItemActionResult.SUCCESS;
        }

        // Extinguish with any shovel
        if (lit && stack.isIn(ItemTags.SHOVELS)) {
            world.setBlockState(pos, state.with(LIT, false), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f);
            // Spawn smoke particles
            for (int i = 0; i < 8; i++) {
                world.addParticle(ParticleTypes.LARGE_SMOKE,
                        pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 0.6,
                        pos.getY() + 0.8 + world.random.nextDouble() * 0.3,
                        pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 0.6,
                        0, 0.05, 0);
            }
            return ItemActionResult.SUCCESS;
        }

        // Otherwise open the furnace GUI
        if (!player.isSneaking()) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
                return ItemActionResult.SUCCESS;
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                  BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        if (!player.isSneaking()) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(InfernalFurnaceBlock::new);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfernalFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                    BlockEntityType<T> type) {
        if (world.isClient) return null;
        return validateTicker(type, InfernalFurnaceMod.INFERNAL_FURNACE_BLOCK_ENTITY,
                (w, pos, s, be) -> InfernalFurnaceBlockEntity.tick(w, pos, s, be));
    }
}
