package com.pier.snom.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.client.ClientEvents;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SoulPlayerModel extends AnimatedPlayerModel
{

    private final ModelRenderer Tail1R;
    private final ModelRenderer tail1L;
    private final ModelRenderer Tail2R;
    private final ModelRenderer Tail3R;
    private final ModelRenderer Tail2L;
    private final ModelRenderer Tail3L;


    public SoulPlayerModel(PlayerEntity player, boolean isHand)
    {
        super(player, isHand);
        this.Tail2R = new ModelRenderer(this, 1, 19);
        this.Tail2R.setRotationPoint(-1.1F, 4.0F, -1.5F);
        this.Tail2R.addBox(0.0F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
        this.Tail2L = new ModelRenderer(this, 17, 51);
        this.Tail2L.setRotationPoint(-1.9F, 4.0F, -1.5F);
        this.Tail2L.addBox(0.0F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
        this.Tail3R = new ModelRenderer(this, 4, 21);
        this.Tail3R.setRotationPoint(1.0F, 4.0F, 1.0F);
        this.Tail3R.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1, 0.0F);
        this.tail1L = new ModelRenderer(this, 16, 48);
        this.tail1L.setRotationPoint(1.9F, 12.0F, 0.1F);
        this.tail1L.addBox(-2.0F, 0.0F, -2.0F, 4, 4, 4, 0.0F);
        this.Tail3L = new ModelRenderer(this, 4, 21);
        this.Tail3L.setRotationPoint(0.0F, 4.0F, 1.0F);
        this.Tail3L.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1, 0.0F);
        this.Tail1R = new ModelRenderer(this, 0, 16);
        this.Tail1R.setRotationPoint(-1.9F, 12.0F, 0.1F);
        this.Tail1R.addBox(-2.0F, 0.0F, -2.0F, 4, 4, 4, 0.0F);
        this.Tail1R.addChild(this.Tail2R);
        this.tail1L.addChild(this.Tail2L);
        this.Tail2R.addChild(this.Tail3R);
        this.Tail2L.addChild(this.Tail3L);

        this.bipedRightLeg.showModel = false;
        this.bipedLeftLeg.showModel = false;
        this.bipedLeftLegwear.showModel = false;
        this.bipedRightLegwear.showModel = false;
    }


    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            float a = 1F - soulPlayer.getAbilitiesManager().getSeparation().deathSoulPlayerAnimation.getAnimationF();
            super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, a * 0.4F);
        });

    }

    @Override
    @Nonnull
    protected Iterable<ModelRenderer> getBodyParts()
    {
        return ImmutableList.of(this.bipedBody, this.bipedRightArm, this.bipedRightArmwear, this.bipedLeftArmwear, this.bipedLeftArm, this.bipedHeadwear, this.Tail1R, this.tail1L);
    }

    @Override
    public boolean applyAbilityTransforms()
    {
        return false;
    }

    @Override
    public void setRotationAngles(AbstractClientPlayerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if(this.isSneak)
        {
            this.Tail1R.rotationPointZ = 4.0F;
            this.tail1L.rotationPointZ = 4.0F;
            this.Tail1R.rotationPointY = 11.0F;
            this.tail1L.rotationPointY = 11.0F;
        }
        if(this.isSitting)
        {
            this.Tail1R.rotateAngleX = -1.4137167F;
            this.tail1L.rotateAngleX = -1.4137167F;
        }
        boolean flag = entityIn.getTicksElytraFlying() > 4;
        float f = 1.0F;
        if(flag)
        {
            f = (float) entityIn.getMotion().lengthSquared();
            f = f / 0.2F;
            f = f * f * f;
        }

        if(f < 1.0F)
        {
            f = 1.0F;
        }
        this.Tail1R.rotateAngleX = Math.abs(MathHelper.cos(limbSwing * 0.4662F)) * 0.3F * limbSwingAmount / f;
        this.tail1L.rotateAngleX = this.Tail1R.rotateAngleX;

        this.Tail2R.rotateAngleX = Math.abs(MathHelper.cos(limbSwing * 0.4662F)) * 0.3F * limbSwingAmount / f;
        this.Tail2L.rotateAngleX = this.Tail2R.rotateAngleX;

        this.Tail3R.rotateAngleX = Math.abs(MathHelper.cos(limbSwing * 0.4662F)) * 0.3F * limbSwingAmount / f;
        this.Tail3L.rotateAngleX = this.Tail3R.rotateAngleX;


        if(this.rightArmPose == ArmPose.EMPTY && this.leftArmPose == ArmPose.EMPTY)
        {
            this.bipedRightArm.rotateAngleX *= 0.5F;
            this.bipedRightArmwear.rotateAngleX = this.bipedRightArm.rotateAngleX;
            this.bipedLeftArm.rotateAngleX *= 0.5F;
            this.bipedLeftArmwear.rotateAngleX = this.bipedLeftArm.rotateAngleX;

        }

        entityIn.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            float armF = soulPlayer.getAbilitiesManager().getSeparation().deathSoulPlayerAnimation.getAnimationF(ClientEvents.getPartialTicks(), 0, 15);
            if(armF > 0.0F)
            {
                this.bipedRightArm.rotateAngleZ = armF * 2F;
                this.bipedLeftArm.rotateAngleZ = armF * -2F;
                this.bipedRightArmwear.rotateAngleZ = this.bipedRightArm.rotateAngleZ;
                this.bipedLeftArmwear.rotateAngleZ = this.bipedLeftArm.rotateAngleZ;
                this.bipedHead.rotateAngleX = -armF * 0.7F;
                this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
            }
        });
    }



}
