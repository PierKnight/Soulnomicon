package com.pier.snom.entity.soulmaster;

import com.pier.snom.entity.AttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class ArrowSummonGoal extends AttackGoal<SoulMasterEntity>
{
    private int arrowWaveDuration;
    private int cooldown = 0;
    private int totalCycles = 0;

    public ArrowSummonGoal(SoulMasterEntity soulMaster)
    {
        super(soulMaster);
        this.arrowWaveDuration = getArrowWaveDuration();
        this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    protected int getCooldown()
    {
        return 0;
    }

    @Override
    public void tick()
    {
        int animationProgress = this.boss.ARROW_SUMMONING.getCurrentTick();
        if(!this.boss.ARROW_SUMMONING.isStarted())
            this.boss.playAnimation(this.boss.ARROW_SUMMONING);
        else if(animationProgress >= 20)
        {
            if(this.cooldown > 0)
            {
                --this.cooldown;
            }
            else
            {
                if(this.arrowWaveDuration % 2 == 0)
                {
                    ArrowEntity arrowEntity = new ArrowEntity(world, boss);
                    Vector3d position = this.boss.getEyePosition(1.0F);
                    Vector3d targetPos = target.getEyePosition(1.0F).add(target.getMotion().scale(1.5D));
                    arrowEntity.setDamage(5D);
                    arrowEntity.setPierceLevel((byte) 3);
                    if(this.boss.isInSecondPhase())
                        arrowEntity.addEffect(new EffectInstance(Effects.WITHER,40,2));
                    arrowEntity.setPositionAndUpdate(position.x, position.y, position.z);

                    Vector3d direction = targetPos.subtract(position).normalize();
                    arrowEntity.shoot(direction.x * 2D, direction.y* 2D, direction.z* 2D, 2F, 1F);
                    arrowEntity.setIsCritical(true);
                    this.world.addEntity(arrowEntity);
                    this.world.playSound(null, position.x, position.y, position.z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.HOSTILE, 1F, 1.2F - this.boss.getRNG().nextFloat() * 0.4F);
                }
                if(--this.arrowWaveDuration == 0)
                {
                    this.cooldown = 20;
                    this.arrowWaveDuration = getArrowWaveDuration();
                    this.totalCycles++;
                }
            }
        }


    }


    @Override
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting() && this.totalCycles < 3;
    }

    @Override
    public void resetTask()
    {
        this.totalCycles = 0;
        this.arrowWaveDuration = getArrowWaveDuration();
        this.cooldown = 0;
        this.boss.playAnimation(this.boss.ARROW_SUMMONING);
        this.endAttack();

    }

    private int getArrowWaveDuration()
    {
        return this.boss.isInSecondPhase() ? 50 : 30;
    }
}
