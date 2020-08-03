package com.pier.snom;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.SoulPlayerStorage;
import com.pier.snom.capability.render.SeparationAbilityRenderer;
import com.pier.snom.client.KeyBoardHandler;
import com.pier.snom.client.render.ModRenderingRegistry;
import com.pier.snom.network.PacketManager;
import com.pier.snom.world.structure.ModStructures;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SoulnomiconMain.ID)
public class SoulnomiconMain
{
    public static final String ID = "snom";
    private static final Logger LOGGER = LogManager.getLogger(ID);

    public SoulnomiconMain()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    }

    @SuppressWarnings("UnusedParameters")
    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("registering soul capability");
        CapabilityManager.INSTANCE.register(ISoulPlayer.class, new SoulPlayerStorage(), SoulPlayer::new);

        PacketManager.register();

        ModStructures.addStructure();
    }

    @SuppressWarnings("UnusedParameters")
    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModRenderingRegistry.registerRender();
        KeyBoardHandler.registerKeyBinding();

        SeparationAbilityRenderer.initReflection();


    }


    public static String getFormattedText(String unlocalizedName)
    {
        return new TranslationTextComponent(unlocalizedName).getFormattedText();
    }
}
