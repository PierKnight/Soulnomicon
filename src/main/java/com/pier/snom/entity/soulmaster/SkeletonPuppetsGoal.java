package com.pier.snom.entity.soulmaster;

import com.pier.snom.client.particle.ModParticles;
import com.pier.snom.entity.AttackGoal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class SkeletonPuppetsGoal extends AttackGoal<SoulMasterEntity>
{
    private int timer = 0;
    private final List<SkeletonEntity> minions = new ArrayList<>();

    public SkeletonPuppetsGoal(SoulMasterEntity boss)
    {
        super(boss);
    }


    @Override
    public boolean shouldExecute()
    {
        return super.shouldExecute() && !this.boss.PUPPETS_ANIMATION.isStarted();
    }

    @Override
    protected int getCooldown()
    {
        return 40;
    }

    @Override
    public void startExecuting()
    {
        this.boss.playAnimation(this.boss.PUPPETS_ANIMATION);
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if(!target.isAlive())
            return false;

        if(this.timer >= 20 * 30)
            return false;

        if(this.boss.PUPPETS_ANIMATION.getCurrentTick() < 20)
            return true;

        for (SkeletonEntity skeleton : this.minions)
            if(skeleton.isAlive() && skeleton.getPositionVec().squareDistanceTo(this.boss.getPositionVec()) <= this.boss.getArenaRadius() * this.boss.getArenaRadius())
                return true;
        return false;
    }

    @Override
    public void tick()
    {
        this.timer++;

        if(timer % 40 == 0)
        {
            if(this.boss.isInSecondPhase())
            {
                this.boss.attackEntityFrom(DamageSource.MAGIC, 3F);
                ((ServerWorld) this.world).spawnParticle(ModParticles.SOUL_FLAME, this.boss.getPosX(), this.boss.getPosYEye(), this.boss.getPosZ(), 20, 0.5D, 0.5D, 0.5D, 0.1D);
            }
            else
                this.boss.heal(5F);
        }

        if(this.boss.PUPPETS_ANIMATION.getCurrentTick() == 19)
        {
            this.spawnSkeleton();
            this.spawnSkeleton();
            this.spawnSkeleton();
        }


    }

    private void spawnSkeleton()
    {
        ItemStack heldItem = new ItemStack(this.boss.getRNG().nextBoolean() ? Items.BOW : Items.IRON_SWORD);

        SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, this.world);
        skeleton.setPosition(this.boss.getPosX(), this.boss.getPosY(), this.boss.getPosZ());
        skeleton.setMotion(0.2D - this.boss.getRNG().nextGaussian() * 0.4D, 0.4D, 0.2D - this.boss.getRNG().nextGaussian() * 0.4D);
        skeleton.setCustomName(new StringTextComponent("Boss Puppet"));

        equipItemStack(skeleton,EquipmentSlotType.MAINHAND,heldItem);

        if(this.boss.isInSecondPhase())
        {
            equipItemStack(skeleton,EquipmentSlotType.HEAD,new ItemStack(Items.CHAINMAIL_HELMET));
            equipItemStack(skeleton,EquipmentSlotType.CHEST,new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            equipItemStack(skeleton,EquipmentSlotType.LEGS,new ItemStack(Items.CHAINMAIL_LEGGINGS));
            equipItemStack(skeleton,EquipmentSlotType.FEET,new ItemStack(Items.CHAINMAIL_BOOTS));
        }

        this.world.addEntity(skeleton);
        this.minions.add(skeleton);
        skeleton.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(skeleton, this.target.getClass(), true));
        skeleton.targetSelector.goals.removeIf(prioritizedGoal -> prioritizedGoal.getGoal() instanceof HurtByTargetGoal || prioritizedGoal.getGoal() instanceof FleeSunGoal);
    }

    @Override
    public void resetTask()
    {
        this.minions.forEach(entity -> entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, Integer.MAX_VALUE));
        this.minions.clear();
        this.timer = 0;
        this.boss.playAnimation(this.boss.PUPPETS_ANIMATION);
        this.endAttack();
    }

    private void equipItemStack(SkeletonEntity skeleton,EquipmentSlotType slotType,ItemStack stack)
    {
        if(slotType != EquipmentSlotType.MAINHAND)
            if(this.boss.getRNG().nextBoolean())
                return;

        Difficulty difficulty = world.getDifficulty();
            EnchantmentHelper.addRandomEnchantment(this.boss.getRNG(), stack, (difficulty.getId() - 1) * 2 - 1, false);
        skeleton.setItemStackToSlot(slotType,stack);
    }
}
