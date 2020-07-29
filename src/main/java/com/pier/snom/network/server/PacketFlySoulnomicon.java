package com.pier.snom.network.server;

import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketFlySoulnomicon
{

    private final boolean shouldFly;

    public PacketFlySoulnomicon(boolean shouldFly)
    {
        this.shouldFly = shouldFly;
    }

    public static void encode(PacketFlySoulnomicon pkt, PacketBuffer buf)
    {
        buf.writeBoolean(pkt.shouldFly);
    }

    public static PacketFlySoulnomicon decode(PacketBuffer buf)
    {
        return new PacketFlySoulnomicon(buf.readBoolean());
    }


    public static class Handler
    {
        public static void handle(final PacketFlySoulnomicon pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                //getting player who receives the packet
                ServerPlayerEntity player = ctx.get().getSender();
                if(player != null)
                {

                    player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
                    {

                        if(pkt.shouldFly && !soulPlayer.getAbilitiesManager().bookFlyingAroundA.isFlying)
                            player.setHeldItem(player.getActiveHand(), ItemStack.EMPTY);
                        soulPlayer.getAbilitiesManager().bookFlyingAroundA.isFlying = pkt.shouldFly;
                    });
                    SoulPlayer.updatePlayerData(player);
                }
            });
            ctx.get().setPacketHandled(true);

        }


    }
}
