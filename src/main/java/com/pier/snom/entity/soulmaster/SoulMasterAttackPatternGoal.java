package com.pier.snom.entity.soulmaster;

import com.pier.snom.entity.DungeonBossEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class SoulMasterAttackPatternGoal extends Goal
{
    int currentIndex = 0;
    private final SoulMasterEntity dungeonBoss;

    private LivingEntity target;


    public SoulMasterAttackPatternGoal(SoulMasterEntity dungeonBoss)
    {
        this.dungeonBoss = dungeonBoss;
    }

    @Override
    public boolean shouldExecute()
    {
        return dungeonBoss.getAttackTarget() != null && dungeonBoss.getCurrentAttack() == null;
    }

    @Override
    public void startExecuting()
    {
        target = dungeonBoss.getAttackTarget();
        // dungeonBoss.playAnimation(dungeonBoss.getRNG().nextInt(dungeonBoss.ATTACKS.length));
        if(currentIndex >= SoulMasterEntity.ATTACKS.length)
            currentIndex = 0;
        dungeonBoss.playAnimation(currentIndex++);
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return !dungeonBoss.getCurrentAttack().isFinished() && target.isAlive();
    }

    @Override
    public void resetTask()
    {
        dungeonBoss.playAnimation(DungeonBossEntity.CANCEL);
        if(!target.isAlive())
            currentIndex = 0;
    }
}
