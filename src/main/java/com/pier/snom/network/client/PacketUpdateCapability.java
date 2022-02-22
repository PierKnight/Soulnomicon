package com.pier.snom.network.client;


import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.network.PacketUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketUpdateCapability
{

    private UUID playerToUpdateUUID;
    private CompoundNBT capNBT;


    public PacketUpdateCapability(PlayerEntity playerToUpdate)
    {
        this.playerToUpdateUUID = playerToUpdate.getUniqueID();
        playerToUpdate.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer -> capNBT = soulPlayer.writeToNBT());
    }

    public PacketUpdateCapability(UUID playerToUpdateUUID, CompoundNBT capNBT)
    {
        this.playerToUpdateUUID = playerToUpdateUUID;
        this.capNBT = capNBT;
    }

    public static void encode(PacketUpdateCapability pkt, PacketBuffer buf)
    {
        buf.writeUniqueId(pkt.playerToUpdateUUID);
        buf.writeCompoundTag(pkt.capNBT);
    }

    public static PacketUpdateCapability decode(PacketBuffer buf)
    {
        return new PacketUpdateCapability(buf.readUniqueId(), buf.readCompoundTag());
    }

    public static class Handler
    {
        public static void handle(final PacketUpdateCapability pkt, Supplier<NetworkEvent.Context> ctx)
        {

            ctx.get().enqueueWork(() ->
            {

                PlayerEntity playerToUpdate;

                //player who received this packet
                PlayerEntity player = PacketUtils.getClientPlayer();

                World world = player.world;

                //get the player to update
                playerToUpdate = world.getPlayerByUuid(pkt.playerToUpdateUUID);


                //updates the capability on the client side
                if(playerToUpdate != null)
                    playerToUpdate.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer -> soulPlayer.readFromNBT(pkt.capNBT));


            });
            ctx.get().setPacketHandled(true);
        }
    }
}
