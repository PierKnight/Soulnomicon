package com.pier.snom.tile;

import com.pier.snom.init.ModTiles;
import com.pier.snom.world.save.DungeonDataSave;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public class StartPedestalTile extends TileEntity
{

    private UUID dungeonUUID;


    public StartPedestalTile()
    {
        super(ModTiles.START_PEDESTAL_TYPE);
    }


    public void addNewChallengers(PlayerEntity player)
    {
        DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(world);
        if(dungeonDataSave != null && dungeonUUID != null)
            dungeonDataSave.addNewChallengers(dungeonUUID, player);

    }

    public void setDungeonUUID(UUID dungeonUUID)
    {
        this.dungeonUUID = dungeonUUID;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT compound)
    {
        super.read(state,compound);
        if(compound.hasUniqueId("dungeonUUID"))
            this.dungeonUUID = compound.getUniqueId("dungeonUUID");
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound)
    {
        super.write(compound);
        if(dungeonUUID != null)
            compound.putUniqueId("dungeonUUID", dungeonUUID);


        return compound;
    }
}
