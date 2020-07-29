package com.pier.snom.world.structure;

import com.mojang.datafixers.Dynamic;
import com.pier.snom.SoulnomiconMain;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.function.Function;

public class TestStructure extends Structure<NoFeatureConfig>
{
    public TestStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
        this.setRegistryName(SoulnomiconMain.ID, "wowowow");
    }

    /**
     * decide whether the Structure can be generated
     */
    public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
        ChunkPos chunkpos = this.getStartPositionForPosition(generatorIn, randIn, chunkX, chunkZ, 0, 0);
        return chunkX == chunkpos.x && chunkZ == chunkpos.z && generatorIn.hasStructure(biomeIn, this);
    }

    @Override
    public IStartFactory getStartFactory()
    {
        return TestStructure.Start::new;
    }

    @Override
    public String getStructureName()
    {
        return SoulnomiconMain.ID + ":wowowow";
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
            BlockPos blockpos = new BlockPos(i, 90, j);
            this.components.add(new TestStructurePieces(templateManagerIn, blockpos));
            this.recalculateStructureSize();

        }


    }
}
