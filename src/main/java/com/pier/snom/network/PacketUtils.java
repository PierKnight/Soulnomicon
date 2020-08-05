package com.pier.snom.network;

import com.pier.snom.client.ControlLoopSound;
import com.pier.snom.client.gui.ClairvoyanceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class PacketUtils
{

    public static PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static void playControlSound(PlayerEntity player, Entity entity)
    {
         Minecraft.getInstance().getSoundHandler().play(new ControlLoopSound(player, entity));
    }

    public static void displayClairvoyanceAbility(PlayerEntity player, NonNullList<ItemStack> items)
    {
        Minecraft.getInstance().displayGuiScreen(new ClairvoyanceScreen(player, items));

    }


}
