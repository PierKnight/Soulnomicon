package com.pier.snom.capability;

import com.pier.snom.entity.PlayerBodyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.GameType;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.UUID;


/**
 * this class stores the player original data before switching to the body
 */
public class PlayerData
{


    public final CompoundNBT playerTag;


    public PlayerData(CompoundNBT playerTag)
    {
        this.playerTag = playerTag;
    }


    public UUID getPlayerUUID()
    {
        return playerTag.getUniqueId("UniqueUUID");
    }

    public String getPlayerName()
    {
        return playerTag.getString("PlayerName");
    }

    public boolean isCreative()
    {
        return playerTag.getBoolean("isCreative");
    }

    public boolean isFlying()
    {
        if(playerTag.contains("abilities", 10))
        {
            CompoundNBT tag = playerTag.getCompound("abilities");
            return tag.getBoolean("flying");
        }
        return false;
    }

    public boolean isInvulnerable() { return playerTag.getBoolean("Invulnerable");}



    /**
     * this method transfers the player data to the body
     */
    public static void handleSeparation(PlayerBodyEntity body, PlayerEntity player)
    {


        player.inventory.clear();

        body.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(player.getMaxHealth());
        body.setHealth(player.getHealth());
        body.setFire(player.getFireTimer());
        body.setAbsorptionAmount(player.getAbsorptionAmount());

        body.fallDistance = player.fallDistance;

        body.setMotion(player.getMotion());
        body.velocityChanged = true;

        body.setAir(player.getAir());

        Entity ridingEntity = player.getRidingEntity();

        if(ridingEntity != null)
        {
            player.stopRiding();
            body.startRiding(ridingEntity);
        }


        if(!body.world.isRemote)
        {
            for (EffectInstance effectInstance : player.getActivePotionEffects())
                body.addPotionEffect(effectInstance);
            player.clearActivePotions();
        }


        player.setGameType(GameType.SPECTATOR);

        player.setHealth(player.getMaxHealth());
        player.setAbsorptionAmount(0.0F);
        player.extinguish();


        CapabilityDispatcher dispatcher = ForgeEventFactory.gatherCapabilities(Entity.class, player, null);
        if(dispatcher != null)
            dispatcher.deserializeNBT(new CompoundNBT());



    }

    /**
     * this method transfers the body data back to the player
     */
    public void handleUnion(PlayerBodyEntity body, PlayerEntity player)
    {
        player.setHealth(body.getHealth());
        player.setAbsorptionAmount(body.getAbsorptionAmount());

        if(!body.world.isRemote)
        {
            player.clearActivePotions();
            for (EffectInstance effectInstance : body.getActivePotionEffects())
                player.addPotionEffect(effectInstance);
        }

        player.fallDistance = body.fallDistance;

        player.setMotion(body.getMotion());
        player.velocityChanged = true;

        player.setAir(body.getAir());

        Entity ridingEntity = body.getRidingEntity();

        if(ridingEntity != null)
        {
            body.stopRiding();
            player.startRiding(ridingEntity);
        }

        player.getFoodStats().read(playerTag);
        player.setFire(body.getFireTimer());

        player.abilities.isFlying = isFlying();

        readPlayerInventory(player);
        readPlayerGameType(player);

        CapabilityDispatcher dispatcher = ForgeEventFactory.gatherCapabilities(Entity.class, player, null);
        if(playerTag.contains("ForgeCaps", 10) && dispatcher != null)
            dispatcher.deserializeNBT(playerTag.getCompound("ForgeCaps"));


    }

    public void readPlayerGameType(PlayerEntity player)
    {
        if(playerTag.contains("playerGameType", 99))
        {
            player.setGameType(GameType.getByID(playerTag.getInt("playerGameType")));
        }
    }

    public void readPlayerInventory(PlayerEntity player)
    {
        ListNBT listnbt = playerTag.getList("Inventory", 10);
        player.inventory.read(listnbt);
    }


}
