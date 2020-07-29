package com.pier.snom.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SoulPlayerStorage implements Capability.IStorage<ISoulPlayer>
{
    @Nullable
    @Override
    public INBT writeNBT(Capability<ISoulPlayer> capability, ISoulPlayer instance, Direction side)
    {
        return instance.writeToNBT();
    }

    @Override
    public void readNBT(Capability<ISoulPlayer> capability, ISoulPlayer instance, Direction side, INBT nbt)
    {

        CompoundNBT tag = (CompoundNBT) nbt;
        instance.readFromNBT(tag);

    }
}
