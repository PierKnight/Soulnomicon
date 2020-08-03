package com.pier.snom.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.ISoulAbility;
import com.pier.snom.client.render.model.AnimatedPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RenderAnimatedPlayer extends PlayerRenderer
{

    public RenderAnimatedPlayer()
    {
        super(Minecraft.getInstance().getRenderManager());
    }

    public static boolean hasSmallArms(UUID uuid)
    {
        return (uuid.hashCode() & 1) == 1;
    }


    @Override
    public void render(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        AnimatedPlayerModel model = getPlayerModel(entityIn,false);
        this.setModelVisibilities(entityIn, model);
        this.entityModel = model;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public AnimatedPlayerModel getPlayerModel(PlayerEntity entity, boolean isHand)
    {
        return new AnimatedPlayerModel(entity, isHand);
    }

    public void renderArm(AbstractClientPlayerEntity clientPlayer, ISoulPlayer soulPlayer, ISoulAbility<?> ability, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn)
    {
        /*
        AnimatedPlayerModel model = getPlayerModel(clientPlayer, true);
        ModelRenderer armModel = model.getArmForSide(clientPlayer.getPrimaryHand());
        RendererModel armWearModel = model.getArmwearForSide(clientPlayer.getPrimaryHand());
        this.setModelVisibilities(clientPlayer, model);
        GlStateManager.enableBlend();
        model.isSneak = false;
        model.swingProgress = 0.0F;
        model.swimAnimation = 0.0F;
        model.setRotationAngles(clientPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        ability.getRenderer().applyHandTransforms(clientPlayer, soulPlayer, clientPlayer.getPrimaryHand(), partialTicks);
        armModel.render(0.0625F);
        armWearModel.copyModelAngles(armModel);
        armWearModel.render(0.0625F);
        GlStateManager.disableBlend();
        */


    }
    public void renderArm(HandSide handSide,MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn) {



        AnimatedPlayerModel model = getPlayerModel(playerIn, true);
        setModelVisibilities(playerIn,model);

        ModelRenderer arm = model.getArmForSide(handSide);
        ModelRenderer armWear = model.getArmWearForSide(handSide);

        model.swingProgress = 0.0F;
        model.isSneak = false;
        model.swimAnimation = 0.0F;
        model.setRotationAngles(playerIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.rotateAngleX = 0.0F;
        arm.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntitySolid(playerIn.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY);
        armWear.rotateAngleX = 0.0F;
        armWear.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(playerIn.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY);
    }


    private void setModelVisibilities(AbstractClientPlayerEntity clientPlayer, AnimatedPlayerModel model)
    {

        model.bipedHeadwear.showModel = clientPlayer.isWearing(PlayerModelPart.HAT);
        model.bipedBodyWear.showModel = clientPlayer.isWearing(PlayerModelPart.JACKET);
        model.bipedLeftLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
        model.bipedRightLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
        model.bipedLeftArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_SLEEVE);
        model.bipedRightArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_SLEEVE);
        model.isSneak = clientPlayer.isCrouching();

    }
}
