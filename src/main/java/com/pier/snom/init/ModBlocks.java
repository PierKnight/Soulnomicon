package com.pier.snom.init;

import com.pier.snom.block.DungeonBlock;
import com.pier.snom.block.DungeonButton;
import com.pier.snom.block.DungeonStartPedestal;
import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
    public static Block DUNGEON_BRICKS = new DungeonBlock("dungeon_bricks");
    public static Block CHISELED_DUNGEON_BRICKS = new DungeonBlock("chiseled_dungeon_bricks");
    public static Block DUNGEON_BUTTON = new DungeonButton();
    public static Block DUNGEON_START_PEDESTAL = new DungeonStartPedestal();

    public static void registerBlocks(IForgeRegistry<Block> registryEvent)
    {
        registryEvent.register(DUNGEON_BRICKS);
        registryEvent.register(DUNGEON_START_PEDESTAL);
        registryEvent.register(CHISELED_DUNGEON_BRICKS);
        registryEvent.register(DUNGEON_BUTTON);
    }

}
