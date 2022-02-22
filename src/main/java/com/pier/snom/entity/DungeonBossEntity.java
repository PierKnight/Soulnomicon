package com.pier.snom.entity;

import com.pier.snom.SoulnomiconMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class DungeonBossEntity extends MonsterEntity
{

    public int currentAttackIndex = -1;
    public int prevHurtTime = 0;
    public final EntityAnimationData<? extends DungeonBossEntity>[] ANIMATIONS;


    protected DungeonBossEntity(EntityType<? extends MonsterEntity> entityType, World world)
    {
        super(entityType, world);
        ANIMATIONS = getAnimations();

    }

    @Override
    public void livingTick()
    {
        this.prevHurtTime = hurtTime;
        super.livingTick();
        for (EntityAnimationData<?> animationData : ANIMATIONS)
            animationData.update(this.world);
    }

    public static final int CANCEL = -1;

    public void playAnimation(int index)
    {
        if(world.isRemote)
            return;

        if(index >= ANIMATIONS.length)
        {
            SoulnomiconMain.LOGGER.warn("Can't Play Attack #" + index + " for entity: " + this.getType().getRegistryName());
            return;
        }

        if(index == CANCEL && currentAttackIndex != -1)
        {
            ANIMATIONS[currentAttackIndex].cancelPlay();
            currentAttackIndex = -1;
            world.setEntityState(this, (byte) CANCEL);
            return;
        }

        if(ANIMATIONS[index] instanceof IAnimationAttack)
            currentAttackIndex = index;
        ANIMATIONS[index].play();
        this.world.setEntityState(this, (byte) (50 + index));
    }


    public IAnimationAttack getCurrentAttack()
    {
        if(currentAttackIndex != -1)
            return (IAnimationAttack) this.ANIMATIONS[currentAttackIndex];
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id)
    {


        if(id == CANCEL && currentAttackIndex != -1)
        {
            ANIMATIONS[currentAttackIndex].cancelPlay();
            currentAttackIndex = -1;
        }

        //if status update is not called regarding attacks just behave like normal
        //regarding attacks means if the id is not between 50 and 50 + totalAnimation - 1 (inclusive)
        if(id >= 50 && id < 50 + ANIMATIONS.length)
        {
            int index = id - 50;
            if(ANIMATIONS[index] instanceof IAnimationAttack)
                currentAttackIndex = index;
            ANIMATIONS[index].play();
            return;
        }
        super.handleStatusUpdate(id);
    }

    protected abstract EntityAnimationData<? extends DungeonBossEntity>[] getAnimations();


}
