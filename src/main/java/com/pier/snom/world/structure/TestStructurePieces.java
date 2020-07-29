package com.pier.snom.world.structure;

import com.pier.snom.SoulnomiconMain;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class TestStructurePieces extends TemplateStructurePiece
{
    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(SoulnomiconMain.ID, "test/sos");


    public TestStructurePieces(TemplateManager templateManager, CompoundNBT tag)
    {
        super(ModStructures.TEST_PIECE, tag);
        Template template = templateManager.getTemplateDefaulted(RESOURCE_LOCATION);
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(Rotation.CLOCKWISE_90).setMirror(Mirror.NONE).setCenterOffset(new BlockPos(1, 1, 1)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(template, this.templatePosition, placementsettings);
    }

    public TestStructurePieces(TemplateManager templateManager, BlockPos pos)
    {
        super(ModStructures.TEST_PIECE, 0);
        this.templatePosition = pos;
        Template template = templateManager.getTemplateDefaulted(RESOURCE_LOCATION);
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(Rotation.CLOCKWISE_90).setMirror(Mirror.NONE).setCenterOffset(new BlockPos(1, 1, 1)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(template, this.templatePosition, placementsettings);

    }

    /*
    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn)
    {
        int i = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.templatePosition.getX(), this.templatePosition.getZ());
        this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
        return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, chunkPosIn);
    }
    */



    @Override
    protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb)
    {

    }
}
