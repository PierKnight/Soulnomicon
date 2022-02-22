package com.pier.snom;

import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.client.PacketUpdateCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;


public class ModEvents
{


    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof PlayerEntity)
            event.addCapability(new ResourceLocation(SoulnomiconMain.ID, "soul_cap"), new SoulPlayerProvider());

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {

        /*
        if(event.side.isServer() && event.phase == TickEvent.Phase.START)
        {
            DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(event.world);
            dungeonDataSave.updateDungeonChallenges(event.world);
        }

         */
    }

    @SubscribeEvent
    public static void onExplosionEvent(ExplosionEvent.Detonate event)
    {
        /*
        DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(event.getWorld());
        if(dungeonDataSave != null)
            event.getAffectedBlocks().removeIf(dungeonDataSave::isBlockPosInsideDungeon);

         */
    }

    @SubscribeEvent
    public static void dungeonCancelEvent(PlayerInteractEvent event)
    {
        /*
        World world = event.getWorld();

        if(!event.getWorld().isRemote && world.getBlockState(event.getPos()).getBlock() != ModBlocks.DUNGEON_BUTTON && !(event instanceof PlayerInteractEvent.RightClickItem) && !(event instanceof PlayerInteractEvent.LeftClickEmpty) && !(event instanceof PlayerInteractEvent.RightClickEmpty))
        {

            DungeonDataSave save = DungeonDataSave.getSave(world);
            if(save.isPlayerChallenging(event.getPlayer()))
                event.setCanceled(true);
        }

         */
    }




    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> player), new PacketUpdateCapability(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> player), new PacketUpdateCapability(player));
        }

    }

    /**
     * update all tracking players of the player capability
     */
    @SubscribeEvent
    public static void updateClientCapability(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity targetPl = (ServerPlayerEntity) event.getTarget();
            PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketUpdateCapability(targetPl));

        }

    }


    /**
     * saves the capability after death
     */
    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event)
    {
        if(event.isWasDeath())
            event.getOriginal().getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(oldCap -> event.getPlayer().getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(newCap -> newCap.readFromNBT(oldCap.writeToNBT())));
    }

    @SubscribeEvent
    public static void updateSoulPlayerState(TickEvent.PlayerTickEvent event)
    {

        if(event.phase == TickEvent.Phase.END)
        {
            PlayerEntity player = event.player;
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer -> soulPlayer.update(player));
        }
    }




}

