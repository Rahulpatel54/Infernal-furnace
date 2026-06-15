package com.yourname.infernal_furnace.screen;

import com.yourname.infernal_furnace.InfernalFurnaceMod;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

public class InfernalFurnaceScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    // Client-side constructor
    public InfernalFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
    }

    // Server-side constructor
    public InfernalFurnaceScreenHandler(int syncId, PlayerInventory playerInventory,
                                         Inventory inventory, PropertyDelegate propertyDelegate) {
        super(InfernalFurnaceMod.INFERNAL_FURNACE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 3);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);

        // Slot 0 — input (top slot)
        this.addSlot(new Slot(inventory, 0, 56, 17));

        // Slot 1 (fuel) — SKIPPED intentionally

        // Slot 2 — output (right slot)
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addProperties(propertyDelegate);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // Output slot (1) → player inventory (2–37)
            if (slotIndex == 1) {
                if (!this.insertItem(originalStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(originalStack, newStack);
            }
            // Player inventory (2–37) → input slot (0)
            else if (slotIndex >= 2) {
                if (!this.insertItem(originalStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // Input slot (0) → player inventory (2–37)
            else {
                if (!this.insertItem(originalStack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    // Expose property delegate for the screen to read burn/cook progress
    public int getCookProgress() {
        int cookTime = this.propertyDelegate.get(2);
        int cookTimeTotal = this.propertyDelegate.get(3);
        return cookTimeTotal != 0 && cookTime != 0 ? cookTime * 24 / cookTimeTotal : 0;
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }
}