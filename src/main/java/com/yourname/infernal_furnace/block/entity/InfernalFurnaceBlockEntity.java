package com.yourname.infernal_furnace.block.entity;

import com.yourname.infernal_furnace.InfernalFurnaceMod;
import com.yourname.infernal_furnace.block.InfernalFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfernalFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    public InfernalFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(InfernalFurnaceMod.INFERNAL_FURNACE_BLOCK_ENTITY, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.infernal_furnace.infernal_furnace");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    // Return max fuel time so the furnace always appears to have fuel
    @Override
    protected int getFuelTime(ItemStack fuel) {
        return 32767;
    }

    // Server tick: keep burn time maxed when lit, sync lit state to blockstate
    public static void tick(World world, BlockPos pos, BlockState state, InfernalFurnaceBlockEntity be) {
        if (world.isClient) return;

        boolean isLit = state.get(InfernalFurnaceBlock.LIT);

        if (isLit) {
            // Keep burn time permanently full so it never runs out
            be.burnTime = 32767;
            be.fuelTime = 32767;
        } else {
            // Unlit — zero out burn time so it won't smelt
            be.burnTime = 0;
            be.fuelTime = 0;
        }

        // Let AbstractFurnaceBlockEntity handle the actual smelting logic
        // We call the parent tick only when lit — when unlit, nothing happens
        if (isLit) {
            AbstractFurnaceBlockEntity.tick(world, pos, state, be);
        } else {
            be.markDirty();
        }
    }

    // Make isBurning always return true when lit, so the GUI flame shows
    @Override
    public boolean isBurning() {
        return this.world != null
                && this.world.getBlockState(this.pos).contains(InfernalFurnaceBlock.LIT)
                && this.world.getBlockState(this.pos).get(InfernalFurnaceBlock.LIT);
    }
}
