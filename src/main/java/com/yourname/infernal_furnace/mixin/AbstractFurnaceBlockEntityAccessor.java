package com.yourname.infernal_furnace.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {

    @Accessor("burnTime")
    int getBurnTime();

    @Accessor("burnTime")
    void setBurnTime(int burnTime);

    @Accessor("fuelTime")
    int getFuelTime();

    @Accessor("fuelTime")
    void setFuelTime(int fuelTime);
}