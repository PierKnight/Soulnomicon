package com.pier.snom.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.client.render.model.SoulMasterModel;
import com.pier.snom.entity.SoulMasterEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderSoulMaster extends MobRenderer<SoulMasterEntity, SoulMasterModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(SoulnomiconMain.ID,"textures/entity/soul_master.png");

    public RenderSoulMaster(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new SoulMasterModel(), 1F);
    }

    @Override
    public ResourceLocation getEntityTexture(SoulMasterEntity entity)
    {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(SoulMasterEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
        float scale = 2F;
        matrixStackIn.scale(scale,scale,scale);
        super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
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
