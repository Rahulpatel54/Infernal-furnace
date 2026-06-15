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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;

public class InfernalFurnaceBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public InfernalFurnaceBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    // ── Particles & sounds (client-side, called every tick while chunk is loaded) ──
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) return;

        Direction facing = state.get(FACING);

        // Centre of the block
        double cx = pos.getX() + 0.5;
        double cy = pos.getY();
        double cz = pos.getZ() + 0.5;

        // Offset toward the front face (furnace mouth)
        double fx = cx + facing.getOffsetX() * 0.52;
        double fz = cz + facing.getOffsetZ() * 0.52;

        // ── Smoke rising from the top (matches vanilla furnace) ──
        world.addParticle(ParticleTypes.SMOKE,
                cx + (random.nextDouble() - 0.5) * 0.4,
                cy + 1.1,
                cz + (random.nextDouble() - 0.5) * 0.4,
                0, 0.07, 0);

        // Occasional thick smoke puff
        if (random.nextInt(3) == 0) {
            world.addParticle(ParticleTypes.LARGE_SMOKE,
                    cx + (random.nextDouble() - 0.5) * 0.3,
                    cy + 1.05,
                    cz + (random.nextDouble() - 0.5) * 0.3,
                    0, 0.05, 0);
        }

        // ── Flame flickers from the furnace mouth ──
        world.addParticle(ParticleTypes.FLAME,
                fx + (random.nextDouble() - 0.5) * 0.15,
                cy + 0.45 + random.nextDouble() * 0.2,
                fz + (random.nextDouble() - 0.5) * 0.15,
                0, 0.02, 0);

        // ── Ash / ember pops (netherrack flavour) ──
        if (random.nextInt(5) == 0) {
            world.addParticle(ParticleTypes.ASH,
                    cx + (random.nextDouble() - 0.5) * 0.6,
                    cy + 0.9 + random.nextDouble() * 0.3,
                    cz + (random.nextDouble() - 0.5) * 0.6,
                    (random.nextDouble() - 0.5) * 0.02,
                    0.02,
                    (random.nextDouble() - 0.5) * 0.02);
        }

        // ── Rare lava pop (ember spitting from netherrack base) ──
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.LAVA,
                    fx + (random.nextDouble() - 0.5) * 0.2,
                    cy + 0.3,
                    fz + (random.nextDouble() - 0.5) * 0.2,
                    0, 0, 0);
        }

        // ── Ambient crackling sound (same cadence as vanilla furnace) ──
        if (random.nextInt(24) == 0) {
            world.playSound(
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                    SoundCategory.BLOCKS,
                    1.0f, 1.0f, false);
        }
    }

    // ── Right-click: ignite / extinguish / open GUI ──
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ItemActionResult.SUCCESS;

        boolean lit = state.get(LIT);

        // Ignite with Flint & Steel
        if (!lit && stack.isOf(Items.FLINT_AND_STEEL)) {
            world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
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
            stack.damage(1, player,
                    hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return ItemActionResult.SUCCESS;
        }

        // Extinguish with any shovel
        if (lit && stack.isIn(ItemTags.SHOVELS)) {
            world.setBlockState(pos, state.with(LIT, false), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f);
            for (int i = 0; i < 8; i++) {
                world.addParticle(ParticleTypes.LARGE_SMOKE,
                        pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 0.6,
                        pos.getY() + 0.8 + world.random.nextDouble() * 0.3,
                        pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 0.6,
                        0, 0.05, 0);
            }
            return ItemActionResult.SUCCESS;
        }

        // Open GUI
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