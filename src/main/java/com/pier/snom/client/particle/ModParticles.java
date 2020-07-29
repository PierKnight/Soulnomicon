package com.pier.snom.client.particle;

import com.pier.snom.SoulnomiconMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public class ModParticles
{

    static SoulPlayerParticleData SOUL_FLAME = new SoulPlayerParticleData();

    public static void registerParticleTypes(IForgeRegistry<ParticleType<?>> registry)
    {
        registry.register(SOUL_FLAME);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerParticles()
    {
        ParticleManager particleManager = Minecraft.getInstance().particles;
        particleManager.registerFactory(SOUL_FLAME, SoulFlameParticle.Factory::new);
    }


    public static class ModParticleType extends BasicParticleType
    {
        public ModParticleType(String particleName)
        {
            super(true);
            this.setRegistryName(SoulnomiconMain.ID, particleName);
        }
    }
}
