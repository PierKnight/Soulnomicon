package com.pier.snom.capability.abilities;

import com.google.common.collect.Multimap;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.animation.BeamAnimation;
import com.pier.snom.capability.render.BeamAbilityRenderer;
import com.pier.snom.client.ClientEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

public class BeamAbility implements ISoulAbility<BeamAbilityRenderer>
{

    private static final double DISTANCE = 20D;


    private static final UUID BEAM_DISTANCE = UUID.fromString("a2725db4-de36-11ea-87d0-0242ac130003");
    private static final AttributeModifier modifier = new AttributeModifier(BEAM_DISTANCE, "beam_distance", DISTANCE, AttributeModifier.Operation.ADDITION);

    private boolean isActive = false;

    public final BeamAnimation beamAnimation = new BeamAnimation();

    public int damageTime = 0;

    private double hitDistance = DISTANCE;
    private BlockPos lastHitBlock = BlockPos.ZERO;


    public boolean isActive()
    {
        return isActive;
    }

    @Override
    public EnumAbility getAbility()
    {
        return EnumAbility.BEAM;
    }

    @Override
    public boolean canUse(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return true;
    }

    @Override
    public void onUpdate(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        beamAnimation.update(player, this);

        if(this.beamAnimation.ticks < 30)
            return;

        World world = player.world;

        Vec3d lookVec = player.getLook(1F);
        Vec3d eyePos = player.getEyePosition(1F);
        Vec3d targetPos = eyePos.add(lookVec.scale(DISTANCE));

        AxisAlignedBB axisalignedbb = player.getBoundingBox().expand(lookVec.scale(DISTANCE)).grow(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityRayTraceResult = ProjectileHelper.rayTraceEntities(world,player, eyePos, targetPos, axisalignedbb, (entity) -> !entity.isSpectator() && entity.canBeCollidedWith(), DISTANCE * DISTANCE);

        this.hitDistance = DISTANCE;
        if(entityRayTraceResult != null)
        {
            this.damageTime++;
            if(this.damageTime >= player.getCooldownPeriod())
            {
                player.attackTargetEntityWithCurrentItem(entityRayTraceResult.getEntity());
                this.damageTime = 0;
            }
            switchWeapon(player);

            this.hitDistance = entityRayTraceResult.getHitVec().distanceTo(eyePos);
        }
        else
        {
            if(this.damageTime > 0)
                this.damageTime = 0;
            BlockRayTraceResult rayTraceResult = player.world.rayTraceBlocks(new RayTraceContext(eyePos, targetPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
            BlockPos blockPos = rayTraceResult.getPos();
            BlockState state = world.getBlockState(blockPos);

            if(!state.isAir(world, blockPos))
            {
                if(world.isRemote)
                    ClientEvents.damageBlock(player,blockPos, rayTraceResult.getFace());

                switchBlockTool(player, state);
                this.hitDistance = rayTraceResult.getHitVec().distanceTo(eyePos);
                this.lastHitBlock = blockPos;
            }

        }

    }

    private void switchBlockTool(PlayerEntity player, BlockState state)
    {
        ItemStack offStack = player.getHeldItem(Hand.OFF_HAND);
        ItemStack mainStack = player.getHeldItem(Hand.MAIN_HAND);
        float offHandSpeed = offStack.getItem().getDestroySpeed(offStack, state);
        float mainHandSpeed = mainStack.getItem().getDestroySpeed(mainStack, state);
        if(offHandSpeed > mainHandSpeed)
        {
            player.inventory.mainInventory.set(player.inventory.currentItem, offStack.copy());
            player.inventory.offHandInventory.set(0, mainStack.copy());
        }

    }

    private void switchWeapon(PlayerEntity player)
    {
        ItemStack offStack = player.getHeldItem(Hand.OFF_HAND);
        ItemStack mainStack = player.getHeldItem(Hand.MAIN_HAND);
        float offHandDamage = getItemAttackDamage(offStack);
        float mainHandDamage = getItemAttackDamage(mainStack);
        if(!(mainStack.getItem() instanceof SwordItem) && (offStack.getItem() instanceof SwordItem || offHandDamage > mainHandDamage))
        {
            player.inventory.mainInventory.set(player.inventory.currentItem, offStack.copy());
            player.inventory.offHandInventory.set(0, mainStack.copy());
            this.damageTime = 0;
        }

    }

    private float getItemAttackDamage(ItemStack stack)
    {
        Multimap<String, AttributeModifier> map = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        return map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).stream().mapToInt(attributeModifier -> (int) attributeModifier.getAmount()).sum();
    }

    public double getHitDistance()
    {
        return hitDistance;
    }

    @Override
    public boolean cast(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        this.isActive = !this.isActive;
        if(this.isActive)
        {
            player.getAttribute(PlayerEntity.REACH_DISTANCE).applyModifier(modifier);
            this.damageTime = 0;
        }
        else
        {
            player.getAttribute(PlayerEntity.REACH_DISTANCE).removeModifier(BEAM_DISTANCE);
            this.beamAnimation.ticks = 7;
            if(player.world.isRemote)
                ClientEvents.resetBlockBreaking(lastHitBlock);
        }

        this.beamAnimation.rotationSpeed = 0;
        return true;
    }


    @Override
    public boolean shouldBlockInteractions(PlayerEntity player, ISoulPlayer iSoulPlayer)
    {
        return this.isActive;
    }

    @Override
    public float soulUsePreview(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        return 0;
    }

    @Override
    public boolean shouldRegenPlayer(PlayerEntity player, ISoulPlayer iSoulPlayer)
    {
        return !this.isActive;
    }

    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        nbt.putBoolean("isActive", this.isActive);
        this.beamAnimation.writeToNBT(nbt, "active");
    }

    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        this.isActive = nbt.getBoolean("isActive");
        this.beamAnimation.readFromNBT(nbt, "active");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BeamAbilityRenderer getRenderer()
    {
        return new BeamAbilityRenderer(this);
    }
}
