package com.pier.snom.network.server;

import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.ControlAbility;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketScrollControlDistance
{

    private final double scrollDelta;

    public PacketScrollControlDistance(double scrollDelta)
    {
        this.scrollDelta = scrollDelta;
    }

    public static void encode(PacketScrollControlDistance pkt, PacketBuffer buf)
    {
        buf.writeDouble(pkt.scrollDelta);
    }

    public static PacketScrollControlDistance decode(PacketBuffer buf)
    {
        return new PacketScrollControlDistance(buf.readDouble());
    }


    public static class Handler
    {
        public static void handle(final PacketScrollControlDistance pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                //getting player who receives the packet
                ServerPlayerEntity player = ctx.get().getSender();
                if(player != null)
                    player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
                    {
                        double currentDistance = soulPlayer.getAbilitiesManager().getControl().distance;
                        soulPlayer.getAbilitiesManager().getControl().distance = MathHelper.clamp(currentDistance + pkt.scrollDelta * 0.5F, 1D, ControlAbility.MAX_REACH_DISTANCE);
                    });
            });
            ctx.get().setPacketHandled(true);

        }


    }
}

