package com.pier.snom.network.server;

import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.ISoulAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUseAbility
{

    public PacketUseAbility()
    {
    }

    public static void encode(PacketUseAbility pkt, PacketBuffer buf)
    {

    }

    public static PacketUseAbility decode(PacketBuffer buf)
    {
        return new PacketUseAbility();
    }


    public static class Handler
    {
        public static void handle(final PacketUseAbility pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                //getting player who receives the packet
                ServerPlayerEntity player = ctx.get().getSender();
                useAbility(player);

            }); ctx.get().setPacketHandled(true);

        }


    }

    public static void useAbility(PlayerEntity player)
    {
        if(player != null)
        {
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {
                ISoulAbility ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
                if(ability != null && ability.canUse(player, soulPlayer))
                    if(soulPlayer.getAbilitiesManager().getSelectedAbility().cast(soulPlayer, player) && player instanceof ServerPlayerEntity)
                        SoulPlayer.updatePlayerData(player);
            });
        }
    }
}

