package com.pier.snom.entity;

import net.minecraft.world.World;

public class SingleAnimationData<T extends DungeonBossEntity> extends EntityAnimationData<T>
{
    private final int duration;
    public SingleAnimationData(T entity, int duration)
    {
        super(entity);
        this.duration = duration;
    }

    @Override
    public void play()
    {
        this.currentTick = duration;
    }

    @Override
    public void update(World world)
    {
        super.update(world);
        if(this.currentTick > 0)
            --this.currentTick;
    }

    @Override
    public float getProgress(float partialTicks)
    {
        return super.getProgress(partialTicks) / (float) duration;
    }

}
