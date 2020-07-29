package com.pier.snom.network.client;

import com.pier.snom.client.TestSound;
import com.pier.snom.network.PacketUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPlayControlSound
{
    private int entityID = 0;

    public PacketPlayControlSound(int entityID)
    {
        this.entityID = entityID;
    }

    public static void encode(PacketPlayControlSound pkt, PacketBuffer buf)
    {
        buf.writeInt(pkt.entityID);
    }

    public static PacketPlayControlSound decode(PacketBuffer buf)
    {
        return new PacketPlayControlSound(buf.readInt());
    }

    public static class Handler
    {
        public static void handle(final PacketPlayControlSound pkt, Supplier<NetworkEvent.Context> ctx)
        {

            ctx.get().enqueueWork(() ->
            {

                PlayerEntity player = PacketUtils.getClientPlayer();
                Entity entity = player.world.getEntityByID(pkt.entityID);
                if(entity != null)
                    PacketUtils.getSoundHandler().play(new TestSound(player, entity));


            });
            ctx.get().setPacketHandled(true);
        }
    }
}
