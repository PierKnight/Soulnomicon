package com.pier.snom.item;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.client.render.soulnomicon.SoulnomiconRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

import javax.annotation.Nonnull;


public class SoulnomiconItem extends Item
{
    public SoulnomiconItem()
    {
        super(new Item.Properties().maxStackSize(1).setISTER(() -> SoulnomiconRenderer::new).group(ItemGroup.MISC));
        this.setRegistryName(SoulnomiconMain.ID, "soulnomicon");

    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn)
    {
        playerIn.setActiveHand(handIn);
        playerIn.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            HandSide playerHand = playerIn.getPrimaryHand();
            HandSide opposite = playerHand == HandSide.RIGHT ? HandSide.LEFT : HandSide.RIGHT;
            soulPlayer.getAbilitiesManager().bookAbilityHand = handIn == Hand.MAIN_HAND ? playerIn.getPrimaryHand() : opposite;
            soulPlayer.getAbilitiesManager().soulnomiconStack = playerIn.getHeldItem(handIn);
        });

        return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
    }


    public static boolean isDeathNote(ItemStack stack)
    {
        return stack.getDisplayName().getFormattedText().equalsIgnoreCase("Death Note");
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
    {

        if(entityLiving instanceof PlayerEntity)
        {
            if(Integer.MAX_VALUE - timeLeft < 3)
                System.out.println("clicked");


        }

    }


}
