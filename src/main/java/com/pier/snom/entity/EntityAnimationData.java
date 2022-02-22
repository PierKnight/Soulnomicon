package com.pier.snom.entity;

import com.pier.snom.utils.AnimationData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class EntityAnimationData<T extends DungeonBossEntity> extends AnimationData
{
    protected final T entity;

    public EntityAnimationData(T entity)
    {
        this.entity = entity;
    }

    public void update(World world)
    {
        super.update();
    }
    @Nullable
    protected LivingEntity getTarget()
    {
        if(!entity.world.isRemote)
            return entity.getAttackTarget();
        return null;
    }

    public void cancelPlay(){}
}
