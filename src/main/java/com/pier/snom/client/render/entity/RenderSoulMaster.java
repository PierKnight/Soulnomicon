package com.pier.snom.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.client.render.model.SoulMasterModel;
import com.pier.snom.entity.soulmaster.SoulMasterEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nonnull;
import java.util.Random;

public class RenderSoulMaster extends MobRenderer<SoulMasterEntity, SoulMasterModel>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(SoulnomiconMain.ID, "textures/entity/soul_master.png");
    private final Random rnd = new Random();

    public RenderSoulMaster(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new SoulMasterModel(), 0.5F);
    }

    @Override
    public void render(@Nonnull SoulMasterEntity entityIn, float entityYaw, float partialTicks,@Nonnull MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        float radius = MathHelper.lerp(partialTicks,entityIn.prevRenderRadius,entityIn.renderRadius);
        if(radius == 0.0F)
            return;

        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();

        matrixStackIn.push();
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getLines());

        int stacks = 30;
        int slices = 30;

        double offset = (partialTicks + entityIn.ticksExisted) * 0.1D;

        for (int t = 0; t < stacks; t++)
        {
            double theta1 = (((double) (t) / stacks) * Math.PI + offset * 0.03D) % Math.PI;;

            for (int p = 0; p < slices; p++)
            {
                double phi1 = (double) (p) / (double) slices * 2 * Math.PI + offset;
                double phi2 = (double) (p + 1) / (double) slices * 2 * Math.PI + offset;

                Vector3f vertex1 = getSphereCoordinates(phi1, theta1);
                Vector3f vertex4 = getSphereCoordinates(phi2, theta1);
                this.addVertex(buffer, matrix4f, vertex1, radius);
                this.addVertex(buffer, matrix4f, vertex4, radius);


            }
        }

        matrixStackIn.pop();
    }

    private void addVertex(IVertexBuilder buffer, Matrix4f matrix4f, Vector3f point, float R)
    {
        buffer.pos(matrix4f, point.getX() * R, point.getY() * R, point.getZ() * R).color(1F, 1F, 1F, 0.1F).endVertex();
    }

    private Vector3f getSphereCoordinates(double theta, double phi)
    {
        float sinPhi = MathHelper.sin((float) phi);
        return new Vector3f(MathHelper.cos((float) theta) * sinPhi, MathHelper.cos((float) phi), MathHelper.sin((float) theta) * sinPhi);
    }

    @Override
    protected void preRenderCallback(@Nonnull SoulMasterEntity soulMaster, @Nonnull MatrixStack matrixStackIn, float partialTickTime)
    {
        float angle = (partialTickTime + soulMaster.ticksExisted) * 0.046F;
        float floatingY = MathHelper.sin(angle) * 0.1F;
        matrixStackIn.translate(0, floatingY, 0F);

        if(soulMaster.PUPPETS_ANIMATION.getCurrentTick() > 0)
        {
            float t = soulMaster.PUPPETS_ANIMATION.getProgress(partialTickTime);

            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-43F * t));

            matrixStackIn.translate(this.rnd.nextGaussian() * 0.01D, this.rnd.nextGaussian() * 0.01D, this.rnd.nextGaussian() * 0.01D);

            matrixStackIn.translate(0, 0, -t);
        }

        if(soulMaster.isShakingForLosingControl > 0)
        {
            float shakeAmount = soulMaster.isShakingForLosingControl / 30F * 0.04F;
            matrixStackIn.translate(this.rnd.nextGaussian() * shakeAmount, this.rnd.nextGaussian() * shakeAmount, this.rnd.nextGaussian() * shakeAmount);
        }

    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(@Nonnull SoulMasterEntity entity)
    {
        return TEXTURE;
    }

    @Override
    protected float getDeathMaxRotation(@Nonnull SoulMasterEntity entityLivingBaseIn)
    {
        return 0F;
    }


    public static class Factory implements IRenderFactory<SoulMasterEntity>
    {
        @Override
        public EntityRenderer<SoulMasterEntity> createRenderFor(EntityRendererManager manager)
        {
            return new RenderSoulMaster(manager);
        }
    }
}
