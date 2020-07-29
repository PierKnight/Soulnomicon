package com.pier.snom.client;

import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.init.ModSounds;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;

public class TestSound extends TickableSound
{
    private int ticks = 0;

    private final PlayerEntity player;
    private final Entity entity;

    public TestSound(PlayerEntity player, Entity entity)
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
            if(soulPlayer.getAbilitiesManager().getControl().isControllingEntity())
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
