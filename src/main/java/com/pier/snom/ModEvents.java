package com.pier.snom;

import com.pier.snom.capability.PlayerData;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.SeparationAbility;
import com.pier.snom.entity.PlayerBodyEntity;
import com.pier.snom.init.ModBlocks;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.client.PacketUpdateCapability;
import com.pier.snom.world.save.DungeonDataSave;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class ModEvents
{

    private static final Predicate<LivingEntity> BODY_ENTITY_PREDICATE = entity ->
    {
        PlayerData data = ((PlayerBodyEntity) entity).getPlayerData();
        return entity.isAlive() && (data == null || !data.isCreative());
    };

    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof PlayerEntity)
            event.addCapability(new ResourceLocation(SoulnomiconMain.ID, "soul_cap"), new SoulPlayerProvider());

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {

        if(event.side.isServer() && event.phase == TickEvent.Phase.START)
        {
            DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(event.world);
            dungeonDataSave.updateDungeonChallenges(event.world);
        }
    }

    @SubscribeEvent
    public static void onExplosionEvent(ExplosionEvent.Detonate event)
    {
        DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(event.getWorld());
        if(dungeonDataSave != null)
            event.getAffectedBlocks().removeIf(dungeonDataSave::isBlockPosInsideDungeon);
    }

    @SubscribeEvent
    public static void dungeonCancelEvent(PlayerInteractEvent event)
    {
        World world = event.getWorld();


        if(!event.getWorld().isRemote && world.getBlockState(event.getPos()).getBlock() != ModBlocks.DUNGEON_BUTTON && !(event instanceof PlayerInteractEvent.RightClickItem) && !(event instanceof PlayerInteractEvent.LeftClickEmpty) && !(event instanceof PlayerInteractEvent.RightClickEmpty))
        {

            DungeonDataSave save = DungeonDataSave.getSave(world);
            if(save.isPlayerChallenging(event.getPlayer()))
                event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void interactCancel(PlayerInteractEvent event)
    {
        if(SeparationAbility.isSeparated(event.getPlayer()))
            event.setCancellationResult(ActionResultType.SUCCESS);
    }

    @SubscribeEvent
    public static void travelCancel(EntityTravelToDimensionEvent event)
    {
        Entity entity = event.getEntity();
        if(entity.getEntity() instanceof PlayerBodyEntity || (entity instanceof PlayerEntity && SeparationAbility.isSeparated((PlayerEntity) entity)))
            event.setCanceled(true);
    }


    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> player), new PacketUpdateCapability(player));
            SeparationAbility.logInBody(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        MinecraftServer server = world.getServer();
        if(server != null)
        {
            boolean isServer = server.isDedicatedServer() || server.getPublic();
            if(isServer)
                SeparationAbility.logOutBody(player);
        }
    }


    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if(event.getPlayer() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

            //player returns back to normal
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {
                if(soulPlayer.getAbilitiesManager().getSeparation().isSeparated)
                {
                    soulPlayer.recoverSoul(player, soulPlayer.getMaxHealth());
                    soulPlayer.getAbilitiesManager().getSeparation().deathSoulPlayerAnimation.ticks = 0;
                    soulPlayer.getAbilitiesManager().getSeparation().handleSeparation(player, false);
                }
            });


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

            //  PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketUpdateCapability(targetPl));

            PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketUpdateCapability(targetPl));

            System.out.println(event.getPlayer().getName() + " tracked: " + targetPl.getName());
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


    @SubscribeEvent
    public static void onSpawnChunk(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if(!entity.world.isRemote && entity instanceof MobEntity)
        {
            MobEntity mobEntity = (MobEntity) entity;

            if(entity instanceof IMob)
            {
                mobEntity.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(mobEntity, PlayerBodyEntity.class, 10, true, false, BODY_ENTITY_PREDICATE));
            }
        }
    }

    @SubscribeEvent
    public static void onPickUpItem(PlayerEvent.ItemPickupEvent event)
    {
        PlayerEntity player = event.getPlayer();
        learnNewItem(player, event.getStack());
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        PlayerEntity player = event.getPlayer();
        if(player instanceof ServerPlayerEntity)
            learnNewItem(player, event.getCrafting());
    }

    @SubscribeEvent
    public static void onItemCSmelted(PlayerEvent.ItemSmeltedEvent event)
    {
        PlayerEntity player = event.getPlayer();
        if(player instanceof ServerPlayerEntity)
            learnNewItem(player, event.getSmelting());
    }

    @SubscribeEvent
    public static void onChangeItemEvent(LivingEquipmentChangeEvent event)
    {
        Entity player = event.getEntity();
        if(!event.getTo().isEmpty() && player instanceof ServerPlayerEntity)
            learnNewItem((PlayerEntity) player, event.getTo());
    }

    private static void learnNewItem(PlayerEntity player, ItemStack stack)
    {
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(iSoulPlayer ->
        {
            iSoulPlayer.getAbilitiesManager().getClairvoyanceAbility().addItem(stack);
            PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketUpdateCapability(player));

        });
    }

}

