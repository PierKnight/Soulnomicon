package com.pier.snom.network;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.network.client.PacketPlayControlSound;
import com.pier.snom.network.client.PacketUpdateCapability;
import com.pier.snom.network.client.PacketUpdateClairvoyance;
import com.pier.snom.network.server.*;
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
        channel.registerMessage(id++, PacketUseAbility.class, PacketUseAbility::encode, PacketUseAbility::decode, PacketUseAbility.Handler::handle);
        channel.registerMessage(id++, PacketSetAbility.class, PacketSetAbility::encode, PacketSetAbility::decode, PacketSetAbility.Handler::handle);
        channel.registerMessage(id++, PacketUpdateCapability.class, PacketUpdateCapability::encode, PacketUpdateCapability::decode, PacketUpdateCapability.Handler::handle);
        channel.registerMessage(id++, PacketScrollControlDistance.class, PacketScrollControlDistance::encode, PacketScrollControlDistance::decode, PacketScrollControlDistance.Handler::handle);
        channel.registerMessage(id++, PacketFlySoulnomicon.class, PacketFlySoulnomicon::encode, PacketFlySoulnomicon::decode, PacketFlySoulnomicon.Handler::handle);
        channel.registerMessage(id++, PacketPlayControlSound.class, PacketPlayControlSound::encode, PacketPlayControlSound::decode, PacketPlayControlSound.Handler::handle);
        channel.registerMessage(id++, PacketStartSearch.class, PacketStartSearch::encode, PacketStartSearch::decode, PacketStartSearch.Handler::handle);
        channel.registerMessage(id++, PacketUpdateClairvoyance.class, PacketUpdateClairvoyance::encode, PacketUpdateClairvoyance::decode, PacketUpdateClairvoyance.Handler::handle);
    }


}
