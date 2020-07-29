package com.pier.snom.init;

import com.pier.snom.item.SoulnomiconItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems
{

    public static Item SOULNOMICON = new SoulnomiconItem();

    static void registerItems(IForgeRegistry<Item> registryEvent)
    {
        registryEvent.register(SOULNOMICON);
    }

}
