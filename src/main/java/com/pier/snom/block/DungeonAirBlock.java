package com.pier.snom.block;

import com.pier.snom.SoulnomiconMain;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class DungeonAirBlock extends Block
{
    public DungeonAirBlock()
    {
        super(Block.Properties.create(Material.GLASS).hardnessAndResistance(-1, 99999F).sound(SoundType.METAL).notSolid().noDrops());
        this.setRegistryName(SoulnomiconMain.ID, "dungeon_air");
    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos blockPos, BlockState p_220082_4_, boolean e)
    {
        world.getPendingBlockTicks().scheduleTick(blockPos, this, 100 + world.rand.nextInt(200), TickPriority.HIGH);

    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {

        if(world.isRaining())
        {
            if(world.isAreaLoaded(pos, 1))
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        else
            world.getPendingBlockTicks().scheduleTick(pos, this, 100 + world.rand.nextInt(200), TickPriority.HIGH);
    }

    @Override
    public int getOpacity(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_)
    {
        return super.getOpacity(p_200011_1_, p_200011_2_, p_200011_3_);
    }

    @Override
    public PushReaction getPushReaction(BlockState p_149656_1_)
    {
        return PushReaction.BLOCK;
    }

    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_)
    {
        if(p_220071_4_.getEntity() != null)
            if(p_220071_4_.getEntity().isSneaking())
                return VoxelShapes.empty();
        return VoxelShapes.fullCube();
    }
}
