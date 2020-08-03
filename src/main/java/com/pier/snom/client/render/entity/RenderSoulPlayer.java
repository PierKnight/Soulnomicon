package com.pier.snom.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pier.snom.capability.render.SeparationAbilityRenderer;
import com.pier.snom.client.render.model.AnimatedPlayerModel;
import com.pier.snom.client.render.model.SoulPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@OnlyIn(Dist.CLIENT)
public class RenderSoulPlayer extends RenderAnimatedPlayer
{


    /**
     * used to disable {@link PlayerRenderer#getEntityModel()}
     */
    private static final PlayerModel<AbstractClientPlayerEntity> placeOlder = new PlayerModel<>(0.0F, false);

    public RenderSoulPlayer()
    {
        this.shadowOpaque = 0F;
    }

    @Override
    public AnimatedPlayerModel getPlayerModel(PlayerEntity entity, boolean isHand)
    {
        return new SoulPlayerModel(entity, isHand);
    }

    @Override
    public void render(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        SeparationAbilityRenderer.renderBlocks(Minecraft.getInstance(),entityIn.world,entityIn,entityIn.getPositionVec(),matrixStackIn);
        if(entityIn.isAlive())
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
     }

    @Nullable
    @Override
    protected RenderType func_230042_a_(AbstractClientPlayerEntity p_230042_1_, boolean p_230042_2_, boolean p_230042_3_)
    {
     //   System.out.println("test");
        ResourceLocation resourcelocation = this.getEntityTexture(p_230042_1_);
        return RenderType.getEntityTranslucent(resourcelocation);
    }

    @Override
    @Nonnull
    public PlayerModel<AbstractClientPlayerEntity> getEntityModel()
    {
        return placeOlder;
    }

    @Override
    public void renderArm(HandSide handSide, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn)
    {
        RenderSystem.color4f(1F, 1F, 1F, 0.5F);
        super.renderArm(handSide, matrixStackIn, bufferIn, combinedLightIn, playerIn);
    }

    @Override
    protected float getDeathMaxRotation(AbstractClientPlayerEntity entityLivingBaseIn)
    {
        return 0F;
    }
}
