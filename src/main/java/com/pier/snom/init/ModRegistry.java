package com.pier.snom.init;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.client.particle.ModParticles;
import com.pier.snom.entity.EntityRegistry;
import com.pier.snom.world.structure.ModStructures;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry
{

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
    {
        ModItems.registerItems(event.getRegistry());
        EntityRegistry.registerEntityEggs(event.getRegistry());
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
    {
        ModBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void onEntitiesRegistry(final RegistryEvent.Register<EntityType<?>> event)
    {
        EntityRegistry.registerEntities(event.getRegistry());
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTiles.registerTiles(event.getRegistry());
    }

    @SubscribeEvent
    public static void onParticlesRegistry(final RegistryEvent.Register<ParticleType<?>> event)
    {
        ModParticles.registerParticleTypes(event.getRegistry());
    }

    @SubscribeEvent
    public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event)
    {
        ModSounds.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onFeatureRegistry(final RegistryEvent.Register<Structure<?>> event)
    {
        ModStructures.init(event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
    {
        ModParticles.registerParticles();
    }

    //private static final TileEntityType<StartPedestalTile> type = TileEntityType.Builder.create(StartPedestalTile::new, ModBlocks.DUNGEON_START_PEDESTAL).build(null);


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event)
    {
        ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(SoulnomiconMain.ID, "soulnomicon"), "inventory");
        IBakedModel original = event.getModelRegistry().get(location);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        EntityRegistry.registerMobAttributes(event);
    }

}