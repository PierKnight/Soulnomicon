package com.pier.snom.init;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.tile.DungeonButtonTile;
import com.pier.snom.tile.StartPedestalTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;

public class ModTiles
{

    public static final TileEntityType<?> START_PEDESTAL_TYPE = TileEntityType.Builder.create(StartPedestalTile::new, ModBlocks.DUNGEON_START_PEDESTAL).build(null).setRegistryName(SoulnomiconMain.ID,"start_pedestal_dungeon");
    public static final TileEntityType<?> DUNGEON_BUTTON_TYPE = TileEntityType.Builder.create(DungeonButtonTile::new, ModBlocks.DUNGEON_BUTTON).build(null).setRegistryName(SoulnomiconMain.ID,"dungeon_button");

    public static void registerTiles(IForgeRegistry<TileEntityType<?>> registry)
    {
        registry.register(START_PEDESTAL_TYPE);
        registry.register(DUNGEON_BUTTON_TYPE);
    }
}
