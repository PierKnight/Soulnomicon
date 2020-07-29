package com.pier.snom.capability.animation;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class BookOpenAnimation extends BaseAnimation
{

    public BookOpenAnimation()
    {
        super(5);
    }

    @Override
    public void update(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        World world = player.world;
        if(!world.isRemote && this.ticks == 1 && soulPlayer.getAbilitiesManager().selectedAbilityIndex == 0)
            world.playSound(null, player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ(), ModSounds.BOOK_CLOSE, SoundCategory.PLAYERS, 1F, 1.1F);
        super.update(player, soulPlayer);
    }

    @Override
    public boolean shouldAnimate(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return soulPlayer.getAbilitiesManager().selectedAbilityIndex > 0;
    }
}
