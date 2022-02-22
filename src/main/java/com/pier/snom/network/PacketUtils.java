package com.pier.snom.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class PacketUtils
{

    public static PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }



}
