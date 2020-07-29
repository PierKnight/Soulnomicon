package com.pier.snom.network.server;

import com.pier.snom.capability.SoulPlayerProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketStartSearch
{

    private final ItemStack stackToSearch;
    private final boolean ignoreNBT;

    public PacketStartSearch(ItemStack stackToSearch, boolean ignoreNBT)
    {
        this.stackToSearch = stackToSearch;
        this.ignoreNBT = ignoreNBT;
    }

    public static void encode(PacketStartSearch pkt, PacketBuffer buf)
    {
        buf.writeCompoundTag(pkt.stackToSearch.write(new CompoundNBT()));
        buf.writeBoolean(pkt.ignoreNBT);
    }

    public static PacketStartSearch decode(PacketBuffer buf)
    {
        return new PacketStartSearch(ItemStack.read(buf.readCompoundTag()), buf.readBoolean());
    }


    public static class Handler
    {
        public static void handle(final PacketStartSearch pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->
            {
                //getting player who receives the packet
                ServerPlayerEntity player = ctx.get().getSender();
                if(player != null)
                {
                    player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer -> soulPlayer.getAbilitiesManager().getClairvoyanceAbility().startSearch(pkt.stackToSearch, pkt.ignoreNBT));
                    player.swingArm(Hand.MAIN_HAND);
                }
            });
            ctx.get().setPacketHandled(true);

        }


    }
}
