package com.pier.snom.world.structure.sections;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;

import java.util.List;

public class BlazeSection extends MobRoomSection<BlazeEntity>
{
    public BlazeSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    @Override
    public void startWave(World world, List<PlayerEntity> players, int wave)
    {
        this.spawnEntity(world,2,4,2);
        this.spawnEntity(world,14,4,14);
        this.spawnEntity(world,14,4,2);
        this.spawnEntity(world,2,4,14);
    }

    @Override
    public int getTotalWaves()
    {
        return 1;
    }

    @Override
    public Class<BlazeEntity> getEntityClass()
    {
        return BlazeEntity.class;
    }

    @Override
    public BlazeEntity getRoomEntity(World world)
    {
        return new BlazeEntity(EntityType.BLAZE,world);
    }

    @Override
    public String getBossPhrase()
    {
        return "dungeon.boss.blaze";
    }

}
