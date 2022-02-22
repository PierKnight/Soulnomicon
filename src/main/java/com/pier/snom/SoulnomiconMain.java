package com.pier.snom;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.SoulPlayerStorage;
import com.pier.snom.client.ClientSetup;
import com.pier.snom.network.PacketManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SoulnomiconMain.ID)
public class SoulnomiconMain
{
    public static final String ID = "snom";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public SoulnomiconMain()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> ClientSetup::new);

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            LOGGER.info("registering soul capability");
            CapabilityManager.INSTANCE.register(ISoulPlayer.class, new SoulPlayerStorage(), SoulPlayer::new);
            PacketManager.register();
            // ModStructures.addStructure();
        });
    }
}
