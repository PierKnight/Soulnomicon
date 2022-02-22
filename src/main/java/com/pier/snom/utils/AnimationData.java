package com.pier.snom.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class AnimationData implements INBTSerializable<CompoundNBT>
{
    protected int currentTick;
    protected int prevTick;

    public float getProgress(float partialTicks)
    {
        return MathHelper.lerp(partialTicks,prevTick,currentTick);
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

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.currentTick = nbt.getInt("ticks");
    }

}
