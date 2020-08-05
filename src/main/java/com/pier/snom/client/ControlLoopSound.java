package com.pier.snom.client;

import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.ControlAbility;
import com.pier.snom.init.ModSounds;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class ControlLoopSound extends TickableSound
{
    private int ticks = 0;

    private final PlayerEntity player;
    private final Entity entity;

    public ControlLoopSound(PlayerEntity player, Entity entity)
    {
        super(ModSounds.CONTROLLING, SoundCategory.AMBIENT);
        this.repeat = true;
        this.repeatDelay = 0;
        this.entity = entity;
        this.pitch = 1F;
        this.player = player;
    }


    @Override
    public void tick()
    {

        this.volume = (float) this.ticks / 10F;

        this.x = (float) this.entity.getPosX();
        this.y = (float) this.entity.getPosY();
        this.z = (float) this.entity.getPosZ();

        this.player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            double speed = Math.min(entity.getPositionVec().distanceTo(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)),4D);

            this.pitch = 1F + ((float)speed / 4F) * 0.2F;

            ControlAbility controlAbility = soulPlayer.getAbilitiesManager().getControl();
            if(controlAbility.isControllingEntity())
            {
                if(this.ticks < 10)
                    this.ticks++;
            }
            else if(this.ticks > 0)
            {
                this.ticks--;
                if(this.ticks == 0)
                    this.donePlaying = true;
            }
        });
    }
}
