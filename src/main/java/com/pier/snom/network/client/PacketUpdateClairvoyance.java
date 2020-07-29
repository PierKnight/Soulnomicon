package com.pier.snom.network.client;

import com.google.common.collect.Sets;
import com.pier.snom.capability.abilities.ClairvoyanceAbility;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public class PacketUpdateClairvoyance
{
    private final Set<BlockPos> positions;

    public PacketUpdateClairvoyance(Set<BlockPos> positions)
    {
        this.positions = positions;
    }

    public static void encode(PacketUpdateClairvoyance pkt, PacketBuffer buf)
    {
        buf.writeInt(pkt.positions.size());

        for (BlockPos pos : pkt.positions)
            buf.writeBlockPos(pos);
    }

    public static PacketUpdateClairvoyance decode(PacketBuffer buf)
    {
        Set<BlockPos> positions = Sets.newHashSet();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
            positions.add(buf.readBlockPos());


        return new PacketUpdateClairvoyance(positions);
    }

    public static class Handler
    {
        public static void handle(final PacketUpdateClairvoyance pkt, Supplier<NetworkEvent.Context> ctx)
        {

            ctx.get().enqueueWork(() -> ClairvoyanceAbility.highLightedPositions = pkt.positions);
            ctx.get().setPacketHandled(true);
        }
    }
}
