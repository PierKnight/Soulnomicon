package com.pier.snom.world.structure.sections;

import com.pier.snom.init.ModBlocks;
import com.pier.snom.init.ModDamageSource;
import com.pier.snom.tile.DungeonButtonTile;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NoteBlockSection extends RoomSection
{


    private UUID dungeonUUID;

    private int sequenceTime = 0;
    private int[] noteSequence;

    private int currentNote = 0;

    private int round = 0;

    public NoteBlockSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    public void init(UUID dungeonUUID, Random random)
    {
        this.dungeonUUID = dungeonUUID;
        this.generateSequence(6, random);
    }

    @Override
    public boolean updateCheck(World world, List<PlayerEntity> players)
    {
        boolean update = super.updateCheck(world, players);

        if(sequenceTime > 0 && triggered)
        {
            this.sequenceTime++;

            if(sequenceTime % 20 == 0)
            {
                int index = (sequenceTime / 20) - 1;
                int x = noteSequence[index];
                BlockPos pos = new BlockPos(getXWithOffset(x, 7), getYWithOffset(1), getZWithOffset(x, 7));
                world.addBlockEvent(pos, Blocks.NOTE_BLOCK, 0, 0);
            }
            if(sequenceTime > noteSequence.length * 20)
            {
                this.editButtons(world, false);
                this.sequenceTime = 0;
            }
            update = true;
        }

        return update;
    }

    @Override
    public String getBossPhrase()
    {
        return "boss.dungeon.noteblock";
    }

    @Override
    public void onPlayersEnterRoom(World world, List<PlayerEntity> players)
    {
        generateNoteblocks(world);
        placeSequenceButton(world);
    }

    private static final int[] positionIndexes = new int[]{2, 4, 6, 8, 10, 12};

    private static final BlockState NOTE_BLOCK = Blocks.NOTE_BLOCK.getDefaultState();

    public void generateSequence(int amount, Random random)
    {
        this.sequenceTime = 0;
        this.noteSequence = new int[amount];

        for (int i = 0; i < amount; i++)
            this.noteSequence[i] = positionIndexes[random.nextInt(positionIndexes.length)];
    }

    public void showSequence(World world)
    {
        if(sequenceTime == 0)
        {
            this.currentNote = 0;
            this.sequenceTime = 1;
            editButtons(world, true);
        }
    }

    public void generateNoteblocks(World world)
    {

        for (int i = 0; i < positionIndexes.length; i++)
        {
            int x = positionIndexes[i];
            BlockPos pos = new BlockPos(getXWithOffset(x, 7), getYWithOffset(1), getZWithOffset(x, 7));
            world.setBlockState(pos, NOTE_BLOCK.with(NoteBlock.NOTE, i * 2));
            world.playEvent(2001, pos, Block.getStateId(NOTE_BLOCK));


        }


    }

    public void placeSequenceButton(World world)
    {
        BlockPos pos = new BlockPos(getXWithOffset(7, 4), getYWithOffset(2), getZWithOffset(7, 4));
        world.setBlockState(pos, BUTTON.with(AbstractButtonBlock.FACE, AttachFace.FLOOR));
        world.playEvent(2001, pos, Block.getStateId(BUTTON));

        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof DungeonButtonTile)
        {
            ((DungeonButtonTile) tile).setDungeonUUID(dungeonUUID);
            ((DungeonButtonTile) tile).setNoteblockIndex(-1);
        }

        BlockPos brickPos = pos.down();
        world.setBlockState(brickPos, ModBlocks.CHISELED_DUNGEON_BRICKS.getDefaultState());
        world.playEvent(2001, brickPos, Block.getStateId(BUTTON));

    }


    public void editButtons(World world, boolean destroy)
    {
        for (int i = 0; i < positionIndexes.length; i++)
        {
            int x = positionIndexes[i];
            BlockPos pos = new BlockPos(getXWithOffset(x, 7), getYWithOffset(1), getZWithOffset(x, 7));
            BlockPos buttonPos = pos.offset(direction.getOpposite());
            if(!destroy)
            {
                world.setBlockState(buttonPos, BUTTON.with(HorizontalBlock.HORIZONTAL_FACING, direction.getOpposite()));
                world.playEvent(2001, buttonPos, Block.getStateId(BUTTON));

                TileEntity tile = world.getTileEntity(buttonPos);
                if(tile instanceof DungeonButtonTile)
                {
                    ((DungeonButtonTile) tile).setDungeonUUID(dungeonUUID);
                    ((DungeonButtonTile) tile).setNoteblockIndex(i);
                }
            }
            else if(!world.isAirBlock(buttonPos))
                world.destroyBlock(buttonPos, false);
        }
    }

    public void pressButton(World world, PlayerEntity player, int noteBlockIndex)
    {
        if(noteBlockIndex == -1)
        {
            this.showSequence(world);
        }
        else if(this.noteSequence[currentNote] == positionIndexes[noteBlockIndex])
        {
            this.currentNote++;

            if(currentNote >= this.noteSequence.length)
            {
                this.round++;
                this.generateSequence(6 + round, new Random());

                this.editButtons(world, true);

                if(round >= 2)
                    this.completeRoom(world);
                else
                    sendMessage(Collections.singletonList(player), "boss.dungeon.noteblock2");

            }
        }
        else
        {
            this.currentNote = 0;
            player.attackEntityFrom(ModDamageSource.SOUL_MASTER, Float.MAX_VALUE);
        }
    }

    @Override
    public void onPlayersExitDungeon(World world)
    {
        this.currentNote = 0;
        this.sequenceTime = 0;
        this.round = 0;
        this.editButtons(world, true);
    }

    @Override
    public boolean isRoomComplete(World world, List<PlayerEntity> players)
    {
        return completed;
    }

    @Override
    public void readNBT(CompoundNBT tag)
    {
        super.readNBT(tag);
        this.dungeonUUID = tag.getUniqueId("dungeonUUID");
        this.noteSequence = tag.getIntArray("noteSequence");
        this.sequenceTime = tag.getInt("sequenceTime");
        this.currentNote = tag.getInt("currentNote");
        this.round = tag.getInt("round");
    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        super.writeToNBT(tag);
        tag.putUniqueId("dungeonUUID", this.dungeonUUID);
        tag.putIntArray("noteSequence", this.noteSequence);
        tag.putInt("sequenceTime", this.sequenceTime);
        tag.putInt("currentNote", this.currentNote);
        tag.putInt("round", this.round);
    }

}
