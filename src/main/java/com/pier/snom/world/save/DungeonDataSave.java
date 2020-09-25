package com.pier.snom.world.save;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.world.structure.sections.RoomSection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class DungeonDataSave extends WorldSavedData
{
    private static final String NAME = SoulnomiconMain.ID + "_dungeon_safe";

    //dungeonUUID - dungeon data like box and room sections
    private final Map<UUID, DungeonData> dungeonDatas = Maps.newHashMap();

    //dungeonUUID - players who are challenging that dungeon
    private final Map<UUID, Set<UUID>> dungeonChallenging = Maps.newHashMap();

    private final Set<UUID> challengeDungeonToRemove = Sets.newHashSet();

    public DungeonDataSave(String name)
    {
        super(name);
    }

    public static DungeonDataSave getSave(World world)
    {
        if(world instanceof ServerWorld)
        {
            ServerWorld serverWorld = (ServerWorld) world;
            DungeonDataSave result = serverWorld.getSavedData().get(() -> new DungeonDataSave(NAME), NAME);
            if(result == null)
            {
                result = new DungeonDataSave(NAME);
                serverWorld.getSavedData().set(result);
            }
            return result;
        }
        return null;
    }

    public Map<UUID, DungeonData> getDungeonDatas()
    {
        return dungeonDatas;
    }

    public Map<UUID, Set<UUID>> getDungeonChallenging()
    {
        return dungeonChallenging;
    }

    public void addNewDungeon(UUID dungeonUUID, MutableBoundingBox dungeonBox, List<RoomSection> roomSections, BlockPos startPos)
    {
        this.dungeonDatas.put(dungeonUUID, new DungeonData(dungeonBox, roomSections, startPos));
        this.markDirty();

    }

    public void addNewChallengers(UUID dungeonUUID, PlayerEntity player)
    {
        DungeonData dungeonData = this.dungeonDatas.get(dungeonUUID);
        if(dungeonData != null)
        {
            this.dungeonChallenging.put(dungeonUUID, Collections.singleton(player.getUniqueID()));
            float angle = dungeonData.roomSections.get(0).direction.getHorizontalAngle();
            player.rotationYaw = angle;
            player.rotationYawHead = angle;
            player.setPositionAndUpdate(dungeonData.startingRoomPos.getX() + 0.5D, dungeonData.startingRoomPos.getY(), dungeonData.startingRoomPos.getZ() + 0.5D);
            this.markDirty();

        }

    }


    public void updateDungeonChallenges(World world)
    {

        this.dungeonChallenging.forEach((dungeonUUID, playersUUID) ->
        {
            DungeonData dungeonData = this.dungeonDatas.get(dungeonUUID);
            RoomSection roomToClear = getRoomToClear(dungeonData);
            if(roomToClear != null)
            {
                List<PlayerEntity> players = playersUUID.stream().map(world::getPlayerByUuid).filter(Objects::nonNull).collect(Collectors.toList());

                if(!players.isEmpty())
                {
                    MutableBoundingBox dungeonMutableBox = dungeonData.dungeonBoundingBox;

                    if(players.stream().allMatch(player -> !player.isSpectator() && !player.getBoundingBox().intersects(dungeonMutableBox.minX, dungeonMutableBox.minY, dungeonMutableBox.minZ, dungeonMutableBox.maxX, dungeonMutableBox.maxY, dungeonMutableBox.maxZ)))
                    {
                        challengeDungeonToRemove.add(dungeonUUID);
                    }
                    else if(roomToClear.updateCheck(world, players))
                        this.markDirty();
                }
            }
            else
            {
                challengeDungeonToRemove.add(dungeonUUID);
            }
        });
        if(!challengeDungeonToRemove.isEmpty())
        {
            for (UUID dungeonUUID : challengeDungeonToRemove)
            {
                this.dungeonChallenging.remove(dungeonUUID);
                this.dungeonDatas.get(dungeonUUID).roomSections.forEach(roomSection -> roomSection.reset(world));
            }
            this.challengeDungeonToRemove.clear();
            this.markDirty();
        }
    }

    @Nullable
    public RoomSection getRoomToClear(DungeonData dungeonData)
    {
        return dungeonData.roomSections.stream().filter(roomTrigger -> !roomTrigger.isCompleted()).max((o1, o2) ->
        {
            Direction direction = o1.direction;
            AxisAlignedBB box1 = o1.getRoomBox();
            AxisAlignedBB box2 = o2.getRoomBox();
            if(direction == Direction.NORTH)
                return box1.minZ > box2.minZ ? 1 : -1;
            else if(direction == Direction.SOUTH)
                return box1.minZ < box2.minZ ? 1 : -1;
            else if(direction == Direction.WEST)
                return box1.minX > box2.minX ? 1 : -1;
            else if(direction == Direction.EAST)
                return box1.minX < box2.minX ? 1 : -1;
            return 0;
        }).orElse(null);
    }

    public boolean isPlayerChallenging(PlayerEntity player)
    {
        return this.dungeonChallenging.values().stream().anyMatch(uuids -> uuids.contains(player.getUniqueID()));
    }
    public boolean isBlockPosInsideDungeon(BlockPos pos)
    {
        return this.dungeonChallenging.keySet().stream().anyMatch(dungeonUUID -> intersectsWithBox(pos,this.dungeonDatas.get(dungeonUUID).dungeonBoundingBox));
    }

    private boolean intersectsWithBox(BlockPos pos,MutableBoundingBox boundingBox)
    {
        return pos.getX() >= boundingBox.minX && pos.getY() >= boundingBox.minY && pos.getZ() >= boundingBox.minZ && pos.getX() <= boundingBox.maxX && pos.getY() <= boundingBox.maxY && pos.getZ() <= boundingBox.maxZ;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        ListNBT dungeonsList = nbt.getList("dungeonsList", 10);
        for (int i = 0; i < dungeonsList.size(); i++)
        {
            CompoundNBT tagToRead = dungeonsList.getCompound(i);
            UUID uuid = tagToRead.getUniqueId("dungeonUUID");
            DungeonData dungeonData = new DungeonData();
            dungeonData.readFromNBT(tagToRead);
            this.dungeonDatas.put(uuid, dungeonData);
        }

        ListNBT challengesList = nbt.getList("challengingPlayersList", 10);
        for (int i = 0; i < challengesList.size(); i++)
        {
            CompoundNBT tagToRead = challengesList.getCompound(i);
            UUID dungeonUUID = tagToRead.getUniqueId("dungeonUUID");

            Set<UUID> playersUUID = Sets.newHashSet();

            ListNBT playersChallengeUUID = tagToRead.getList("playersChallengeUUID", 10);
            for (int o = 0; o < playersChallengeUUID.size(); o++)
            {
                CompoundNBT t = playersChallengeUUID.getCompound(o);
                playersUUID.add(t.getUniqueId("playerUUID"));
            }

            this.dungeonChallenging.put(dungeonUUID, playersUUID);
        }


    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound)
    {

        ListNBT challengesNBT = new ListNBT();
        dungeonDatas.forEach((dungeonUUID, dungeonData) ->
        {
            CompoundNBT dungeonNBT = new CompoundNBT();
            dungeonData.writeToNBT(dungeonNBT);
            dungeonNBT.putUniqueId("dungeonUUID", dungeonUUID);
            challengesNBT.add(dungeonNBT);
        });

        if(!challengesNBT.isEmpty())
            compound.put("dungeonsList", challengesNBT);


        ListNBT challengingPlayersList = new ListNBT();
        this.dungeonChallenging.forEach((dungeonUUID, players) ->
        {
            CompoundNBT tagToSave = new CompoundNBT();
            tagToSave.putUniqueId("dungeonUUID", dungeonUUID);

            ListNBT playersTag = new ListNBT();
            players.forEach(playerUUID ->
            {
                CompoundNBT playerIdTag = new CompoundNBT();
                playerIdTag.putUniqueId("playerUUID", playerUUID);
                playersTag.add(playerIdTag);
            });

            if(!playersTag.isEmpty())
            tagToSave.put("playersChallengeUUID", playersTag);

            challengingPlayersList.add(tagToSave);
        });

        if(!challengingPlayersList.isEmpty())
            compound.put("challengingPlayersList", challengingPlayersList);

        return compound;
    }


    public static class DungeonData
    {
        public MutableBoundingBox dungeonBoundingBox = null;

        private List<RoomSection> roomSections = Lists.newArrayList();

        private BlockPos startingRoomPos = null;

        public DungeonData()
        {
        }

        public DungeonData(MutableBoundingBox dungeonBoundingBox, List<RoomSection> roomSections, BlockPos startingRoomPos)
        {
            this.dungeonBoundingBox = dungeonBoundingBox;
            this.roomSections = roomSections;
            this.startingRoomPos = startingRoomPos;
        }

        public void writeToNBT(CompoundNBT tag)
        {

            ListNBT roomsNBT = new ListNBT();
            roomSections.forEach(roomTrigger ->
            {
                CompoundNBT tagToSave = new CompoundNBT();
                roomTrigger.writeToNBT(tagToSave);
                roomsNBT.add(tagToSave);
            });

            if(!roomsNBT.isEmpty())
                tag.put("dungeonRoomsList", roomsNBT);


            tag.put("dungeonBox", this.dungeonBoundingBox.toNBTTagIntArray());

            tag.putInt("startX", this.startingRoomPos.getX());
            tag.putInt("startY", this.startingRoomPos.getY());
            tag.putInt("startZ", this.startingRoomPos.getZ());
        }

        public void readFromNBT(CompoundNBT tag)
        {
            ListNBT dungeonRoomsList = tag.getList("dungeonRoomsList", 10);
            for (int i = 0; i < dungeonRoomsList.size(); i++)
            {
                CompoundNBT tagToRead = dungeonRoomsList.getCompound(i);
                roomSections.add(RoomSection.readFromNBT(tagToRead));
            }

            this.dungeonBoundingBox = new MutableBoundingBox(tag.getIntArray("dungeonBox"));

            this.startingRoomPos = new BlockPos(tag.getInt("startX"), tag.getInt("startY"), tag.getInt("startZ"));

        }
    }
}
