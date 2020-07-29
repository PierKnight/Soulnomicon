package com.pier.snom.client;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.SeparationAbility;
import com.pier.snom.client.gui.AbilityWheelScreen;
import com.pier.snom.init.ModItems;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.server.PacketScrollControlDistance;
import com.pier.snom.network.server.PacketUseAbility;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class KeyBoardHandler
{

    public static final KeyBinding SOUL_KEYBINDING = new KeyBinding("key.soul", 77, "key.categories.soul");

    private static int prevPersonView = -1;

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null)
            return;

        if(SeparationAbility.isSeparated(player))
            event.setCanceled(true);
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            if(soulPlayer.getAbilitiesManager().getControl().isControllingEntity())
            {
                PacketManager.channel.sendToServer(new PacketScrollControlDistance(event.getScrollDelta()));
                event.setCanceled(true);
            }
        });

    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();

        PlayerEntity player = mc.player;
        if(player == null)
            return;

        if(event.phase == TickEvent.Phase.END)
            return;


        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            if(showAbilityWheel(player, soulPlayer))
            {
                if(mc.currentScreen == null)
                {
                    mc.deferTask(() -> mc.displayGuiScreen(new AbilityWheelScreen(player)));
                }
            }

            if(soulPlayer.getAbilitiesManager().getSeparation().isSeparated && soulPlayer.getHealth() == 0.0F)
            {
                if(prevPersonView == -1)
                {
                    prevPersonView = Minecraft.getInstance().gameSettings.thirdPersonView;
                    Minecraft.getInstance().gameSettings.thirdPersonView = 1;
                }
            }
            else if(prevPersonView != -1)
            {
                Minecraft.getInstance().gameSettings.thirdPersonView = prevPersonView;
                prevPersonView = -1;
            }


        });


    }

    /**
     * checks if the ability wheel should be rendered
     */
    private static boolean showAbilityWheel(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return soulPlayer.getAbilitiesManager().bookFlyingAroundA.isFlying && !SOUL_KEYBINDING.isKeyDown() && Minecraft.getInstance().gameSettings.keyBindUseItem.isKeyDown() && player.getHeldItemMainhand().isEmpty() || player.getActiveItemStack().getItem() == ModItems.SOULNOMICON;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if(player == null)
            return;
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            if(soulPlayer.getAbilitiesManager().getSeparation().isSeparated)
            {
                GameSettings settings = minecraft.gameSettings;

                for (KeyBinding key : settings.keyBindings)
                {
                    String keyCategory = key.getKeyCategory();
                    if(!key.equals(SOUL_KEYBINDING) && (soulPlayer.getHealth() == 0.0F || !keyCategory.equals("key.categories.movement") && !keyCategory.equals("key.categories.misc") && !keyCategory.equals("key.categories.multiplayer")))
                    {
                        if(key.isPressed())
                            KeyBinding.setKeyBindState(key.getKey(), false);
                    }
                }
            }
        });


        if(SOUL_KEYBINDING.isPressed() && minecraft.currentScreen == null)
        {
            PacketUseAbility.useAbility(player);
            PacketManager.channel.sendToServer(new PacketUseAbility());
        }


    }

    public static void registerKeyBinding()
    {
        ClientRegistry.registerKeyBinding(SOUL_KEYBINDING);
    }

}
