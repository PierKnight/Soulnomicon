package com.pier.snom.entity.soulmaster;

import com.pier.snom.client.particle.ModParticles;
import com.pier.snom.entity.AttackGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class ArrowCageGoal extends AttackGoal<SoulMasterEntity>
{
    private final List<AbstractArrowEntity> arrows = new ArrayList<>();
    private final Map<UUID, Vector3d> arrowVelocities = new HashMap<>();
    private final Map<UUID, Vector3d> arrowStartPosition = new HashMap<>();

    private int summonTime = 0;
    private int waves = 0;

    public ArrowCageGoal(SoulMasterEntity boss)
    {
        super(boss);
    }

    @Override
    protected int getCooldown()
    {
        return 60;
    }

    private void spawnArrows()
    {
        this.boss.playAnimation(this.boss.HAND_COMMAND_ARROW);

        Vector3d pos = this.target.getPositionVec().subtract(10, 0, 10);
        int xSide = this.boss.getRNG().nextBoolean() ? 1 : 0;
        int zSide = 1 - xSide;
        int offset = this.boss.getRNG().nextBoolean() ? 1 : -1;

        int openingIndex1 = this.boss.getRNG().nextInt(20);
        int openingIndex2 = this.boss.getRNG().nextInt(20);

        double arrowSpeed = this.boss.isInSecondPhase() ? 0.65D : 0.5D;

        for (int i = 0; i < 20; i++)
        {
            if(i != openingIndex1 && i != openingIndex2)
                for (int j = 0; j < 2; j++)
                {
                    ArrowEntity arrow = new ArrowEntity(this.world, pos.x + (offset + 1) * 0.5D * 20 * xSide + i * zSide, pos.y + 0.1D + j * 2, pos.z + (offset + 1) * 0.5D * 20 * zSide + i * xSide);
                    arrow.setShooter(this.boss);
                    arrow.setNoGravity(true);
                    arrow.setDamage(10D);
                    arrow.setPierceLevel((byte) 3);
                    arrow.addEffect(getArrowEffect());
                    Vector3d direction = new Vector3d(xSide * -offset, 0, zSide * -offset);
                    arrow.setMotion(direction.scale(0.01D));
                    this.arrowVelocities.put(arrow.getUniqueID(), direction.scale(arrowSpeed));
                    this.arrowStartPosition.put(arrow.getUniqueID(), arrow.getPositionVec());
                    this.world.addEntity(arrow);
                    this.arrows.add(arrow);

                    //effects
                    ((ServerWorld) this.world).spawnParticle(ModParticles.SOUL_FLAME, arrow.getPosX(), arrow.getPosY(), arrow.getPosZ(), 3, 1, 0.1D, 0.1D, 0.1D);
                    this.world.playSound(null, arrow.getPosX(), arrow.getPosY(), arrow.getPosZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.HOSTILE, 1.3F, 1.3F);

                }

        }
    }

    @Override
    public void tick()
    {
        if(this.waves > maxWaves())
            return;
        if(this.arrows.isEmpty())
        {
            this.spawnArrows();
            this.summonTime = this.boss.isInSecondPhase() ? 18 : 25;
        }
        else if(this.summonTime > 0)
        {
            if(--this.summonTime == 0)
                arrows.forEach(arrow ->
                {
                    arrow.setMotion(this.arrowVelocities.get(arrow.getUniqueID()));
                    this.world.playSound(null, arrow.getPosX(), arrow.getPosY(), arrow.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.HOSTILE, 1F, 1.3F);
                    arrow.velocityChanged = true;
                });
        }
        else
        {
            Iterator<AbstractArrowEntity> arrowEntityIterator = this.arrows.iterator();
            while(arrowEntityIterator.hasNext())
            {
                AbstractArrowEntity arrow = arrowEntityIterator.next();
                if(!arrow.isAlive() || arrow.isEntityInsideOpaqueBlock() || this.arrowStartPosition.get(arrow.getUniqueID()).squareDistanceTo(arrow.getPositionVec()) >= 20 * 20)
                {
                    arrowEntityIterator.remove();
                    arrowVelocities.remove(arrow.getUniqueID());
                    arrowStartPosition.remove(arrow.getUniqueID());
                    arrow.remove();
                    ((ServerWorld) this.world).spawnParticle(ModParticles.SOUL_FLAME, arrow.getPosX(), arrow.getPosY(), arrow.getPosZ(), 3, 1, 0.1D, 0.1D, 0.1D);
                }
            }
            if(this.arrows.isEmpty())
                this.waves++;
        }
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting() && this.waves <= maxWaves();
    }

    private int maxWaves()
    {
        return 4;
    }

    private EffectInstance getArrowEffect()
    {
        if(this.boss.isInSecondPhase())
            return new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1);
        return new EffectInstance(Effects.POISON, 60, 1);
    }

    @Override
    public void resetTask()
    {
        this.summonTime = 0;
        this.waves = 0;
        this.arrows.forEach(Entity::remove);
        this.arrows.clear();
        this.arrowVelocities.clear();
        this.arrowStartPosition.clear();
        this.endAttack();
    }

}
