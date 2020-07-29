package com.pier.snom.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.PlayerEntity;

public class PacketUtils
{

    public static PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static SoundHandler getSoundHandler()
    {
        return Minecraft.getInstance().getSoundHandler();
    }

}
