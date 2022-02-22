package com.pier.snom.tile;

import com.pier.snom.init.ModDamageSource;
import com.pier.snom.init.ModTiles;
import com.pier.snom.world.save.DungeonDataSave;
import com.pier.snom.world.structure.sections.ItemQuestionSection;
import com.pier.snom.world.structure.sections.NoteBlockSection;
import com.pier.snom.world.structure.sections.RoomSection;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DungeonButtonTile extends TileEntity
{

    private UUID dungeonUUID;


    //ItemRoomSection
    private boolean isCorrect = false;


    //NoteBlockSection
    private int noteblockIndex = 0;

    public DungeonButtonTile()
    {
        super(ModTiles.DUNGEON_BUTTON_TYPE);
    }

    public void setCorrect(boolean correct)
    {
        isCorrect = correct;
    }

    public void setNoteblockIndex(int noteblockIndex)
    {
        this.noteblockIndex = noteblockIndex;
    }

    public void setDungeonUUID(UUID dungeonUUID)
    {
        this.dungeonUUID = dungeonUUID;
    }



    public void onPress(World world, PlayerEntity player)
    {
        DungeonDataSave dungeonDataSave = DungeonDataSave.getSave(world);
        if(dungeonDataSave != null && dungeonDataSave.getDungeonChallenging().containsKey(dungeonUUID))
        {

            RoomSection section = dungeonDataSave.getRoomToClear(dungeonDataSave.getDungeonDatas().get(dungeonUUID));
            if(section instanceof ItemQuestionSection)
            {
                if(isCorrect)
                {
                    ((ItemQuestionSection) section).nextEntry(world);
                    dungeonDataSave.markDirty();
                }
                else
                    player.attackEntityFrom(ModDamageSource.SOUL_MASTER,Float.MAX_VALUE);
            }
            else if(section instanceof NoteBlockSection)
            {
                ((NoteBlockSection) section).pressButton(world,player,noteblockIndex);
            }
        }
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound)
    {
        super.write(compound);
        compound.putBoolean("isCorrect", this.isCorrect);
        compound.putInt("noteblockIndex",this.noteblockIndex);

        if(dungeonUUID != null)
            compound.putUniqueId("dungeonUUID", dungeonUUID);

        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.isCorrect = nbt.getBoolean("isCorrect");
        this.noteblockIndex = nbt.getInt("noteblockIndex");

        if(nbt.hasUniqueId("dungeonUUID"))
            this.dungeonUUID = nbt.getUniqueId("dungeonUUID");
    }
}
