package com.pier.snom.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

public abstract class AttackGoal<T extends DungeonBossEntity> extends Goal
{
    protected World world;
    protected T boss;
    protected LivingEntity target;

    public AttackGoal(T boss)
    {
        this.boss = boss;
        this.world = this.boss.world;
    }

    @Override
    public boolean shouldExecute()
    {
        LivingEntity livingentity = this.boss.getAttackTarget();
        if(livingentity != null && livingentity.isAlive())
        {
            this.target = livingentity;
            return true;
        }
        else
        {
            return false;
        }
    }

    protected abstract int getCooldown();

    protected void endAttack()
    {
        this.boss.attackGoal = null;
        this.boss.cooldown = (int)(getCooldown() * cooldownMultiplier());
        this.target = null;
        this.boss.targetSelector.removeGoal(this);
    }

    protected double cooldownMultiplier()
    {
        return this.boss.isInSecondPhase() ? 0.5D : 1D;
    }
}
