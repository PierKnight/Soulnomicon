package com.pier.snom.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.entity.SoulMasterEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class SoulMasterModel extends EntityModel<SoulMasterEntity>
{
    public ModelRenderer head;
    public ModelRenderer rightArm;
    public ModelRenderer leftArm;
    public ModelRenderer column1;
    public ModelRenderer core;
    public ModelRenderer fingerR1;
    public ModelRenderer fingerR2;
    public ModelRenderer fingerR3;
    public ModelRenderer fingerR4;
    public ModelRenderer fingerR5;
    public ModelRenderer fingerL1;
    public ModelRenderer fingerL2;
    public ModelRenderer fingerL3;
    public ModelRenderer fingerL4;
    public ModelRenderer fingerL5;
    public ModelRenderer column2;
    public ModelRenderer column3;


    public LibsModel[] libsModels = new LibsModel[12];

    public SoulMasterModel()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.fingerR2 = new ModelRenderer(this, 50, 0);
        this.fingerR2.setRotationPoint(-0.2F, -2.0F, 0.0F);
        this.fingerR2.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.fingerL5 = new ModelRenderer(this, 56, 0);
        this.fingerL5.mirror = true;
        this.fingerL5.setRotationPoint(-1.5F, -1.0F, 0.0F);
        this.fingerL5.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL5, 0.0F, 0.0F, -0.6255260065779288F);
        this.rightArm = new ModelRenderer(this, 40, 0);
        this.rightArm.setRotationPoint(-10.0F, 5.0F, 0.0F);
        this.rightArm.addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.fingerR5 = new ModelRenderer(this, 56, 0);
        this.fingerR5.setRotationPoint(1.5F, -1.0F, 0.0F);
        this.fingerR5.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR5, 0.0F, 0.0F, 0.6255260065779288F);
        this.fingerR4 = new ModelRenderer(this, 50, 0);
        this.fingerR4.setRotationPoint(-2.0F, -1.0F, 0.0F);
        this.fingerR4.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR4, 0.0F, 0.0F, -0.6255260065779288F);
        this.column3 = new ModelRenderer(this, 10, 30);
        this.column3.setRotationPoint(0.0F, 14.5F, 0.1F);
        this.column3.addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(column3, -0.46914448828868976F, 0.0F, 0.0F);
        this.fingerL3 = new ModelRenderer(this, 50, 0);
        this.fingerL3.mirror = true;
        this.fingerL3.setRotationPoint(1.4F, -2.0F, 0.0F);
        this.fingerL3.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL3, 0.0F, 0.0F, 0.23457224414434488F);
        this.fingerL1 = new ModelRenderer(this, 50, 0);
        this.fingerL1.mirror = true;
        this.fingerL1.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.fingerL1.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.head.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.fingerR3 = new ModelRenderer(this, 50, 0);
        this.fingerR3.setRotationPoint(-1.4F, -2.0F, 0.0F);
        this.fingerR3.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR3, 0.0F, 0.0F, -0.23457224414434488F);
        this.leftArm = new ModelRenderer(this, 40, 0);
        this.leftArm.mirror = true;
        this.leftArm.setRotationPoint(10.0F, 5.0F, 0.0F);
        this.leftArm.addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.fingerR1 = new ModelRenderer(this, 50, 0);
        this.fingerR1.setRotationPoint(1.0F, -2.0F, 0.0F);
        this.fingerR1.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.column2 = new ModelRenderer(this, 10, 30);
        this.column2.setRotationPoint(0.0F, -3.0F, -1.7F);
        this.column2.addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(column2, 0.46914448828868976F, 0.0F, 0.0F);
        this.core = new ModelRenderer(this, 10, 20);
        this.core.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.core.addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.fingerL4 = new ModelRenderer(this, 50, 0);
        this.fingerL4.mirror = true;
        this.fingerL4.setRotationPoint(2.0F, -1.0F, 0.0F);
        this.fingerL4.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL4, 0.0F, 0.0F, 0.6255260065779288F);
        this.fingerL2 = new ModelRenderer(this, 50, 0);
        this.fingerL2.mirror = true;
        this.fingerL2.setRotationPoint(-1.2F, -2.0F, 0.0F);
        this.fingerL2.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.column1 = new ModelRenderer(this, 0, 20);
        this.column1.setRotationPoint(-1.0F, 3.0F, 2.0F);
        this.column1.addBox(0.0F, 0.0F, 0.0F, 2.0F, 15.0F, 1.0F, 0.0F, 0.0F, 0.0F);

        for (int i = 0; i < this.libsModels.length; i++)
            this.libsModels[i] = new LibsModel(this,i);

        this.rightArm.addChild(this.fingerR2);
        this.leftArm.addChild(this.fingerL5);
        this.rightArm.addChild(this.fingerR5);
        this.rightArm.addChild(this.fingerR4);
        this.column1.addChild(this.column3);
        this.leftArm.addChild(this.fingerL3);
        this.leftArm.addChild(this.fingerL1);
        this.rightArm.addChild(this.fingerR3);
        this.rightArm.addChild(this.fingerR1);
        this.column1.addChild(this.column2);
        this.leftArm.addChild(this.fingerL4);
        this.leftArm.addChild(this.fingerL2);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {

        ImmutableList.of(this.rightArm, this.head, this.leftArm, this.core, this.column1).forEach((modelRenderer) -> modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));

        for (LibsModel libsModel : this.libsModels)
            libsModel.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

    }

    @Override
    public void setRotationAngles(@Nonnull SoulMasterEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.head.rotateAngleX = headPitch * ((float) Math.PI / 180F);

        this.rightArm.rotationPointY = MathHelper.cos(ageInTicks * 0.2F) * 0.35F;
        this.leftArm.rotationPointY = MathHelper.cos(ageInTicks * 0.2F) * 0.35F;

        this.rightArm.rotateAngleX = (float)Math.PI / 2F;

        this.core.rotateAngleX = (entityIn.ticksExisted + ageInTicks) * 0.1F;
        this.core.rotateAngleY = (entityIn.ticksExisted + ageInTicks) * 0.1F;

        for (LibsModel libsModel : this.libsModels)
            libsModel.setRotationAngles(entityIn,limbSwing,limbSwingAmount,ageInTicks,netHeadYaw,headPitch);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    private static class LibsModel
    {

        public ModelRenderer LeftLib1;
        public ModelRenderer RightLib1;
        public ModelRenderer LeftLib2;
        public ModelRenderer RightLib2;

        private final int position;


        private LibsModel(SoulMasterModel model,int position)
        {
            this.position = position;

            float y = position * 1.5F;

            this.LeftLib1 = new ModelRenderer(model, 51, 8);
            this.LeftLib1.mirror = true;
            this.LeftLib1.setRotationPoint(1.5F, 4.0F + y, 1.5F);
            this.LeftLib1.addBox(-0.5F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
            model.setRotateAngle(LeftLib1, 0.0F, -0.8F, 0.0F);

            this.RightLib1 = new ModelRenderer(model, 51, 8);
            this.RightLib1.mirror = true;
            this.RightLib1.setRotationPoint(-1.5F, 4.0F + y, 1.5F);
            this.RightLib1.addBox(-0.5F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
            model.setRotateAngle(RightLib1, 0.0F, 0.8F, 0.0F);

            this.LeftLib2 = new ModelRenderer(model, 51, 8);
            this.LeftLib2.setRotationPoint(-3.0F, 0.0F, 0.5F);
            this.LeftLib2.addBox(3.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
            model.setRotateAngle(LeftLib2, 0.0F, 1.8325957145940461F, 0.0F);

            this.RightLib2 = new ModelRenderer(model, 51, 8);
            this.RightLib2.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.RightLib2.addBox(3.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
            model.setRotateAngle(RightLib2, 0.0F, 1.3634512595948698F, 0.0F);

            this.RightLib1.addChild(this.RightLib2);
            this.LeftLib1.addChild(this.LeftLib2);
        }

        public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
        {
            matrixStackIn.push();

            float f = 1F - (Math.abs(2 - position) / 9F);


            float scale = 0.6F + 0.2F * f;

          //  matrixStackIn.translate(0F,f,0F);
            matrixStackIn.scale(scale,scale,scale);

            this.LeftLib1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            this.RightLib1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);


            matrixStackIn.pop();

        }

        public void setRotationAngles(@Nonnull SoulMasterEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
        {
            this.LeftLib1.rotateAngleY = -0.8F;
            this.RightLib1.rotateAngleY = 0.8F;
        }
    }
}
