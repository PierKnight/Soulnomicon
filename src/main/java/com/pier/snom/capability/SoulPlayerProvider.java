package com.pier.snom.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@SuppressWarnings("ConstantConditions")
public class SoulPlayerProvider implements ICapabilitySerializable<INBT>
{
    @CapabilityInject(ISoulPlayer.class)
    @Nonnull
    public static final Capability<ISoulPlayer> SOUL_PLAYER_CAPABILITY = null;

    private final LazyOptional<ISoulPlayer> instance;

    public SoulPlayerProvider()
    {
        this.instance = LazyOptional.of(SOUL_PLAYER_CAPABILITY::getDefaultInstance).cast();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        return cap == SOUL_PLAYER_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }


    @Override
    public INBT serializeNBT()
    {
        return SOUL_PLAYER_CAPABILITY.getStorage().writeNBT(SOUL_PLAYER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);

    }

    @Override
    public void deserializeNBT(INBT nbt)
    {
        SOUL_PLAYER_CAPABILITY.getStorage().readNBT(SOUL_PLAYER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);

    }
}
