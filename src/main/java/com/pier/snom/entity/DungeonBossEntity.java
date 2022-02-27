package com.pier.snom.entity;

import com.pier.snom.utils.AnimationData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DungeonBossEntity extends MonsterEntity
{
    private static final DataParameter<Float> ARENA_RADIUS = EntityDataManager.createKey(CreeperEntity.class, DataSerializers.FLOAT);
    public float renderRadius = 0;
    public float prevRenderRadius = 0;

    //current attack handled by the AI Goal
    public AttackGoal<? extends DungeonBossEntity> attackGoal = null;
    //used to give some brief time space between attacks this is set when called endAttack inside a AttackGoal
    public int cooldown = 0;

    public int prevDeathTime = 0;
    public int prevHurtTime = 0;
    private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);

    protected DungeonBossEntity(EntityType<? extends MonsterEntity> entityType, World world)
    {
        super(entityType, world);
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(ARENA_RADIUS, 0.0F);
    }

    public Float getArenaRadius()
    {
        return this.dataManager.get(ARENA_RADIUS);
    }

    public void setArenaRadius(float radius)
    {
        this.dataManager.set(ARENA_RADIUS, radius);
    }

    public boolean isInSecondPhase(){return this.getHealth() <= this.getMaxHealth() / 2F;}


    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();

        LivingEntity target = getAttackTarget();
        boolean isValid = target != null && target.isAlive();

        if(isValid)
            this.getLookController().setLookPositionWithEntity(target, 180.0F, 180.0F);

        if(this.attackGoal == null)
        {
            if(this.cooldown <= 0)
            {
                if(isValid)
                {
                    this.attackGoal = getNextAttackGoal();
                    this.targetSelector.addGoal(10, this.attackGoal);
                }
            }
            else
                --this.cooldown;
        }
    }

    @Override
    public boolean canDespawn(double p_213397_1_)
    {
        return false;
    }

    @Override
    public boolean isPotionApplicable(EffectInstance effectInstance)
    {
        return effectInstance.getPotion().isBeneficial();
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity target)
    {
        super.setAttackTarget(target);
        this.setArenaRadius(target == null ? 0.0F : 20.0F);
    }

    @Override
    public void livingTick()
    {
        this.prevRenderRadius = this.renderRadius;
        this.prevDeathTime = this.deathTime;
        this.prevHurtTime = hurtTime;
        super.livingTick();
        if(this.isAlive())
            for (AnimationData animationData : getAnimations())
                animationData.update();

        float radius = getArenaRadius();

        if(!world.isRemote)
        {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

            LivingEntity target = this.getAttackTarget();
            if(target != null && target.isAlive())
            {
                // AxisAlignedBB boxCheck = new AxisAlignedBB(-radius,-radius,-radius,radius,radius,radius).offset(this.getPositionVec());
                Vector3d targetPos = target.getBoundingBox().getCenter();
                Vector3d direction = this.getPositionVec().subtract(targetPos);
                if(direction.lengthSquared() >= radius * radius)
                {
                    target.setMotion(direction.normalize().scale(1D));
                    target.velocityChanged = true;
                }
            }

        }
        else
        {
            if(this.renderRadius < radius)
                this.renderRadius = Math.min(radius, this.renderRadius + 1F);
            else if(this.renderRadius > radius)
                this.renderRadius = Math.min(radius, this.renderRadius - 1F);
        }

    }


    @Override
    protected void onDeathUpdate()
    {
        ++this.deathTime;
        if(this.deathTime >= getDeathDuration())
        {
            this.onBossDeath();
            this.remove(false);
        }
        this.setPositionAndUpdate(this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ);
    }


    @Override
    public boolean isNonBoss()
    {
        return false;
    }

    public void setCustomName(@Nullable ITextComponent name)
    {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(@Nonnull ServerPlayerEntity player)
    {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(@Nonnull ServerPlayerEntity player)
    {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound)
    {
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound)
    {
        super.readAdditional(compound);

        if(this.hasCustomName())
            this.bossInfo.setName(this.getDisplayName());

    }

    public void playAnimation(AnimationData animation)
    {
        if(world.isRemote)
            return;
        animation.play();
        this.world.setEntityState(this, (byte) (50 + animation.index));
    }

    protected void onBossDeath()
    {
        if(this.attackGoal != null)
            this.attackGoal.resetTask();

        for (int i = 0; i < 20; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.POOF, this.getPosXRandom(1.0D), this.getPosYRandom(), this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }


    protected abstract AttackGoal<? extends DungeonBossEntity> getNextAttackGoal();

    protected abstract AnimationData[] getAnimations();

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        float radius = getArenaRadius();
        if(radius != 0.0F)
            return new AxisAlignedBB(-radius, -radius, -radius, radius, radius, radius).offset(this.getPositionVec());
        return super.getRenderBoundingBox();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if((int) id >= 50)
        {
            getAnimations()[(int) id - 50].play();
            //animationPacketReceived(id - 50);
            return;
        }
        super.handleStatusUpdate(id);
    }

    public int getDeathDuration() {return 40;}

}
