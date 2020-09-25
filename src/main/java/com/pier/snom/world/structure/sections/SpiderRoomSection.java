package com.pier.snom.world.structure.sections;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;

import java.util.List;

public class SpiderRoomSection extends MobRoomSection<SpiderEntity>
{


    public SpiderRoomSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    @Override
    public String getBossPhrase()
    {
        return "dungeon.boss.spiders";
    }

    @Override
    public void startWave(World world, List<PlayerEntity> players, int wave)
    {
        if(wave == 1)
        {
            for (int i = 0; i < 7; i++)
                this.spawnEntity(world, 3 + i * 2, 1, 16);
        }
        else
        {
            this.spawnEntity(world,3,1,3);
            this.spawnEntity(world,16,1,16);
            this.spawnEntity(world,3,1,16);
            this.spawnEntity(world,16,1,3);
            this.spawnEntity(world,9,1,9);
        }
    }

    @Override
    public int getTotalWaves()
    {
        return 2;
    }


    @Override
    public Class<SpiderEntity> getEntityClass()
    {
        return SpiderEntity.class;
    }

    @Override
    public SpiderEntity getRoomEntity(World world)
    {
        return world.rand.nextDouble() < 0.65D ? new SpiderEntity(EntityType.SPIDER, world) : new CaveSpiderEntity(EntityType.CAVE_SPIDER, world);
    }

}
