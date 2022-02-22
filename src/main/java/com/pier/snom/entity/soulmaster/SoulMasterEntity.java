package com.pier.snom.entity.soulmaster;

import com.pier.snom.entity.*;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoulMasterEntity extends DungeonBossEntity
{

    private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);

    public SoulMasterEntity(EntityType<? extends SoulMasterEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    public SoulMasterEntity(World worldIn) {
        this(EntityRegistry.SOUL_MASTER_ENTITY, worldIn);
    }

    @Override
    public boolean hasNoGravity()
    {
        return true;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, CowEntity.class, false));
        this.targetSelector.addGoal(3,new SoulMasterAttackPatternGoal(this));
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public ActionResultType applyPlayerInteraction(@Nonnull PlayerEntity player,@Nonnull Vector3d vec,@Nonnull Hand hand)
    {
        this.playAnimation(0);
        return super.applyPlayerInteraction(player, vec, hand);
    }

    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean isNonBoss()
    {
        return false;
    }

    public void setCustomName(@Nullable ITextComponent name) {
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
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {

        return super.getStandingEyeHeight(poseIn, sizeIn);
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

        if (this.hasCustomName())
            this.bossInfo.setName(this.getDisplayName());

    }


    public static final int LIGHTNING_ATTACK = 0;
    public static final int FIREBALL_SPAM = 1;
    public static final int[] ATTACKS = {LIGHTNING_ATTACK,FIREBALL_SPAM};

    @Override
    protected EntityAnimationData<SoulMasterEntity>[] getAnimations()
    {
        return ArrayUtils.toArray(new LightningAttack(this),new FireBallSpam(this));
    }

    private static class LightningAttack extends SingleAnimationData<SoulMasterEntity> implements IAnimationAttack
    {

        public LightningAttack(SoulMasterEntity entity)
        {
            super(entity, 30);
        }

        @Override
        public void update(World world)
        {
            super.update(world);
            LivingEntity target = getTarget();
            if(target != null)
            {
                //System.out.println(this.currentTick);
                if(this.currentTick == 10)
                {
                    LightningBoltEntity bolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT,world);
                    bolt.setEffectOnly(false);
                    bolt.setPositionAndUpdate(target.getPosX(),target.getPosY(),target.getPosZ());
                    world.addEntity(bolt);
                }
            }
        }
        @Override
        public boolean isFinished()
        {
            return this.currentTick == 0;
        }
    }

    private static class FireBallSpam extends SingleAnimationData<SoulMasterEntity> implements IAnimationAttack
    {

        public FireBallSpam(SoulMasterEntity entity)
        {
            super(entity, 20);
        }

        @Override
        public void update(World world)
        {
            super.update(world);
            LivingEntity target = getTarget();
            if(target != null)
            {
                if(this.currentTick % 2 == 0)
                {
                    Vector3d entityPos = entity.getEyePosition(1.0F);
                    Vector3d targetPos = target.getBoundingBox().getCenter();
                    Vector3d direction = targetPos.subtract(entityPos).normalize();
                    double distance = entityPos.distanceTo(targetPos);
                    double aim = distance * 0.01D;
                    double offsetX = aim - aim * 2 * entity.getRNG().nextDouble();
                    double offsetZ = aim - aim * 2 * entity.getRNG().nextDouble();
                    SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world,entity,direction.x + offsetX,direction.y,direction.z + offsetZ);
                    smallFireballEntity.setPositionAndUpdate(entityPos.x,entityPos.y,entityPos.z);
                    world.addEntity(smallFireballEntity);

                    world.playSound(null,entityPos.x,entityPos.y,entityPos.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 1F,1F + entity.getRNG().nextFloat() * 0.2F);
                }
            }
        }

        @Override
        public boolean isFinished()
        {
            return this.currentTick == 0;
        }
    }



}
