package com.pier.snom.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class AnimationData implements INBTSerializable<CompoundNBT>
{
    protected int currentTick;
    protected int prevTick;
    public final int index;

    public AnimationData(int index)
    {
        this.index = index;
    }

    public float getProgress(float partialTicks)
    {
        return MathHelper.lerp(partialTicks,prevTick,currentTick);
    }

    public float getProgress(int start,int end,float partialTicks)
    {
        if(this.currentTick < start)
            return 0F;
        if(this.currentTick > end)
            return 1F;

        float progress = MathHelper.lerp(partialTicks,prevTick,currentTick);
        return (progress - start) / (float) (end - start);
    }

    public void update()
    {
        this.prevTick = this.currentTick;
    }

    public abstract void play();

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("ticks",this.currentTick);
        return tag;
    }

    public int getCurrentTick(){return this.currentTick;}

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.currentTick = nbt.getInt("ticks");
    }

}
