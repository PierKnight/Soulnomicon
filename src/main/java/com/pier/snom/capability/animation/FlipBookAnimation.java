package com.pier.snom.capability.animation;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.client.ClientEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlipBookAnimation extends BaseAnimation
{
    public boolean isFlipping = false;
    private boolean forward = false;

    public int textTick = 0;
    private int prevTextTick = 0;

    public FlipBookAnimation()
    {
        super(4);
    }


    @Override
    public void update(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        this.prevTicks = ticks;
        this.prevTextTick = textTick;


        if(isFlipping)
        {
            this.ticks++;
            if(textTick < 2)
                this.textTick++;

        }
        else if(this.textTick > 0)
            this.textTick--;


        if(ticks > this.maxTicks)
        {
            this.isFlipping = false;
            this.ticks = 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getTextF()
    {
        return MathHelper.lerp(ClientEvents.getPartialTicks(), this.prevTextTick, this.textTick) / (maxTicks / 2F);
    }


    public float getFlipF()
    {
        float f = getAnimationF();
        if(!forward)
            return 1F - f;
        return f;
    }

    public void flip(boolean forward)
    {
        this.forward = forward;
        this.isFlipping = true;
    }

    @Override
    public boolean shouldAnimate(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return isFlipping;
    }
}
