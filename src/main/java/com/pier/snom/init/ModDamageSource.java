package com.pier.snom.init;

import com.pier.snom.entity.PlayerBodyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class ModDamageSource
{

    public static final DamageSource CONSUMED_SOUL = new DamageSource("consumedSoul").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

    public static DamageSource causeSmashDamage(PlayerEntity attacker)
    {
        return new EntityDamageSource("smash", attacker);
    }

    public static class BodyDamageSource extends DamageSource
    {
        private final PlayerBodyEntity playerBodyEntity;

        public BodyDamageSource(PlayerBodyEntity playerBodyEntity)
        {
            super("bodyDeath");
            this.playerBodyEntity = playerBodyEntity;
        }


        @Override
        @Nonnull
        public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn)
        {
            return playerBodyEntity.getCombatTracker().getDeathMessage();
        }


    }
}
