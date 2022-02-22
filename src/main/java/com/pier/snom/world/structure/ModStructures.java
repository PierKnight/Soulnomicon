package com.pier.snom.world.structure;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;

public class ModStructures
{
    public static final Structure<NoFeatureConfig> TEST_STRUCTURE = new DungeonStructure(NoFeatureConfig.field_236558_a_);

    /*
    public static final IStructurePieceType DUN_START = IStructurePieceType.register(DungeonStructurePieces.Start::new, "DST");
    public static final IStructurePieceType DUN_FIRST_ROOM = IStructurePieceType.register(DungeonStructurePieces.FirstRoom::new, "DFR");
    public static final IStructurePieceType DUN_TNT_ROOM = IStructurePieceType.register(DungeonStructurePieces.TNTRoom::new, "DTNTR");

    //mob rooms
    public static final IStructurePieceType DUN_SPIDER_ROOM = IStructurePieceType.register(DungeonStructurePieces.SpiderRoom::new, "DSPIR");
    public static final IStructurePieceType DUN_IRON_GOLEM_ROOM = IStructurePieceType.register(DungeonStructurePieces.IronGolemRoom::new, "DIGR");
    public static final IStructurePieceType DUN_BLAZE_ROOM = IStructurePieceType.register(DungeonStructurePieces.BlazeRoom::new, "DBLR");

    public static final IStructurePieceType DUN_ITEM_ROOM = IStructurePieceType.register(DungeonStructurePieces.ItemQuestionRoom::new, "DQIR");
    public static final IStructurePieceType DUN_NOTEBLOCK_ROOM = IStructurePieceType.register(DungeonStructurePieces.ItemQuestionRoom::new, "DNBR");

*/


    public static void init(RegistryEvent.Register<Structure<?>> event)
    {
       // event.getRegistry().register(TEST_STRUCTURE);
    }

}
