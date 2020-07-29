package com.pier.snom.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pier.snom.SoulnomiconMain;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.UUID;


public class SoulPlayerParticleData extends ParticleType<SoulPlayerParticleData> implements IParticleData
{

    private static final IParticleData.IDeserializer<SoulPlayerParticleData> DESERIALIZER = new IParticleData.IDeserializer<SoulPlayerParticleData>()
    {
        public SoulPlayerParticleData deserialize(ParticleType<SoulPlayerParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
        {
            reader.expect(' ');
            String soulPlayerUUID = reader.readString();
            UUID uuid = soulPlayerUUID.equalsIgnoreCase("null") ? null : UUID.fromString(soulPlayerUUID);
            return new SoulPlayerParticleData(uuid);
        }

        public SoulPlayerParticleData read(ParticleType<SoulPlayerParticleData> particleTypeIn, PacketBuffer buffer)
        {
            return new SoulPlayerParticleData(buffer.readUniqueId());
        }
    };

    private final UUID soulPlayerUUID;


    public SoulPlayerParticleData(@Nullable UUID soulPlayerUUID)
    {
        super(true, DESERIALIZER);
        this.soulPlayerUUID = soulPlayerUUID;


    }

    public SoulPlayerParticleData()
    {
        this(null);
        this.setRegistryName(SoulnomiconMain.ID, "soul_flame");
    }

    @OnlyIn(Dist.CLIENT)
    UUID getSoulPlayerUUID()
    {
        return soulPlayerUUID;
    }

    @Override
    public ParticleType<?> getType()
    {
        return ModParticles.SOUL_FLAME;
    }

    @Override
    public void write(PacketBuffer buffer)
    {
        if(this.soulPlayerUUID != null)
            buffer.writeUniqueId(this.soulPlayerUUID);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getParameters()
    {
        String parameter = Registry.PARTICLE_TYPE.getKey(this).toString();
        if(this.soulPlayerUUID != null)
            return String.format(Locale.ROOT, "%s %s", parameter, this.soulPlayerUUID);
        return parameter;
    }
}