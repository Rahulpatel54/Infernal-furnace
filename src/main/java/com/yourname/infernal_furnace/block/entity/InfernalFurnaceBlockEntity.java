package com.yourname.infernal_furnace.block.entity;

import com.yourname.infernal_furnace.InfernalFurnaceMod;
import com.yourname.infernal_furnace.block.InfernalFurnaceBlock;
import com.yourname.infernal_furnace.mixin.AbstractFurnaceBlockEntityAccessor;
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
        AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) be;

        if (isLit) {
            // Let the parent do its smelting logic first (it decrements burnTime by 1)
            AbstractFurnaceBlockEntity.tick(world, pos, state, be);
            // Then immediately restore burn time to max so it never runs out
            accessor.setBurnTime(32767);
            accessor.setFuelTime(32767);
        } else {
            // Unlit — zero out burn time so smelting stops
            accessor.setBurnTime(0);
            accessor.setFuelTime(0);
            be.markDirty();
        }
    }
}
