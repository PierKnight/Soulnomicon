package com.pier.snom.capability.abilities;

import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.render.AbilityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface ISoulAbility<T extends AbilityRenderer>
{

    EnumAbility getAbility();

    boolean canUse(PlayerEntity player, ISoulPlayer soulPlayer);

    /**
     * called every ticks via {@link net.minecraftforge.event.TickEvent.PlayerTickEvent}
     */
    void onUpdate(ISoulPlayer soulPlayer, PlayerEntity player);

    /**
     * called when the player casts the soul ability
     *
     * @param soulPlayer soul player capability
     * @return if true packets will be sent to sync datas between different clients and server
     */
    boolean active(ISoulPlayer soulPlayer, PlayerEntity player);

    /**
     * @return the amount of soul that this ability gives/consumes under certain conditions
     */
    float soulUsePreview(ISoulPlayer soulPlayer, PlayerEntity player);

    void writeToNBT(CompoundNBT nbt);

    void readFromNBT(CompoundNBT nbt);

    /**
     * checks if this ability is currently selected by the player
     */
    default boolean isSelectedAbility(ISoulPlayer iSoulPlayer)
    {
        ISoulAbility<?> iSoulAbility = iSoulPlayer.getAbilitiesManager().getSelectedAbility();
        if(iSoulAbility != null)
            return iSoulAbility.getAbility().equals(this.getAbility());
        return false;
    }

    /**
     * @return the renderer used to render custom animation for the ability
     */
    T getRenderer();


}
