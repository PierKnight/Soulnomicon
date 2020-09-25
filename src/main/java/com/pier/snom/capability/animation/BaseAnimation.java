package com.pier.snom.capability.animation;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.client.ClientEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseAnimation
{

    final int maxTicks;
    public int prevTicks = 0;
    public int ticks = 0;

    public BaseAnimation(int maxTicks)
    {
        this.maxTicks = maxTicks;
    }

    public void update(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        this.prevTicks = this.ticks;

        if(shouldAnimate(player, soulPlayer))
        {
            if(ticks < maxTicks)
                this.ticks++;
        }
        else if(ticks > 0)
            this.ticks--;

    }

    public float getAnimationF(float partialTicks, int start, int end)
    {
        int prevTicks = MathHelper.clamp(this.prevTicks - start,0,end);
        int ticks = MathHelper.clamp(this.ticks - start,0,end);
        return MathHelper.lerp(partialTicks, prevTicks, ticks) / (end - start);
    }


    public float getAnimationF(float partialTicks)
    {
        return MathHelper.lerp(partialTicks, this.prevTicks, this.ticks) / maxTicks;
    }

    @OnlyIn(Dist.CLIENT)
    public float getAnimationF()
    {
        return getAnimationF(ClientEvents.getPartialTicks());
    }

    public boolean shouldAnimate(PlayerEntity player, ISoulPlayer soulPlayer) {return false;}


    public void writeToNBT(CompoundNBT nbt, String name)
    {
        nbt.putInt(name + "PrevTicks", this.prevTicks);
        nbt.putInt(name + "Ticks", this.ticks);
    }

    public void readFromNBT(CompoundNBT nbt, String name)
    {
        this.prevTicks = nbt.getInt(name + "PrevTicks");
        this.ticks = nbt.getInt(name + "Ticks");
    }
}
