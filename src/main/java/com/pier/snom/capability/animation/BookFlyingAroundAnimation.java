package com.pier.snom.capability.animation;

import com.pier.snom.capability.ISoulPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class BookFlyingAroundAnimation extends BaseAnimation
{

    public boolean isFlying = false;

    public BookFlyingAroundAnimation()
    {
        super(8);
    }

    @Override
    public boolean shouldAnimate(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return isFlying;
    }

    public float getHudAnimationF(float partialTicks)
    {
        if(ticks > 9)
            return (MathHelper.lerp(partialTicks, this.prevTicks, this.ticks) - 9F) / 3F;
        return 0F;
    }

    @Override
    public float getAnimationF(float partialTicks)
    {
        return super.getAnimationF(partialTicks);
    }

    @Override
    public void update(PlayerEntity player, ISoulPlayer soulPlayer)
    {

        if(!shouldAnimate(player, soulPlayer) && this.ticks == 1 && !player.world.isRemote)
        {
            ItemStack soulnomicon = soulPlayer.getAbilitiesManager().soulnomiconStack.copy();
            HandSide handSide = soulPlayer.getAbilitiesManager().bookAbilityHand;
            Hand hand = player.getPrimaryHand().equals(handSide) ? Hand.MAIN_HAND : Hand.OFF_HAND;

            if(player.getHeldItem(hand).isEmpty())
            {
                player.setHeldItem(hand, soulnomicon);
            }
            else if(!player.inventory.addItemStackToInventory(soulnomicon))
                player.dropItem(soulnomicon, false);
        }

        super.update(player, soulPlayer);


    }

    @Override
    public void writeToNBT(CompoundNBT nbt, String name)
    {
        super.writeToNBT(nbt, name);
        nbt.putBoolean("flyingBook", this.isFlying);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt, String name)
    {
        super.readFromNBT(nbt, name);
        this.isFlying = nbt.getBoolean("flyingBook");
    }
}
