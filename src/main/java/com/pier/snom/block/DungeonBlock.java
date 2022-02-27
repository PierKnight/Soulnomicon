package com.pier.snom.block;

import com.pier.snom.SoulnomiconMain;
import com.pier.snom.client.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class DungeonBlock extends Block
{
    public DungeonBlock(String name)
    {
        super(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 3600000.0F).noDrops());
        this.setRegistryName(SoulnomiconMain.ID,name);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager)
    {
        if(!(target instanceof BlockRayTraceResult))
            return true;

        BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) target;

        BlockPos pos = blockRayTraceResult.getPos();
        Direction side = blockRayTraceResult.getFace();
        Random rand = new Random();

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        AxisAlignedBB axisalignedbb = state.getShape(world, pos).getBoundingBox();
        double d0 = (double)i + rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double)0.2F) + (double)0.1F + axisalignedbb.minX;
        double d1 = (double)j + rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double)0.2F) + (double)0.1F + axisalignedbb.minY;
        double d2 = (double)k + rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double)0.2F) + (double)0.1F + axisalignedbb.minZ;
        if (side == Direction.DOWN) {
            d1 = (double)j + axisalignedbb.minY - (double)0.1F;
        }

        if (side == Direction.UP) {
            d1 = (double)j + axisalignedbb.maxY + (double)0.1F;
        }

        if (side == Direction.NORTH) {
            d2 = (double)k + axisalignedbb.minZ - (double)0.1F;
        }

        if (side == Direction.SOUTH) {
            d2 = (double)k + axisalignedbb.maxZ + (double)0.1F;
        }

        if (side == Direction.WEST) {
            d0 = (double)i + axisalignedbb.minX - (double)0.1F;
        }

        if (side == Direction.EAST) {
            d0 = (double)i + axisalignedbb.maxX + (double)0.1F;
        }


        manager.addParticle(ModParticles.SOUL_FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        return true;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity)
    {
        return false;
    }

}
