package com.pier.snom.init;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Random;

public class ModDamageSource
{

    public static final DamageSource CONSUMED_SOUL = new DamageSource("consumedSoul").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
    public static final DamageSource SOUL_MASTER = new SoulMasterSource();
    public static DamageSource causeSmashDamage(PlayerEntity attacker)
    {
        return new EntityDamageSource("smash", attacker);
    }


    private static final Random RAND = new Random();

    private static class SoulMasterSource extends DamageSource
    {

        public SoulMasterSource()
        {
            super("death.soul_master");
            this.setDamageBypassesArmor();
            this.setDamageIsAbsolute();
            this.setDamageAllowedInCreativeMode();
        }

        @Override
        @Nonnull
        public ITextComponent getDeathMessage(@Nonnull LivingEntity entityLivingBaseIn)
        {
            TranslationTextComponent bossNameComponent = new TranslationTextComponent("dungeon.boss.name");
            String bossName = "[" + bossNameComponent.getString() + "§f]";


            return new StringTextComponent(bossName + " " + new TranslationTextComponent(getDamageType() + RAND.nextInt(2),entityLivingBaseIn.getDisplayName()).getString());
        }

    }
}
