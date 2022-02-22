package com.pier.snom.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

/**
 * Interface used to implement a new player ability
 */
public interface ISoulPlayer
{

    CompoundNBT writeToNBT();

    void readFromNBT(CompoundNBT nbt);

    void update(PlayerEntity player);

    float getMaxHealth();

    void setMaxHealth(float maxHealth);

    void consumeSoul(PlayerEntity player, float amount);

    void recoverSoul(PlayerEntity player, float amount);

    void useSoulHealth(PlayerEntity player, float amount);

    float getHealth();

}
