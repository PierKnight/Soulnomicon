package com.pier.snom.world.structure.sections;

import com.pier.snom.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FourWayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class RoomSection
{

    protected static final BlockState BUTTON = ModBlocks.DUNGEON_BUTTON.getDefaultState();


    protected boolean triggered = false;
    protected boolean completed = false;
    private final MutableBoundingBox roomBox;
    public Direction direction;
    private int doorYOffset = 0;

    public RoomSection(Direction direction, MutableBoundingBox roomBox)
    {
        this.roomBox = roomBox;
        this.direction = direction;
    }

    public boolean updateCheck(World world, List<PlayerEntity> players)
    {
        if(completed)
            return false;

        if(!triggered)
        {
            if(arePlayersInsideRoom(players))
            {
                sendMessage(players, getBossPhrase());
                onPlayersEnterRoom(world, players);
                closeDoors(world, players, false);
                triggered = true;
                return true;
            }
        }
        else
        {
            if(isRoomComplete(world, players))
            {
                completeRoom(world);
                return true;
            }
        }
        return false;
    }

    private BooleanProperty getDirectionProperty(Direction direction)
    {
        switch(direction)
        {
            case NORTH:
                return FourWayBlock.NORTH;
            case SOUTH:
                return FourWayBlock.SOUTH;
            case EAST:
                return FourWayBlock.EAST;
            default:
                return FourWayBlock.WEST;
        }

    }

    public void setDoorYOffset(int doorYOffset)
    {
        this.doorYOffset = doorYOffset;
    }

    protected void closeDoors(World world, @Nullable List<PlayerEntity> players, boolean exitOnly)
    {
        BlockPos pos = getEntrance();
        Direction d = direction.rotateY();

        for (int y = -1; y <= 1; y++)
        {
            for (int i = -1; i <= 1; i++)
            {


                BlockPos posEntrance = pos.add(d.getXOffset() * i, y, d.getZOffset() * i);
                BlockState state = getDoorBlock();

                if(!exitOnly)
                {
                    world.setBlockState(posEntrance, state, 2);
                    world.playEvent(2001, posEntrance, Block.getStateId(state));
                }

                BlockPos posExit = posEntrance.add(direction.getXOffset() * this.getRoomBox().getXSize(), 0, direction.getZOffset() * this.getRoomBox().getZSize());
                world.setBlockState(posExit, state, 2);
                world.playEvent(2001, posExit, Block.getStateId(state));

            }
        }
        if(players != null)
        {
            players.forEach(player ->
            {
                player.setMotion(direction.getXOffset() * 0.5D, 0, direction.getZOffset() * 0.5D);
                player.velocityChanged = true;
            });
        }

    }

    public void openDoors(World world)
    {


        BlockPos pos = getEntrance();
        Direction d = direction.rotateY();

        for (int y = -1; y <= 1; y++)
        {
            for (int i = -1; i <= 1; i++)
            {
                BlockPos posEntrance = pos.add(d.getXOffset() * i, y, d.getZOffset() * i);
                world.destroyBlock(posEntrance, false);

                BlockPos posExit = posEntrance.add(direction.getXOffset() * this.getRoomBox().getXSize(), 0, direction.getZOffset() * this.getRoomBox().getZSize());
                world.destroyBlock(posExit, false);


            }
        }
    }

    protected BlockPos getEntrance()
    {

        int width = direction.getAxis() == Direction.Axis.X ? (int) this.getRoomBox().getZSize() : (int) this.getRoomBox().getXSize();
        int center = width / 2;
        return new BlockPos(getXWithOffset(center, 0), getYWithOffset(2) + doorYOffset, getZWithOffset(center, 0));

    }


    public void sendMessage(List<PlayerEntity> players, String unlocalized_phrase, ITextComponent... textComponents)
    {
        TranslationTextComponent bossNameComponent = new TranslationTextComponent("dungeon.boss.name");
        String bossName = "[" + bossNameComponent.getString() + "§f]";

        players.forEach(player ->
        {
            TranslationTextComponent phraseComponent = new TranslationTextComponent(unlocalized_phrase, player.getDisplayName(),textComponents);
            player.sendStatusMessage(new StringTextComponent(bossName + " " + phraseComponent.getString()),false);
        });
    }

    public void spawnEntity(World world, Entity entity, int x, int y, int z)
    {
        int wx = getXWithOffset(x, z);
        int wy = getYWithOffset(y);
        int wz = getZWithOffset(x, z);
        entity.setPosition(wx + 0.5D, wy, wz + 0.5D);
        world.addEntity(entity);
    }

    public abstract String getBossPhrase();

    public abstract void onPlayersEnterRoom(World world, List<PlayerEntity> players);

    public abstract void onPlayersExitDungeon(World world);

    public abstract boolean isRoomComplete(World world, List<PlayerEntity> players);


    protected boolean arePlayersInsideRoom(List<PlayerEntity> players)
    {
        return players.stream().allMatch(this::isPlayerInsideRoom);
    }


    public boolean isPlayerInsideRoom(PlayerEntity player)
    {
        double minX = player.getBoundingBox().minX - roomBox.minX;
        double minY = player.getBoundingBox().minY - roomBox.minY;
        double minZ = player.getBoundingBox().minZ - roomBox.minZ;

        double maxX = player.getBoundingBox().maxX - roomBox.maxX;
        double maxY = player.getBoundingBox().maxY - roomBox.maxY;
        double maxZ = player.getBoundingBox().maxZ - roomBox.maxZ;

        return !player.isSpectator() && minX >= 0 && minY >= 0 && minZ >= 0 && maxX <= 0 && maxY <= 0 && maxZ <= 0;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public AxisAlignedBB getRoomBox()
    {
        return new AxisAlignedBB(roomBox.minX, roomBox.minY, roomBox.minZ, roomBox.maxX, roomBox.maxY, roomBox.maxZ);
    }

    protected int getXWithOffset(int x, int z)
    {
        if(direction == null)
        {
            return x;
        }
        else
        {
            switch(direction)
            {
                case NORTH:
                case SOUTH:
                    return this.roomBox.minX + x;
                case WEST:
                    return this.roomBox.maxX - z;
                case EAST:
                    return this.roomBox.minX + z;
                default:
                    return x;
            }
        }
    }

    protected int getYWithOffset(int y)
    {
        return direction == null ? y : y + roomBox.minY;
    }

    protected int getZWithOffset(int x, int z)
    {
        if(direction == null)
        {
            return z;
        }
        else
        {
            switch(direction)
            {
                case NORTH:
                    return roomBox.maxZ - z;
                case SOUTH:
                    return roomBox.minZ + z;
                case WEST:
                case EAST:
                    return roomBox.minZ + x;
                default:
                    return z;
            }
        }
    }

    public void reset(World world)
    {
        this.triggered = false;
        this.completed = false;
        openDoors(world);
        closeDoors(world, null, true);
        onPlayersExitDungeon(world);
    }

    public void completeRoom(World world)
    {
        this.completed = true;
        this.openDoors(world);
    }

    public BlockState getDoorBlock()
    {
        Direction d = direction.rotateY();
        return Blocks.IRON_BARS.getDefaultState().with(getDirectionProperty(d), Boolean.TRUE).with(getDirectionProperty(d.getOpposite()), Boolean.TRUE);
    }

    public void writeToNBT(CompoundNBT tag)
    {
        tag.putBoolean("triggered", this.triggered);
        tag.putBoolean("completed", this.completed);
        tag.putInt("direction", this.direction.getIndex());
        tag.put("box", this.roomBox.toNBTTagIntArray());
        tag.putString("name", this.getClass().getName());
        tag.putInt("doorOffset", this.doorYOffset);
    }

    public void readNBT(CompoundNBT tag)
    {
        this.triggered = tag.getBoolean("triggered");
        this.completed = tag.getBoolean("completed");
        this.doorYOffset = tag.getInt("doorOffset");
    }


    public static RoomSection readFromNBT(CompoundNBT tag)
    {

        Direction direction = Direction.byIndex(tag.getInt("direction"));
        MutableBoundingBox roomBox = new MutableBoundingBox(tag.getIntArray("box"));

        String className = tag.getString("name");

        RoomSection roomSection = null;

        try
        {
            roomSection = (RoomSection) Class.forName(className).getConstructor(Direction.class, MutableBoundingBox.class).newInstance(direction, roomBox);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        if(roomSection != null)
            roomSection.readNBT(tag);

        return roomSection;
    }

}