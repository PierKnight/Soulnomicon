package com.pier.snom.capability.animation;

import com.pier.snom.capability.abilities.ClairvoyanceAbility;

public class ClairvoyanceScan extends BaseAnimation
{

    private boolean scanDone = false;

    public ClairvoyanceScan()
    {
        super(30);
    }

    public void update(ClairvoyanceAbility ability)
    {
        this.prevTicks = this.ticks;
        if(ability.remainingSearchTime > 0 && !scanDone)
        {
            this.ticks++;
            if(this.ticks > this.maxTicks)
            {
                this.ticks = 0;
                this.scanDone = true;
            }
        }
    }

    public boolean isScanning()
    {
        return !this.scanDone;
    }

    public void startScan()
    {
        this.prevTicks = 0;
        this.ticks = 0;
        this.scanDone = false;
    }


}
