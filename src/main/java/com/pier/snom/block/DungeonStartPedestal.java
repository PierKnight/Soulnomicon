package com.pier.snom.block;

import com.pier.snom.tile.StartPedestalTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DungeonStartPedestal extends DungeonBlock
{
    public DungeonStartPedestal()
    {
        super("dungeon_pedestal");
    }


    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof StartPedestalTile && !worldIn.isRemote && worldIn.getDifficulty() != Difficulty.PEACEFUL)
        {
            StartPedestalTile pedestalTile = (StartPedestalTile) tileEntity;
            pedestalTile.addNewChallengers(player);
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new StartPedestalTile();
    }
}
