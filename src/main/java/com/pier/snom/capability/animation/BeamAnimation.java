package com.pier.snom.capability.animation;

import com.pier.snom.capability.abilities.BeamAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class BeamAnimation extends BaseAnimation
{


    private int prevRotationSpeed = 0;
    public int rotationSpeed = 0;


    public BeamAnimation()
    {
        super(30);
    }


    public void update(PlayerEntity player,BeamAbility ability)
    {
        this.prevTicks = this.ticks;

        if(ability.isActive())
        {
            if(ticks < maxTicks)
                this.ticks++;
        }
        else if(ticks > 0)
            this.ticks--;



        this.prevRotationSpeed = this.rotationSpeed;
        if(this.ticks >= 7)
        {
            float i = (this.ticks - 7F) / 23F;
            float speed = isSingleItem(player) ? 10F : 6F;
            this.rotationSpeed += i * speed;
        }

    }

    private boolean isSingleItem(PlayerEntity player)
    {
        return player.getHeldItemMainhand().isEmpty() || player.getHeldItemOffhand().isEmpty();
    }

    public float getItemRotationSpeed(float partialTicks)
    {
        return MathHelper.lerp(partialTicks,this.prevRotationSpeed,this.rotationSpeed);
    }
}
