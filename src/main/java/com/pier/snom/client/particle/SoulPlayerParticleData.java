package com.pier.snom.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.pier.snom.SoulnomiconMain;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;


public class SoulPlayerParticleData extends ParticleType<SoulPlayerParticleData> implements IParticleData
{

    private static final IParticleData.IDeserializer<SoulPlayerParticleData> DESERIALIZER = new IParticleData.IDeserializer<SoulPlayerParticleData>()
    {
        public SoulPlayerParticleData deserialize(ParticleType<SoulPlayerParticleData> particleTypeIn, StringReader reader)
        {
            return new SoulPlayerParticleData();
        }

        public SoulPlayerParticleData read(ParticleType<SoulPlayerParticleData> particleTypeIn, PacketBuffer buffer)
        {
            return new SoulPlayerParticleData();
        }
    };


    public SoulPlayerParticleData()
    {
        super(true, DESERIALIZER);
        this.setRegistryName(SoulnomiconMain.ID, "soul_flame");
    }


    @Override
    public ParticleType<?> getType()
    {
        return ModParticles.SOUL_FLAME;
    }

    @Override
    public void write(PacketBuffer buffer)
    {
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getParameters()
    {
        return Registry.PARTICLE_TYPE.getKey(this).toString();
    }

    @Override
    public Codec<SoulPlayerParticleData> func_230522_e_()
    {
        return Codec.unit(this);
    }
}