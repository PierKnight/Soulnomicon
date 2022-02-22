package com.pier.snom.item;

import com.pier.snom.SoulnomiconMain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;


public class SoulnomiconItem extends Item
{
    public SoulnomiconItem()
    {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
        this.setRegistryName(SoulnomiconMain.ID, "soulnomicon");
    }


}
