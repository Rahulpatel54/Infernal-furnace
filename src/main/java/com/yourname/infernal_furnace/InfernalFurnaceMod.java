package com.yourname.infernal_furnace;

import net.minecraft.resource.featuretoggle.FeatureFlags;
import com.yourname.infernal_furnace.block.InfernalFurnaceBlock;
import com.yourname.infernal_furnace.block.entity.InfernalFurnaceBlockEntity;
import com.yourname.infernal_furnace.screen.InfernalFurnaceScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalFurnaceMod implements ModInitializer {

    public static ScreenHandlerType<InfernalFurnaceScreenHandler> INFERNAL_FURNACE_SCREEN_HANDLER;

    public static final String MOD_ID = "infernal_furnace";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Block
    public static final Block INFERNAL_FURNACE_BLOCK = new InfernalFurnaceBlock(
            AbstractBlock.Settings.copy(Blocks.BLAST_FURNACE)
                    .hardness(3.5f)
                    .resistance(3.5f)
                    .luminance(state -> state.get(InfernalFurnaceBlock.LIT) ? 13 : 0)
    );

    // Block Entity Type
    public static BlockEntityType<InfernalFurnaceBlockEntity> INFERNAL_FURNACE_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        INFERNAL_FURNACE_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of("infernal_furnace", "infernal_furnace"),
                new ScreenHandlerType<>(InfernalFurnaceScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
        // 1. Register Block
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "infernal_furnace"), INFERNAL_FURNACE_BLOCK);

        // 2. Register BlockItem
        BlockItem blockItem = new BlockItem(INFERNAL_FURNACE_BLOCK, new Item.Settings());
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "infernal_furnace"), blockItem);

        // 3. Register BlockEntityType
        INFERNAL_FURNACE_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(MOD_ID, "infernal_furnace"),
                FabricBlockEntityTypeBuilder.create(InfernalFurnaceBlockEntity::new, INFERNAL_FURNACE_BLOCK).build()
        );

        // 4. Add to creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(blockItem);
        });

        LOGGER.info("Infernal Furnace mod initialized — burns forever!");
    }
}
