package com.pier.snom.world.structure.sections;

import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TNTRoomSection extends RoomSection
{


    private final Random rand = new Random();
    private int explosiveTime = 0;


    public TNTRoomSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    @Override
    public boolean updateCheck(World world, List<PlayerEntity> players)
    {
        if(explosiveTime > 0)
        {
            if(world.getGameTime() % 12 == 0)
            {
                Vector3d tntPosition = this.getRoomBox().getCenter();
                TNTEntity tnt = new TNTEntity(world, tntPosition.x, tntPosition.y, tntPosition.z, null);
                tnt.setMotion(rand.nextDouble() - 0.5D,0.2D + rand.nextDouble() * 0.4D,rand.nextDouble() - 0.5D);
                world.addEntity(tnt);
                world.playSound(null,tntPosition.x,tntPosition.y,tntPosition.z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.AMBIENT,1F,1F);

                tnt.velocityChanged = true;
            }
            explosiveTime--;
            return true;
        }
        return super.updateCheck(world, players);
    }

    @Override
    public String getBossPhrase()
    {
        return "dungeon.boss.tnt";
    }

    @Override
    public void onPlayersEnterRoom(World world, List<PlayerEntity> players)
    {
        this.explosiveTime = 180;
    }

    @Override
    public void onPlayersExitDungeon(World world)
    {
        this.explosiveTime = 0;
        for (TNTEntity entity : world.getEntitiesWithinAABB(TNTEntity.class, getRoomBox()))
            entity.remove();
    }

    @Override
    public boolean isRoomComplete(World world, List<PlayerEntity> players)
    {
        return explosiveTime == 0;
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        super.writeToNBT(tag);
        tag.putInt("explosiveTime",this.explosiveTime);
    }

    @Override
    public void readNBT(CompoundNBT tag)
    {
        super.readNBT(tag);
        this.explosiveTime = tag.getInt("explosiveTime");
    }
}
