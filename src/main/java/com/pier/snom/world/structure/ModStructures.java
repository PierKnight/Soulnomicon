package com.pier.snom.world.structure;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModStructures
{
    public static final Structure<NoFeatureConfig> TEST_STRUCTURE = new TestStructure(NoFeatureConfig::deserialize);

    public static final IStructurePieceType TEST_PIECE = IStructurePieceType.register(TestStructurePieces::new, "SOS");

    public static void init(RegistryEvent.Register<Feature<?>> event)
    {
        event.getRegistry().register(TEST_STRUCTURE);
    }

    public static void addStructure()
    {
        for (Biome biome : ForgeRegistries.BIOMES)
        {
          //  biome.addStructure(TEST_STRUCTURE);
          //  biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Biome.createDecoratedFeature(TEST_STRUCTURE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
        }
    }

}
