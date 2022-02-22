package com.pier.snom.world.structure;

import com.mojang.serialization.Codec;
import com.pier.snom.SoulnomiconMain;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DungeonStructure extends Structure<NoFeatureConfig>
{

    public DungeonStructure(Codec<NoFeatureConfig> codec)
    {
        super(codec);
        this.setRegistryName(SoulnomiconMain.ID, "dungeon");
    }

    @Override
    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_)
    {
        return true;
    }

    /**
     * decide whether the Structure can be generated
     */



    @Override
    public IStartFactory getStartFactory()
    {
        return DungeonStructure.Start::new;
    }

    @Override
    public String getStructureName()
    {
        return SoulnomiconMain.ID + ":dungeon";
    }


    public static class Start extends StructureStart
    {

        public Start(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
        {
            super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }





        @Override
        public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, int x, int z, Biome biome, IFeatureConfig featureConfig)
        {

          //  DungeonStructurePieces.Start start = (DungeonStructurePieces.Start) this.components.get(0);
            //DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(chunkGenerator.get);
          //  if(dungeonDataSave != null)
           //     dungeonDataSave.addNewDungeon(start.dungeonUUID,start.dungeonBoundingBox,start.roomSections,start.dungeonStartPos);

        }
    }
}
