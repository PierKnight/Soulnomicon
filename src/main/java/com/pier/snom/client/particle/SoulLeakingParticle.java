package com.pier.snom.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.client.render.entity.RenderSoulMaster;
import com.pier.snom.client.render.model.SoulMasterModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class SoulLeakingParticle extends Particle
{
    protected int ticksOnGround = 0;
    protected int prevTicksOnGround = 0;
    protected final int maxTicksOnGround;

    public SoulLeakingParticle(ClientWorld world, int maxTicksOnGround, double x, double y, double z, double speed)
    {
        super(world, x, y, z);
        this.motionX = Math.cos(this.rand.nextDouble() * Math.PI * 2) * speed;
        this.motionY = Math.sin(this.rand.nextDouble() * Math.PI) * speed;
        this.motionZ = Math.sin(this.rand.nextDouble() * Math.PI * 2) * speed;
        this.canCollide = true;
        this.particleGravity = 0.5F;
        this.setSize(0.15F, 0.15F);
        this.maxAge = 20 * 10;
        this.maxTicksOnGround = maxTicksOnGround;
    }

    @Override
    public void tick()
    {
        super.tick();

        this.prevTicksOnGround = this.ticksOnGround;
        if(this.onGround)
            ++this.ticksOnGround;
        else
        {
            double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * this.width;
            double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * this.height;
            double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * this.width;
            this.world.addParticle(ModParticles.SOUL_FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        if(this.ticksOnGround > maxTicksOnGround)
            this.setExpired();


    }

    @Override
    public void renderParticle(@Nonnull IVertexBuilder buffer, @Nonnull ActiveRenderInfo renderInfo, float partialTicks)
    {
    }


    @Override
    @Nonnull
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.CUSTOM;
    }


    public static class SoulMasterPiece extends SoulLeakingParticle
    {
        private final SoulMasterModel soulMasterModel = new SoulMasterModel();
        private final RenderTypeBuffers renderTypeBuffers;
        private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
        private float rotation = 0F;
        private final int pieceType;
        private ModelRenderer modelRenderer;

        public SoulMasterPiece(ClientWorld world, double x, double y, double z, double speed, int pieceType)
        {
            super(world, 130, x, y, z, speed);
            this.renderTypeBuffers = Minecraft.getInstance().getRenderTypeBuffers();
            this.setSize(0.3F, 0.3F);
            this.pieceType = pieceType;
            this.modelRenderer = getModelToRender();
            this.modelRenderer.setRotationPoint(0, 0, 0);
        }


        @Override
        public void renderParticle(@Nonnull IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
        {
            float t = MathHelper.lerp(partialTicks, this.prevTicksOnGround, this.ticksOnGround) / this.maxTicksOnGround;
            float alpha = -t * t + 1;


            float yaw = -(float) (MathHelper.atan2(this.motionX, this.motionZ) + Math.PI) * 180F / (float) Math.PI;

            if(!this.onGround)
                this.rotation = this.age + partialTicks;

            double d0 = MathHelper.lerp(partialTicks, this.prevPosX, this.posX);
            double d1 = MathHelper.lerp(partialTicks, this.prevPosY, this.posY);
            double d2 = MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ);

            blockPos.setPos(d0, d1, d2);

            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();

            int light = WorldRenderer.getCombinedLight(this.world, blockPos);
            int overlay = OverlayTexture.getPackedUV(0, OverlayTexture.getV(false));
            Vector3d vector3d = renderInfo.getProjectedView();
            IRenderTypeBuffer.Impl typeBuffer = this.renderTypeBuffers.getBufferSource();
            IVertexBuilder vertexBuilder = typeBuffer.getBuffer(RenderType.getEntityTranslucent(RenderSoulMaster.TEXTURE));

            double offsetY = pieceType == 3 ? -1D : 0D;

            matrixStack.translate(d0 - vector3d.x, d1 - vector3d.y + offsetY, d2 - vector3d.z);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180F));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(yaw));
            matrixStack.rotate(Vector3f.XP.rotation(rotation));
            modelRenderer.render(matrixStack, vertexBuilder, light, overlay, 1F, 1F, 1F, alpha);
            matrixStack.pop();

            typeBuffer.finish();

        }

        private ModelRenderer getModelToRender()
        {
            switch(pieceType)
            {
                case 0:
                    return this.soulMasterModel.bipedHead;
                case 1:
                    return this.soulMasterModel.leftPart;
                case 2:
                    return this.soulMasterModel.rightPart;
                default:
                    return this.soulMasterModel.hip;
            }
        }

    }
}
