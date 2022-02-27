package com.pier.snom.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.entity.DungeonBossEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;

public class SphereArenaLayer extends LayerRenderer<DungeonBossEntity, EntityModel<DungeonBossEntity>>
{
    public SphereArenaLayer(IEntityRenderer<DungeonBossEntity, EntityModel<DungeonBossEntity>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DungeonBossEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

    }

}
