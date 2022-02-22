package com.pier.snom.entity;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.entity.soulmaster.SoulMasterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.registries.IForgeRegistry;

public class EntityRegistry
{
    public static EntityType<SoulMasterEntity> SOUL_MASTER_ENTITY = createEntityType("soul_master", SoulMasterEntity::new, EntityClassification.MONSTER, 1.2F, 4F);



    public static void registerEntities(IForgeRegistry<EntityType<?>> registry)
    {
        registry.register(SOUL_MASTER_ENTITY);
    }

    public static void registerEntityEggs(IForgeRegistry<Item> registryEvent)
    {
        //registryEvent.register(getEntityEgg(PLAYER_BODY_ENTITY, 1, 1));
    }

    public static Item getEntityEgg(EntityType<?> entityType, int color1, int color2)
    {
        Item egg = new SpawnEggItem(entityType, color1, color2, new Item.Properties().group(ItemGroup.MISC));
        egg.setRegistryName(SoulnomiconMain.ID, entityType.getRegistryName().getNamespace() + "_egg");
        return egg;

    }

    private static <T extends Entity> EntityType<T> createEntityType(String name, EntityType.IFactory<T> factoryIn, EntityClassification classification, float width, float height)
    {
        EntityType<T> entityType = EntityType.Builder.create(factoryIn, classification).size(width, height).build(SoulnomiconMain.ID + ":" + name);
        entityType.setRegistryName(SoulnomiconMain.ID, name);
        return entityType;
    }

}
