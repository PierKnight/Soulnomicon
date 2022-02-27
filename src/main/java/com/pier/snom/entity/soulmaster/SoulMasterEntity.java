package com.pier.snom.entity.soulmaster;

import com.pier.snom.client.particle.ModParticles;
import com.pier.snom.client.particle.SoulLeakingParticle;
import com.pier.snom.entity.AttackGoal;
import com.pier.snom.entity.DungeonBossEntity;
import com.pier.snom.entity.SingleAnimationData;
import com.pier.snom.utils.AnimationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nonnull;
import java.util.List;

public class SoulMasterEntity extends DungeonBossEntity
{

    //ai attacks
    private final ArrowCageGoal arrowCage = new ArrowCageGoal(this);
    private final ArrowSummonGoal arrowAttack = new ArrowSummonGoal(this);
    private final SkeletonPuppetsGoal skeletonPuppets = new SkeletonPuppetsGoal(this);

    //animations
    public final ArrowSummonAnimationData ARROW_SUMMONING = new ArrowSummonAnimationData(this, 0);
    public final SingleAnimationData HAND_COMMAND_ARROW = new SingleAnimationData(13, 1);
    public final SkeletonPuppetsAnimation PUPPETS_ANIMATION = new SkeletonPuppetsAnimation(this, 2);
    public int isShakingForLosingControl = 0;
    protected final AnimationData[] ANIMATIONS = new AnimationData[]{ARROW_SUMMONING, HAND_COMMAND_ARROW, PUPPETS_ANIMATION};

    public int hurtSeed;


    public SoulMasterEntity(EntityType<? extends SoulMasterEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    }

    public static AttributeModifierMap.MutableAttribute getAttributes()
    {
        return MonsterEntity.registerAttributes().createMutableAttribute(Attributes.FOLLOW_RANGE, 48D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4D).createMutableAttribute(Attributes.MAX_HEALTH, 200D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 99999D).createMutableAttribute(Attributes.FLYING_SPEED, 1D).createMutableAttribute(ForgeMod.ENTITY_GRAVITY.get(), 0D);
    }

    @Override
    public void livingTick()
    {
        super.livingTick();
        if(world.isRemote && !this.getShouldBeDead())
        {
            int shakeChance = (int)((0.5F - this.getHealth() / this.getMaxHealth()) * 30);

            if(this.isInSecondPhase() && this.isShakingForLosingControl == 0 && shakeChance > 0 && this.rand.nextInt(shakeChance) == 0)
                this.isShakingForLosingControl = 10 + this.rand.nextInt(20);
            if(this.isShakingForLosingControl > 0)
                --this.isShakingForLosingControl;
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount)
    {
        if(this.hurtTime == 0)
            this.hurtSeed = this.rand.nextInt();

        //cannot be hit from hitself and skeletons
        if(source.getTrueSource() == this || source.getTrueSource() instanceof SkeletonEntity)
            return false;

        //damage reduction during skeleton attack
        if(this.attackGoal == skeletonPuppets)
            amount *= 0.5F;

        return super.attackEntityFrom(source, amount);
    }


    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void collideWithNearbyEntities() {}

    @Override
    public void applyKnockback(float x, double y, double z)
    {
    }

    @Override
    protected void onBossDeath()
    {
        if(world.isRemote)
        {
            for (int i = 0; i < 15; i++)
            {
                double d0 = this.getPosX() + (this.rand.nextDouble() - this.rand.nextDouble()) * this.getBoundingBox().getXSize();
                double d1 = this.getPosY() + (this.rand.nextDouble() - this.rand.nextDouble()) * this.getBoundingBox().getYSize();
                double d2 = this.getPosZ() + (this.rand.nextDouble() - this.rand.nextDouble()) * this.getBoundingBox().getZSize();
                Minecraft.getInstance().particles.addEffect(new SoulLeakingParticle((ClientWorld) world, 0, d0, d1, d2, 0.2D + this.rand.nextDouble() * 0.25D));
            }
            Minecraft.getInstance().particles.addEffect(new SoulLeakingParticle.SoulMasterPiece((ClientWorld) world, this.getPosX(), this.getPosYEye(), this.getPosZ(), 0.25D, 0));
            Minecraft.getInstance().particles.addEffect(new SoulLeakingParticle.SoulMasterPiece((ClientWorld) world, this.getPosX(), this.getPosY() + this.getBoundingBox().getYSize() * 0.5D, this.getPosZ(), 0.2D, 1));
            Minecraft.getInstance().particles.addEffect(new SoulLeakingParticle.SoulMasterPiece((ClientWorld) world, this.getPosX(), this.getPosY() + this.getBoundingBox().getYSize() * 0.5D, this.getPosZ(), 0.2D, 2));
            Minecraft.getInstance().particles.addEffect(new SoulLeakingParticle.SoulMasterPiece((ClientWorld) world, this.getPosX(), this.getPosY() + this.getBoundingBox().getYSize() * 0.3D, this.getPosZ(), 0.2D, 3));

        }
        super.onBossDeath();

    }

    @Override
    protected AttackGoal<SoulMasterEntity> getNextAttackGoal()
    {
        return this.rand.nextBoolean() ? (this.rand.nextBoolean() ? this.skeletonPuppets : this.arrowAttack) : this.arrowCage;
    }

    @Override
    protected AnimationData[] getAnimations()
    {
        return ANIMATIONS;
    }

    public static class ArrowSummonAnimationData extends AnimationData
    {
        private final SoulMasterEntity soulMaster;
        private boolean started = false;

        public ArrowSummonAnimationData(SoulMasterEntity soulMaster, int index)
        {
            super(index);
            this.soulMaster = soulMaster;
        }

        @Override
        public void play()
        {
            if(this.started)
                this.currentTick = 0;
            this.started = !this.started;
        }

        @Override
        public void update()
        {
            super.update();
            if(this.started)
            {
                if(this.currentTick < 20F)
                    this.currentTick++;
            }
            else if(this.currentTick > 0)
                this.currentTick -= 5;

            if(this.started)
            {
                Vector3d vector3d = new Vector3d(1, 1, 0);
                float X = this.soulMaster.ticksExisted * 0.209F;
                float Y = (this.soulMaster.rotationYawHead + 90F) / 180F * (float) Math.PI;
                float Z = -this.soulMaster.rotationPitch / 180F * (float) Math.PI;
                float cosX = MathHelper.cos(X);
                float sinX = MathHelper.sin(X);
                float cosY = MathHelper.cos(Y);
                float sinY = MathHelper.sin(Y);
                float cosZ = MathHelper.cos(Z);
                float sinZ = MathHelper.sin(Z);
                double x = dotProduct(vector3d, cosZ, -sinZ * cosX, sinZ * sinX);
                double y = dotProduct(vector3d, sinZ, cosZ * cosX, -cosZ * sinX);
                double z = dotProduct(vector3d, 0, sinX, cosX);
                double finalX = x * cosY - z * sinY;
                double finalZ = x * sinY + z * cosY;
                Vector3d point = this.soulMaster.getEyePosition(1.0F).add(finalX, y, finalZ);
                this.soulMaster.world.addParticle(ModParticles.SOUL_FLAME, true, point.x, point.y, point.z, 0, 0, 0);
            }
        }


        private double dotProduct(Vector3d vec, double a, double b, double c)
        {
            return vec.x * a + vec.y * b + vec.z * c;
        }

        public boolean isStarted() {
            return this.started;
        }

        @Override
        public float getProgress(float partialTicks)
        {
            return MathHelper.lerp(partialTicks, prevTick, currentTick) / 20F;
        }
    }

    public static class SkeletonPuppetsAnimation extends AnimationData
    {
        private final SoulMasterEntity soulMaster;
        private boolean started = false;

        public SkeletonPuppetsAnimation(SoulMasterEntity soulMaster, int index)
        {
            super(index);
            this.soulMaster = soulMaster;
        }

        @Override
        public void play()
        {
            this.started = !this.started;
        }

        public boolean isStarted() {return this.started;}

        @Override
        public void update()
        {
            super.update();
            if(this.started)
            {
                if(this.currentTick < 25)
                    this.currentTick++;


                if(this.soulMaster.world.isRemote)
                {
                    List<SkeletonEntity> list = this.soulMaster.world.getEntitiesWithinAABB(SkeletonEntity.class, this.soulMaster.getBoundingBox().grow(20), entity -> entity.getCustomName() instanceof StringTextComponent);
                    for (SkeletonEntity entity : list)
                    {
                        Vector3d pos = this.soulMaster.getPositionVec().add(0, 1.5D, 0);
                        Vector3d direction = entity.getBoundingBox().getCenter().subtract(pos);

                        for (int i = 0; i <= 10; i++)
                        {
                            Vector3d point = pos.add(direction.scale(i / 10D));
                            this.soulMaster.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, true, point.x, point.y, point.z, 0, 0, 0);
                        }
                    }
                }
            }
            else if(this.currentTick > 0)
                this.currentTick -= 1;
        }

        @Override
        public float getProgress(float partialTicks)
        {
            return MathHelper.lerp(partialTicks, prevTick, currentTick) / 25F;
        }
    }


}
