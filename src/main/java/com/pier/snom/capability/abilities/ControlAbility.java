package com.pier.snom.capability.abilities;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.render.ControlAbilityRenderer;
import com.pier.snom.client.particle.SoulPlayerParticleData;
import com.pier.snom.init.ModDamageSource;
import com.pier.snom.init.ModSounds;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.client.PacketPlaySmashParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;

public class ControlAbility implements ISoulAbility<ControlAbilityRenderer>
{


    public static double MAX_REACH_DISTANCE = 8D;

    public int selectedEntityID = 0;
    public double distance = 8D;
    private boolean lastCollide = false;


    @Override
    public EnumAbility getAbility()
    {
        return EnumAbility.CONTROL;
    }

    @Override
    public boolean canUse(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return isControllingEntity() || player.getHeldItemMainhand().isEmpty();
    }


    @Override
    public void onUpdate(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        World world = player.world;
        Entity entity = world.getEntityByID(this.selectedEntityID);


        if(entity != null && isSelectedAbility(soulPlayer))
        {

            Vec3d entityVec = getEntityVec(entity, 1.0F);
            Vec3d lookVec = player.getLookVec().scale(distance);
            Vec3d target = player.getEyePosition(1.0F).add(lookVec);


            double speed = entity.getPositionVec().distanceTo(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));

            if(!world.isRemote)
            {
                float rotationYaw = getRotationYawPitch(player, entity, 1.0F).x;
                float yawRender = player.renderYawOffset * ((float) Math.PI / 180F);

                double signed_diff = (rotationYaw - yawRender + (Math.PI * 3D)) % (Math.PI * 2D) - Math.PI;
                if(!player.isAlive() || !entity.isAlive() || entityVec.distanceTo(target) > 15D || signed_diff <= 0 && !entity.isPassenger(player))
                {

                    this.selectedEntityID = 0;
                    SoulPlayer.updatePlayerData(player);
                    entity.setMotion(0D, 0D, 0D);
                    entity.velocityChanged = true;

                }

                double d1 = target.x - entityVec.x;
                double d2 = target.y - entityVec.y;
                double d3 = target.z - entityVec.z;

                double maxSpeed = 28D;
                if(entity.isPassenger(player))
                    maxSpeed = 1D;

                entity.setMotion(new Vec3d(d1, d2, d3).normalize().scale(Math.min(maxSpeed, target.distanceTo(entityVec))));
                entity.velocityChanged = true;

                if(entity instanceof ServerPlayerEntity)
                    ((ServerPlayerEntity) entity).connection.sendPacket(new SEntityVelocityPacket(entity));


                if(entity.collided && !this.lastCollide && speed >= 0.8D)
                {
                    float volume = (float) speed * 2F;
                    float pitch = 0.9F + (float) Math.random() * 0.2F;

                    world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModSounds.CONTROL_SMASH, SoundCategory.AMBIENT, volume, pitch);
                    PacketManager.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),new PacketPlaySmashParticle(entity.getEntityId()));

                    entity.attackEntityFrom(ModDamageSource.causeSmashDamage(player), (float) speed * 4F);

                    if(!entity.isAlive())
                        entity.setMotion(0D, 0D, 0D);
                }
                entity.fallDistance = 0F;

            }
            else
            {
                SoulPlayerParticleData soulFlame = new SoulPlayerParticleData(null);

                if(entity instanceof PlayerEntity)
                    soulFlame = new SoulPlayerParticleData(entity.getUniqueID());

                Random rand = new Random();
                int particleAmount = (int) ((speed + 0.2D) * 4D);

                double particleX = entity.getPosX() + (rand.nextDouble() - 0.5D) * (double) entity.getWidth();
                double particleY = entity.getPosY() + rand.nextDouble() * (double) entity.getHeight();
                double particleZ = entity.getPosZ() + (rand.nextDouble() - 0.5D) * (double) entity.getWidth();

                Vec3d particleMotion = entity.getMotion().scale(-0.1D);

                if(particleAmount == 0)
                {
                    if(world.getGameTime() % 5 == 0)
                        world.addParticle(soulFlame, true, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
                }
                else
                {
                    for (int i = 0; i < particleAmount + 2; i++)
                        world.addParticle(soulFlame, true, particleX, particleY, particleZ, particleMotion.x, particleMotion.y, particleMotion.z);
                }
            }
            this.lastCollide = entity.collided;

        }
        else if(this.isControllingEntity())
        {
            this.selectedEntityID = 0;

        }
    }


    private Vec3d getEntityVec(Entity entity, float partialTicks)
    {
        if(partialTicks == 1.0F)
        {
            return new Vec3d(entity.getPosX(), entity.getPosY() + entity.getSize(Pose.STANDING).height / 2F, entity.getPosZ());
        }
        else
        {
            double d0 = MathHelper.lerp(partialTicks, entity.prevPosX, entity.getPosX());
            double d1 = MathHelper.lerp(partialTicks, entity.prevPosY, entity.getPosY()) + entity.getSize(Pose.STANDING).height / 2F;
            double d2 = MathHelper.lerp(partialTicks, entity.prevPosZ, entity.getPosZ());
            return new Vec3d(d0, d1, d2);
        }

    }


    public boolean isControllingEntity()
    {
        return this.selectedEntityID != 0;
    }

    private double getEntityVolumeFactor(Entity entity)
    {
        AxisAlignedBB boxEntity = entity.getBoundingBox();
        double volume = boxEntity.getXSize() + boxEntity.getYSize() + boxEntity.getZSize();
        volume = Math.min(volume, 12D);

        return 1D - (volume / 12D);
    }

    @Override
    public boolean cast(ISoulPlayer soulPlayer, PlayerEntity player)
    {

        World world = player.world;

        EntityRayTraceResult rayTraceResult = getRayTraceResult(player);

        if(isControllingEntity())
        {
            Entity entity = world.getEntityByID(this.selectedEntityID);

            if(entity != null)
            {
                entity.setMotion(entity.getMotion().scale(0.2D + getEntityVolumeFactor(entity) * 0.4D));
                entity.velocityChanged = true;
                if(entity instanceof DamagingProjectileEntity)
                {
                    DamagingProjectileEntity projectile = (DamagingProjectileEntity) entity;
                    projectile.accelerationX = entity.getMotion().x;
                    projectile.accelerationY = entity.getMotion().y;
                    projectile.accelerationZ = entity.getMotion().z;
                }
                if(player.isSneaking())
                {
                    if(!world.isRemote)
                    {
                        SoulPlayer.updatePlayerData(player);

                        Vec3d shootVec = player.getLookVec().scale(1.3D + getEntityVolumeFactor(entity));

                        if(entity instanceof DamagingProjectileEntity)
                        {
                            DamagingProjectileEntity projectile = (DamagingProjectileEntity) entity;
                            projectile.accelerationX = shootVec.x * 0.2D;
                            projectile.accelerationY = shootVec.y * 0.2D;
                            projectile.accelerationZ = shootVec.z * 0.2D;
                        }
                        else
                            entity.setMotion(shootVec);
                        entity.velocityChanged = true;

                    }
                }
            }
            this.selectedEntityID = 0;
            return true;
        }
        else if(rayTraceResult != null && !world.isRemote)
        {
            Entity entity = rayTraceResult.getEntity();

            entity.stopRiding();
            this.selectedEntityID = entity.getEntityId();
            this.distance = entity.getPositionVec().distanceTo(player.getEyePosition(1.0F));

            if(entity instanceof DamagingProjectileEntity)
                ((DamagingProjectileEntity) entity).shootingEntity = player;

            soulPlayer.consumeSoul(player, 2F);


          //  PacketManager.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new PacketPlayControlSound(this.selectedEntityID));

            return true;
        }


        return false;

    }

    private EntityRayTraceResult getRayTraceResult(PlayerEntity player)
    {
        Vec3d playerPos = player.getEyePosition(1.0F);
        Vec3d lookVec = player.getLookVec().scale(MAX_REACH_DISTANCE);
        Vec3d target = playerPos.add(lookVec);
        AxisAlignedBB box = player.getBoundingBox().expand(target.subtract(playerPos)).grow(10D);
        return ProjectileHelper.rayTraceEntities(player.world, player, playerPos, target, box, (entity) -> !entity.isSpectator() && entity.isNonBoss() && entity.isAlive(), Double.MAX_VALUE);
    }

    @Override
    public float soulUsePreview(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        if(getRayTraceResult(player) != null && !this.isControllingEntity())
            return -2F;
        return 0F;
    }

    @Override
    public boolean shouldRegenPlayer(PlayerEntity player, ISoulPlayer iSoulPlayer)
    {
        return !isControllingEntity();
    }

    public Vec2f getRotationYawPitch(PlayerEntity player, Entity entity, float partialTicks)
    {
        Vec3d entityPosition = getEntityVec(entity, partialTicks);
        Vec3d playerPosition = player.getEyePosition(partialTicks);

        double d0 = entityPosition.x - playerPosition.x;
        double d1 = entityPosition.y - playerPosition.y;
        double d2 = entityPosition.z - playerPosition.z;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        float rotationPitch = MathHelper.wrapDegrees((float) (-MathHelper.atan2(d1, d3)));
        float rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0)));
        return new Vec2f(rotationYaw, rotationPitch);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        nbt.putDouble("distance", this.distance);
        nbt.putInt("selectedEntityID", this.selectedEntityID);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        this.distance = nbt.getDouble("distance");
        this.selectedEntityID = nbt.getInt("selectedEntityID");
    }

    @Override
    public boolean shouldBlockInteractions(PlayerEntity player, ISoulPlayer iSoulPlayer)
    {
        return this.isControllingEntity();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ControlAbilityRenderer getRenderer()
    {
        return new ControlAbilityRenderer(this);
    }


}
