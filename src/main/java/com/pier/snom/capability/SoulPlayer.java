package com.pier.snom.capability;

import com.pier.snom.init.ModDamageSource;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.client.PacketUpdateCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

public class SoulPlayer implements ISoulPlayer
{



    private float maxHealth = 6F;
    private float health = 6F;



    @Override
    public float getMaxHealth()
    {
        return maxHealth;
    }

    @Override
    public void setMaxHealth(float maxHealth)
    {
        this.maxHealth = maxHealth;
    }

    @Override
    public void consumeSoul(PlayerEntity player, float amount)
    {
        if(!player.isCreative())
        {
            this.health -= amount;
            if(this.health <= 0)
            {
                player.attackEntityFrom(ModDamageSource.CONSUMED_SOUL, Float.MAX_VALUE);
                recoverSoul(player, maxHealth);
            }
        }
    }

    @Override
    public void recoverSoul(PlayerEntity player, float amount)
    {
        this.health += amount;
        if(this.health > this.maxHealth)
            this.health = this.maxHealth;

    }

    @Override
    public void useSoulHealth(PlayerEntity player, float amount)
    {
        this.health = MathHelper.clamp(this.health + amount, 0.0F, this.maxHealth);
        if(this.health == 0.0F)
        {
            player.attackEntityFrom(ModDamageSource.CONSUMED_SOUL, Float.MAX_VALUE);
        }

    }

    @Override
    public float getHealth()
    {
        return health;
    }


    @Override
    public CompoundNBT writeToNBT()
    {

        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("maxSoulHealth", this.maxHealth);
        nbt.putFloat("soulHealth", this.health);

        return nbt;
    }

    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        this.maxHealth = nbt.getFloat("maxSoulHealth");
        this.health = nbt.getFloat("soulHealth");

    }

    @Override
    public void update(PlayerEntity player)
    {

    }

    public static void updatePlayerData(PlayerEntity player)
    {
        PacketManager.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new PacketUpdateCapability(player));
    }
}


