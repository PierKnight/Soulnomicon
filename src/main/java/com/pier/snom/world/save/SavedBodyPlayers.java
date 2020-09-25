package com.pier.snom.world.save;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.PlayerData;
import com.pier.snom.entity.PlayerBodyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SavedBodyPlayers extends WorldSavedData
{

    private static final String NAME = SoulnomiconMain.ID + "_saved_bodies";

    private Map<UUID, CompoundNBT> playerBodies = new HashMap<>();

    public SavedBodyPlayers(String name)
    {
        super(name);
    }


    public static SavedBodyPlayers getSave(World world)
    {
        if(world instanceof ServerWorld)
        {
            ServerWorld serverWorld = (ServerWorld) world;
            SavedBodyPlayers result = serverWorld.getSavedData().get(() -> new SavedBodyPlayers(NAME), NAME);
            if(result == null)
            {
                result = new SavedBodyPlayers(NAME);
                serverWorld.getSavedData().set(result);
            }
            return result;
        }
        return null;
    }

    public void savePlayerBody(PlayerData playerData, CompoundNBT bodyData)
    {
        playerBodies.put(playerData.getPlayerUUID(), bodyData);
        this.markDirty();

    }

    public void respawnSavedBody(PlayerEntity player)
    {
        CompoundNBT bodyData = playerBodies.get(player.getUniqueID());


        if(bodyData != null)
        {
            PlayerBodyEntity body = new PlayerBodyEntity(player.world,player.rotationYaw, player.rotationYawHead, player.rotationPitch);
            body.read(bodyData);
            player.world.addEntity(body);
            playerBodies.remove(player.getUniqueID());
            this.markDirty();

        }
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt)
    {
        ListNBT listnbt = nbt.getList("bodiesDataList", 10);
        for (int i = 0; i < listnbt.size(); i++)
        {
            CompoundNBT tagToRead = listnbt.getCompound(i);

            UUID playerUUID = tagToRead.getUniqueId("playerUUID");
            CompoundNBT bodyData = tagToRead.getCompound("bodyData");

            playerBodies.put(playerUUID, bodyData);
        }


    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound)
    {

        ListNBT listNBT = new ListNBT();
        playerBodies.forEach((uuid, bodyData) ->
        {
            CompoundNBT tagToSave = new CompoundNBT();
            tagToSave.putUniqueId("playerUUID", uuid);
            tagToSave.put("bodyData", bodyData);
            listNBT.add(tagToSave);
        });

        if(!listNBT.isEmpty())
            compound.put("bodiesDataList", listNBT);

        return compound;
    }
}
