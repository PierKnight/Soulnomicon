package com.pier.snom.capability.abilities;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.PlayerData;
import com.pier.snom.capability.SoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.animation.DeathSoulPlayerAnimation;
import com.pier.snom.capability.render.SeparationAbilityRenderer;
import com.pier.snom.client.particle.SoulPlayerParticleData;
import com.pier.snom.entity.PlayerBodyEntity;
import com.pier.snom.world.SavedBodyPlayers;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class SeparationAbility implements ISoulAbility<SeparationAbilityRenderer>
{


    public boolean isSeparated = false;
    private GameType oldGameType = GameType.SURVIVAL;


    public DeathSoulPlayerAnimation deathSoulPlayerAnimation = new DeathSoulPlayerAnimation();


    @Override
    public EnumAbility getAbility()
    {
        return EnumAbility.SEPARATION;
    }

    /**
     * This method inverts the player separation state (separated to normal - normal to separated)
     *
     * @param teleport if true, the player will be teleported to the body current position (this is false only when player dies)
     */
    public void handleSeparation(ServerPlayerEntity player, boolean teleport)
    {
        World world = player.world;

        if(isSeparated)
        {
            isSeparated = false;

            //return player's gametype back to normal
            player.setGameType(oldGameType);


            //get the corresponding player's body(which is currently a soul)
            PlayerBodyEntity playerBodyEntity = getPlayerBodyBody(player);
            if(playerBodyEntity != null)
            {
                PlayerData data = playerBodyEntity.getPlayerData();

                //revert body's stats back to the player
                if(data != null)
                    data.handleUnion(playerBodyEntity, player);

                //despawn body
                playerBodyEntity.remove();

                //teleport the player to the body's location
                if(teleport)
                    player.setPositionAndUpdate(playerBodyEntity.getPosX(), playerBodyEntity.getPosY(), playerBodyEntity.getPosZ());
            }

        }
        else
        {

            isSeparated = true;

            //save current player gametype (it will be used when he will return back to normal)
            oldGameType = player.interactionManager.getGameType();

            //spawn player's body
            PlayerBodyEntity bodyEntity = new PlayerBodyEntity(world,player.rotationYaw, player.rotationYawHead, player.rotationPitch);
            bodyEntity.setPlayerSoul(player);
            world.addEntity(bodyEntity);

            PlayerData.handleSeparation(bodyEntity, player);


        }

        SoulPlayer.updatePlayerData(player);

    }

    @Override
    public boolean canUse(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return (!player.isSpectator() || isSeparated) && soulPlayer.getHealth() > 0.0F;
    }

    @Override
    public void onUpdate(ISoulPlayer soulPlayer, PlayerEntity player)
    {

        deathSoulPlayerAnimation.update(player, soulPlayer);

        if(isSeparated)
        {
            if(player instanceof ServerPlayerEntity)
            {
                if(player.abilities.getFlySpeed() != 0.02F)
                {
                    SPlayerAbilitiesPacket packet = new SPlayerAbilitiesPacket(player.abilities);
                    packet.setFlySpeed(0.02F);
                    ((ServerPlayerEntity) player).connection.sendPacket(packet);
                }


                player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, false, false));
            }

            player.setSprinting(false);
            if(!player.isSpectator())
                player.setGameType(GameType.SPECTATOR);

            SoulPlayerParticleData soulFlame = new SoulPlayerParticleData(null);

            if(player.world.isRemote && this.deathSoulPlayerAnimation.ticks > 0 && player.isAlive())
            {
                float t = this.deathSoulPlayerAnimation.getAnimationF();
                for (int i = 0; i < (int) (t * 7); i++)
                {
                    Random rand = new Random();
                    double pX = player.getPosX() + (rand.nextDouble() - 0.5D) * player.getWidth();
                    double pY = player.getPosY() + rand.nextDouble() * player.getHeight();
                    double pZ = player.getPosZ() + (rand.nextDouble() - 0.5D) * player.getWidth();
                    Vec3d center = player.getBoundingBox().getCenter();

                    double motionX = pX - center.x;
                    double motionY = pY - center.y;
                    double motionZ = pZ - center.z;
                    Vec3d velocity = new Vec3d(motionX, motionY, motionZ).mul(t * 0.7D, t * 0.5D, t * 0.7D);

                    player.world.addParticle(soulFlame, true, pX, pY, pZ, velocity.x, velocity.y, velocity.z);
                }
            }

            if(player.world.getGameTime() % 10 == 0 && oldGameType != GameType.CREATIVE)
            {
                soulPlayer.useSoulHealth(player, -0.25F);
            }

        }

    }

    @Override
    public boolean cast(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        if(player instanceof ServerPlayerEntity)
            handleSeparation((ServerPlayerEntity) player, true);
        return false;
    }

    @Override
    public float soulUsePreview(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        if(isSeparated)
            return -0.25F;
        return 0F;
    }

    @Override
    public boolean shouldRegenPlayer(PlayerEntity player, ISoulPlayer iSoulPlayer)
    {
        return !this.isSeparated;
    }

    @Override
    public boolean shouldBlockInteractions(PlayerEntity player, ISoulPlayer iSoulPlayer)
    {
        return this.isSeparated;
    }

    public static boolean isSeparated(PlayerEntity player)
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer -> atomicBoolean.lazySet(soulPlayer.getAbilitiesManager().getSeparation().isSeparated));
        return atomicBoolean.get();
    }




    @Nullable
    public static PlayerBodyEntity getPlayerBodyBody(PlayerEntity player)
    {

        World world = player.world;

        if(world instanceof ServerWorld)
        {
            ServerWorld serverWorld = (ServerWorld) world;

            Stream<Entity> entities = serverWorld.getEntities();
            return getPlayerBodyFromIterator(entities.iterator(), player);

        }
        else if(world instanceof ClientWorld)
        {
            ClientWorld clientWorld = (ClientWorld) world;
            return getPlayerBodyFromIterator(clientWorld.getAllEntities().iterator(), player);
        }

        return null;

    }

    @Nullable
    private static PlayerBodyEntity getPlayerBodyFromIterator(Iterator<Entity> iterator, PlayerEntity player)
    {
        while(iterator.hasNext())
        {
            Entity entity = iterator.next();
            if(entity instanceof PlayerBodyEntity)
            {
                PlayerBodyEntity body = (PlayerBodyEntity) entity;
                PlayerData playerData = body.getPlayerData();
                if(playerData != null && playerData.getPlayerUUID().equals(player.getUniqueID()))
                    return body;
            }
        }
        return null;
    }


    public static void logOutBody(PlayerEntity player)
    {
        PlayerBodyEntity body = getPlayerBodyBody(player);
        if(body != null)
            body.logOutBody();
    }

    public static void logInBody(PlayerEntity player)
    {
        SavedBodyPlayers save = SavedBodyPlayers.getSave(player.world);
        save.respawnSavedBody(player);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        nbt.putBoolean("isSeparated", isSeparated);
        nbt.putInt("playerGameType", oldGameType.getID());
        this.deathSoulPlayerAnimation.writeToNBT(nbt, "deathAnimation");
    }

    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        isSeparated = nbt.getBoolean("isSeparated");
        oldGameType = GameType.getByID(nbt.getInt("playerGameType"));

        this.deathSoulPlayerAnimation.readFromNBT(nbt, "deathAnimation");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SeparationAbilityRenderer getRenderer()
    {
        return new SeparationAbilityRenderer(this);
    }


}
