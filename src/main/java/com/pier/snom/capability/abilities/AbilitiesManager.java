package com.pier.snom.capability.abilities;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.animation.BookFlyingAroundAnimation;
import com.pier.snom.capability.animation.BookOpenAnimation;
import com.pier.snom.capability.animation.FlipBookAnimation;
import com.pier.snom.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nullable;

public class AbilitiesManager
{

    public HandSide bookAbilityHand = HandSide.RIGHT;

    public int prevSelectedAbilityIndex = 0;
    public int selectedAbilityIndex = 0;

    private final SeparationAbility separationAbility = new SeparationAbility();
    private final ControlAbility controlAbility = new ControlAbility();
    private final ClairvoyanceAbility clairvoyanceAbility = new ClairvoyanceAbility();


    public final BookOpenAnimation bookOpeningA = new BookOpenAnimation();
    public final BookFlyingAroundAnimation bookFlyingAroundA = new BookFlyingAroundAnimation();
    public final FlipBookAnimation flipBookA = new FlipBookAnimation();

    public ItemStack soulnomiconStack = ItemStack.EMPTY;


    public void update(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        //update soulnomicon animations
        bookOpeningA.update(player, soulPlayer);
        bookFlyingAroundA.update(player, soulPlayer);
        flipBookA.update(player, soulPlayer);

        //update abilities
        for (EnumAbility ability : EnumAbility.values())
            if(ability != EnumAbility.NONE)
                getAbility(ability).onUpdate(soulPlayer, player);

    }

    @Nullable
    public ISoulAbility getAbility(EnumAbility ability)
    {
        switch(ability)
        {
            case SEPARATION:
                return separationAbility;
            case CONTROL:
                return controlAbility;
            case CLAIRVOYANCE:
                return clairvoyanceAbility;
            default:
                return null;
        }
    }


    @Nullable
    public ISoulAbility getSelectedAbility()
    {
        return getAbility(EnumAbility.values()[selectedAbilityIndex]);
    }

    public void setSelectedAbility(PlayerEntity player, int selectedAbilityIndex)
    {
        if(!player.world.isRemote && !this.flipBookA.isFlipping && selectedAbilityIndex != 0)
        {
            player.world.playSound(null, player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ(), ModSounds.BOOK_FLIP, SoundCategory.PLAYERS, 0.8F, 1.1F);
        }
        this.prevSelectedAbilityIndex = this.selectedAbilityIndex;
        this.flipBookA.flip(selectedAbilityIndex - this.selectedAbilityIndex > 0);
        this.selectedAbilityIndex = selectedAbilityIndex;
    }

    public SeparationAbility getSeparation()
    {
        return separationAbility;
    }

    public ControlAbility getControl()
    {
        return controlAbility;
    }

    public ClairvoyanceAbility getClairvoyanceAbility()
    {
        return clairvoyanceAbility;
    }

    public void writeToNBT(CompoundNBT nbt)
    {
        nbt.putInt("selectedAbility", selectedAbilityIndex);
        for (EnumAbility ability : EnumAbility.values())
            if(ability != EnumAbility.NONE)
                getAbility(ability).writeToNBT(nbt);
        nbt.putString("bookHand", this.bookAbilityHand == HandSide.RIGHT ? "right" : "left");
        this.bookFlyingAroundA.writeToNBT(nbt, "bookAbility");

        CompoundNBT bookTag = new CompoundNBT();
        this.soulnomiconStack.write(bookTag);
        nbt.put("soulnomiconStack", bookTag);

    }

    public void readFromNBT(CompoundNBT nbt)
    {
        this.selectedAbilityIndex = nbt.getInt("selectedAbility");
        for (EnumAbility ability : EnumAbility.values())
            if(ability != EnumAbility.NONE)
                getAbility(ability).readFromNBT(nbt);

        this.bookAbilityHand = nbt.getString("bookHand").equals("right") ? HandSide.RIGHT : HandSide.LEFT;
        this.bookFlyingAroundA.readFromNBT(nbt, "bookAbility");

        this.soulnomiconStack = ItemStack.read(nbt.getCompound("soulnomiconStack"));

    }

}
