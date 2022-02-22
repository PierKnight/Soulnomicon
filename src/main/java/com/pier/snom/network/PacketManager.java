package com.pier.snom.network;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.network.client.PacketUpdateCapability;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketManager
{

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(SoulnomiconMain.ID, "soulnomicon_channel")).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

    private static int id = 0;

    public static void register()
    {
        channel.registerMessage(id++, PacketUpdateCapability.class, PacketUpdateCapability::encode, PacketUpdateCapability::decode, PacketUpdateCapability.Handler::handle);
    }


}
