package com.pier.snom.world.structure.sections;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;

import java.util.List;

public class IronGolemSection extends MobRoomSection<IronGolemEntity>
{


    public IronGolemSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    @Override
    public Class<IronGolemEntity> getEntityClass()
    {
        return IronGolemEntity.class;
    }

    @Override
    public IronGolemEntity getRoomEntity(World world)
    {
        return new IronGolemEntity(EntityType.IRON_GOLEM, world);
    }

    @Override
    public boolean updateCheck(World world, List<PlayerEntity> players)
    {
        List<IronGolemEntity> ironGolems = world.getEntitiesWithinAABB(IronGolemEntity.class, getRoomBox());
        setTargets(ironGolems, players);
        return super.updateCheck(world, players);
    }

    @Override
    public String getBossPhrase()
    {
        return "dungeon.boss.irongolems";
    }

    @Override
    public void startWave(World world, List<PlayerEntity> players, int wave)
    {
        spawnEntity(world, 4, 1, 24 - (wave * 7));
        spawnEntity(world, 14, 1, 24 - (wave * 7));

    }

    @Override
    public int getTotalWaves()
    {
        return 2;
    }

    private void setTargets(List<IronGolemEntity> golems, List<PlayerEntity> players)
    {

        for (IronGolemEntity golem : golems)
        {
            if(golem.getAttackTarget() == null)
            {
                players.forEach(player ->
                {
                    boolean canTarget = players.size() == 1 || golems.stream().noneMatch(g -> player.equals(g.getAttackTarget()));

                    if(isPlayerInsideRoom(player) && EntityPredicate.DEFAULT.canTarget(golem, player) && canTarget)
                        golem.setAttackTarget(player);
                });
            }
        }

    }


}
