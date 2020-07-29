package com.pier.snom.network.server;

import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetAbility
{

    private final int abilityIndex;

    public PacketSetAbility(int abilityIndex)
    {
        this.abilityIndex = abilityIndex;
    }

    public static void encode(PacketSetAbility pkt, PacketBuffer buf)
    {
        buf.writeInt(pkt.abilityIndex);
    }

    public static PacketSetAbility decode(PacketBuffer buf)
    {
        return new PacketSetAbility(buf.readInt());
    }


    public static class Handler
    {
        public static void handle(final PacketSetAbility pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                //getting player who receives the packet
                ServerPlayerEntity player = ctx.get().getSender();
                if(player != null)
                {
                    player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
                    {
                        soulPlayer.getAbilitiesManager().setSelectedAbility(player, pkt.abilityIndex);

                    });
                    SoulPlayer.updatePlayerData(player);
                }
            });
            ctx.get().setPacketHandled(true);

        }


    }
}
