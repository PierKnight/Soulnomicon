package com.pier.snom.entity;

import com.pier.snom.utils.AnimationData;

public class SingleAnimationData extends AnimationData
{
    private final int duration;
    public SingleAnimationData(int duration,int index)
    {
        super(index);
        this.duration = duration;
    }

    @Override
    public void play()
    {
        this.currentTick = duration;
    }

    @Override
    public void update()
    {
        super.update();
        if(this.currentTick > 0)
            --this.currentTick;
    }

    @Override
    public float getProgress(float partialTicks)
    {
        return super.getProgress(partialTicks) / (float) duration;
    }

}
