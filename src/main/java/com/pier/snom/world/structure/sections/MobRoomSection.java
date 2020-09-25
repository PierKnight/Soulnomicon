package com.pier.snom.world.structure.sections;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;

import java.util.List;

public abstract class MobRoomSection<T extends Entity> extends RoomSection
{

    private int wave = 0;

    public MobRoomSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    @Override
    public void onPlayersEnterRoom(World world, List<PlayerEntity> players)
    {
    }

    @Override
    public boolean updateCheck(World world, List<PlayerEntity> players)
    {
        if(!triggered)
        {
            if(arePlayersInsideRoom(players))
            {
                sendMessage(players, getBossPhrase());
                onPlayersEnterRoom(world, players);
                closeDoors(world, players, false);
                triggered = true;
                return true;
            }
        }
        else if(world.getEntitiesWithinAABB(getEntityClass(), getRoomBox()).isEmpty() && this.wave <= getTotalWaves())
        {

            if(isRoomComplete(world, players))
                this.completeRoom(world);
            else
                startWave(world, players, this.wave + 1);


            this.wave++;

            return true;
        }

        return false;

    }

    @Override
    public void onPlayersExitDungeon(World world)
    {
        this.wave = 0;

        for (T entity : world.getEntitiesWithinAABB(getEntityClass(), getRoomBox()))
            entity.remove();
    }

    @Override
    public boolean isRoomComplete(World world, List<PlayerEntity> players)
    {
        return this.wave >= getTotalWaves();
    }

    public void spawnEntity(World world, int x, int y, int z)
    {
        T entity = getRoomEntity(world);
        int wx = getXWithOffset(x, z);
        int wy = getYWithOffset(y);
        int wz = getZWithOffset(x, z);
        entity.setPosition(wx + 0.5D, wy, wz + 0.5D);
        world.addEntity(entity);
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        super.writeToNBT(tag);
        tag.putInt("wave", this.wave);
    }

    @Override
    public void readNBT(CompoundNBT tag)
    {
        super.readNBT(tag);
        this.wave = tag.getInt("wave");
    }


    public abstract void startWave(World world, List<PlayerEntity> players, int wave);

    public abstract int getTotalWaves();

    public abstract Class<T> getEntityClass();

    public abstract T getRoomEntity(World world);
}
