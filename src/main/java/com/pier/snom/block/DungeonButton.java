package com.pier.snom.block;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.tile.DungeonButtonTile;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DungeonButton extends AbstractButtonBlock
{
    public DungeonButton()
    {
        super(true, Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(-1.0F, 3600000.0F).noDrops().sound(SoundType.WOOD));
        this.setRegistryName(SoulnomiconMain.ID, "dungeon_button");

    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(!state.get(POWERED))
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof DungeonButtonTile)
                ((DungeonButtonTile) tileEntity).onPress(worldIn,player);
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected SoundEvent getSoundEvent(boolean p_196369_1_)
    {
        return p_196369_1_ ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new DungeonButtonTile();
    }
}
