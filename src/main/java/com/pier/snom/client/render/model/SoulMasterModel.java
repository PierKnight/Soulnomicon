package com.pier.snom.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.client.ClientEvents;
import com.pier.snom.entity.soulmaster.SoulMasterEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class SoulMasterModel extends EntityModel<SoulMasterEntity>
{
    // private final ModelRenderer bipedRightArm;
    //private final ModelRenderer bipedRightLeg;
    //private final ModelRenderer bipedLeftLeg;
    //private final ModelRenderer bipedHeadwear;
    // private final ModelRenderer bipedLeftArm;
    //private final ModelRenderer bipedBody;
    //private final ModelRenderer bipedHead;


    public final ModelRenderer bipedHeadwear;
    public final ModelRenderer bipedHead;
    public final ModelRenderer leftPart;
    public final ModelRenderer bipedLeftArm;
    public final ModelRenderer rightPart;
    public final ModelRenderer bipedRightArm;
    public final ModelRenderer hip;


    public SoulMasterModel()
    {
        textureWidth = 64;
        textureHeight = 32;

        bipedHeadwear = new ModelRenderer(this);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear.setTextureOffset(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);

        bipedHead = new ModelRenderer(this);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        leftPart = new ModelRenderer(this);
        leftPart.setRotationPoint(0.0F, 4.0F, 0.0F);
        leftPart.setTextureOffset(16, 16).addBox(0.0F, -4.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, true);

        bipedLeftArm = new ModelRenderer(this);
        bipedLeftArm.setRotationPoint(4.0F, -3.0F, 0.0F);
        leftPart.addChild(bipedLeftArm);
        bipedLeftArm.setTextureOffset(40, 16).addBox(0.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

        rightPart = new ModelRenderer(this);
        rightPart.setRotationPoint(0.0F, 4.0F, 0.0F);
        rightPart.setTextureOffset(16, 16).addBox(-4.0F, -4.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);

        bipedRightArm = new ModelRenderer(this);
        bipedRightArm.setRotationPoint(-4.0F, -3.0F, 0.0F);
        rightPart.addChild(bipedRightArm);
        bipedRightArm.setTextureOffset(40, 16).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

        hip = new ModelRenderer(this);
        hip.setRotationPoint(0.0F, 24.0F, 0.0F);
        hip.setTextureOffset(16, 26).addBox(-4.0F, -14.0F, -2.0F, 8.0F, 2.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        matrixStack.scale(1.3F,1.3F,1.3F);
        bipedHeadwear.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        bipedHead.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftPart.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightPart.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        hip.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(SoulMasterEntity soulMaster, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float partialTicks = ClientEvents.getPartialTicks();

        this.bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        this.bipedHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.bipedHead.rotateAngleZ = 0F;

        this.bipedRightArm.rotateAngleX = 0.0F;
        this.bipedLeftArm.rotateAngleX = 0.0F;
        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedLeftArm.rotateAngleY = 0.0F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        ModelHelper.func_239101_a_(this.bipedRightArm, this.bipedLeftArm, ageInTicks);

        this.rightPart.rotateAngleY = 0F;
        this.leftPart.rotateAngleY = 0F;
        this.rightPart.rotateAngleZ = 0F;
        this.leftPart.rotateAngleZ = 0F;
        this.hip.rotateAngleY = 0F;

        if(soulMaster.hurtTime > 0)
        {
            float hurtTime =  MathHelper.lerp(partialTicks, soulMaster.prevHurtTime, soulMaster.hurtTime);
            float progress = ((float) soulMaster.maxHurtTime - hurtTime) / (soulMaster.maxHurtTime * 0.5F);
            float f = -progress * progress + 2 * progress;

            Random random = new Random(soulMaster.hurtSeed);
            this.bipedHead.rotateAngleY += f * 0.6F * (1D - random.nextDouble() * 2D);
            this.bipedHead.rotateAngleX += f * 0.6F * (1D - random.nextDouble() * 2D);
            this.bipedHead.rotateAngleZ += f * 0.6F *  (1D - random.nextDouble() * 2D);

            this.rightPart.rotateAngleY += f * 0.4F *  (1D - random.nextDouble() * 2D);
            this.leftPart.rotateAngleY += f * 0.4F *  (1D - random.nextDouble() * 2D);

            this.rightPart.rotateAngleZ += f * 0.1F *  (1D - random.nextDouble() * 2D);
            this.leftPart.rotateAngleZ += f * 0.1F *  (1D - random.nextDouble() * 2D);

            this.hip.rotateAngleY += f * 0.6F *  (1D - random.nextDouble() * 2D);


        }

        if(soulMaster.ARROW_SUMMONING.getCurrentTick() > 0)
        {
            float t = soulMaster.ARROW_SUMMONING.getProgress(partialTicks);

            this.bipedRightArm.rotateAngleX = -t * 1.63F;
            this.bipedLeftArm.rotateAngleX = -t * 1.63F;

            this.bipedRightArm.rotateAngleY = -t * 0.2F;
            this.bipedLeftArm.rotateAngleY = t * 0.2F;

        }

        if(soulMaster.HAND_COMMAND_ARROW.getCurrentTick() > 0)
        {
            float t = soulMaster.HAND_COMMAND_ARROW.getProgress(partialTicks);
            this.bipedRightArm.rotateAngleX = -t * 2.234F;
            this.rightPart.rotateAngleY = -t * 0.7F;
        }

        if(soulMaster.PUPPETS_ANIMATION.getCurrentTick() > 0)
        {
            float t = soulMaster.PUPPETS_ANIMATION.getProgress(partialTicks);

            this.rightPart.rotateAngleY = t * 0.65F;
            this.leftPart.rotateAngleY = -t * 0.65F;
            this.bipedRightArm.rotateAngleX = -t * 0.64F;
            this.bipedLeftArm.rotateAngleX = -t * 0.64F;
            this.bipedHead.rotateAngleX = -t * 0.5F;
        }



        if(soulMaster.deathTime > 0)
        {
            float t = MathHelper.lerp(partialTicks, soulMaster.prevDeathTime, soulMaster.deathTime) / 39F;

            float frequency = t * 2.3F;
            bipedHead.rotateAngleX = -t * 0.7F;

            bipedRightArm.rotateAngleX = -t * 2.5F;
            bipedLeftArm.rotateAngleX = -t * 2.5F;

            bipedRightArm.rotateAngleY = MathHelper.sin(ageInTicks * (frequency + 0.3F)) * 0.14F;
            bipedLeftArm.rotateAngleY = MathHelper.sin(ageInTicks * (frequency + 0.1F)) * 0.1F;

            bipedHead.rotateAngleY = MathHelper.sin(ageInTicks * (frequency - 0.15F)) * 0.1F;
            bipedHead.rotateAngleZ = MathHelper.sin(ageInTicks * (frequency + 0.14F)) * 0.13F;

        }


    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }




}