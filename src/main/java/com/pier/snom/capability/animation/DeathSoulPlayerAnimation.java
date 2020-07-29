package com.pier.snom.capability.animation;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.init.ModDamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class DeathSoulPlayerAnimation extends BaseAnimation
{
    public DeathSoulPlayerAnimation()
    {
        super(30);
    }

    @Override
    public void update(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        this.prevTicks = ticks;
        if(soulPlayer.getHealth() == 0.0F && soulPlayer.getAbilitiesManager().getSeparation().isSeparated)
        {
            if(this.ticks < maxTicks)
            {
                this.ticks++;
            }
            else
            {
                player.attackEntityFrom(ModDamageSource.CONSUMED_SOUL, Float.MAX_VALUE);
            }
        }
    }
}
