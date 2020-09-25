package com.pier.snom.world.structure;

import com.mojang.datafixers.Dynamic;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.world.save.DungeonDataSave;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.function.Function;

public class DungeonStructure extends Structure<NoFeatureConfig>
{
    public DungeonStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
        this.setRegistryName(SoulnomiconMain.ID, "dungeon");
    }

    /**
     * decide whether the Structure can be generated
     */
    public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn)
    {
        ChunkPos chunkpos = this.getStartPositionForPosition(generatorIn, randIn, chunkX, chunkZ, 0, 0);
        return chunkX == chunkpos.x && chunkZ == chunkpos.z && generatorIn.hasStructure(biomeIn, this) && randIn.nextDouble() < 0.002D;
     }

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

    @Override
    public int getSize()
    {
        return 2;
    }

    public static class Start extends StructureStart
    {

        public Start(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
        {
            super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn)
        {
            int i = chunkX * 16;
            int j = chunkZ * 16;
            DungeonStructurePieces.Start start = new DungeonStructurePieces.Start(this.rand, i, j);
            this.components.add(start);
            start.buildComponent(start,components,rand);
            this.recalculateStructureSize();
            start.setDungeonBoundingBox(this.bounds);

        }

        @Override
        public void generateStructure(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_)
        {

            DungeonStructurePieces.Start start = (DungeonStructurePieces.Start) this.components.get(0);
            DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(p_225565_1_.getWorld());
            if(dungeonDataSave != null)
                dungeonDataSave.addNewDungeon(start.dungeonUUID,start.dungeonBoundingBox,start.roomSections,start.dungeonStartPos);

            super.generateStructure(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_);
        }
    }
}
