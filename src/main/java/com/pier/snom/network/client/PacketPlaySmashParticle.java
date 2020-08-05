package com.pier.snom.network.client;

import com.pier.snom.network.PacketUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class PacketPlaySmashParticle
{
    private int entityID;

    public PacketPlaySmashParticle(int entityID)
    {
        this.entityID = entityID;
    }

    public static void encode(PacketPlaySmashParticle pkt, PacketBuffer buf)
    {
        buf.writeInt(pkt.entityID);
    }

    public static PacketPlaySmashParticle decode(PacketBuffer buf)
    {
        return new PacketPlaySmashParticle(buf.readInt());
    }

    public static class Handler
    {
        public static void handle(final PacketPlaySmashParticle pkt, Supplier<NetworkEvent.Context> ctx)
        {

            ctx.get().enqueueWork(() ->
            {

                PlayerEntity player = PacketUtils.getClientPlayer();
                Entity entity = player.world.getEntityByID(pkt.entityID);
                if(entity != null)
                    playSmashBlockParticle(player.world,entity);

            });
            ctx.get().setPacketHandled(true);
        }

        //TODO make decent block particle effect
        private static void playSmashBlockParticle(World world, Entity entity)
        {

            Vec3d offset = entity.getMotion();

            AxisAlignedBB box = entity.getBoundingBox();
            AxisAlignedBB offsetBox = box.expand(offset);

            BlockPos.Mutable blockPos = new BlockPos.Mutable();

            for (double x = offsetBox.minX; x <= offsetBox.maxX; x += 0.2D)
            {
                for (double y = offsetBox.minY; y <= offsetBox.maxY; y += 0.2D)
                {
                    for (double z = offsetBox.minZ; z <= offsetBox.maxZ; z += 0.2D)
                    {

                        blockPos.setPos(x,y,z);
                        BlockState state = world.getBlockState(blockPos);

                        if(state.getMaterial() != Material.AIR)
                        {
                            Random random = new Random();
                            BlockParticleData particleData = new BlockParticleData(ParticleTypes.BLOCK, state);
                            double particleX = box.minX + box.getXSize() * random.nextDouble();
                            double particleY = box.minY + box.getYSize() * random.nextDouble();
                            double particleZ = box.minZ + box.getZSize() * random.nextDouble();
                            Vec3d motionP = offset.inverse();
                            world.addParticle(particleData, particleX, particleY, particleZ, motionP.x, motionP.y, motionP.z);

                        }

                    }
                }
            }


        }

    }
}
