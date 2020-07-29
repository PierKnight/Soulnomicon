package com.pier.snom.client.render.soulnomicon;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.animation.FlipBookAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulnomiconModel extends Model
{
    private final ModelRenderer cover_1 = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
    private final ModelRenderer cover_2 = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
    private final ModelRenderer pages_1;
    private final ModelRenderer pages_2;
    private final ModelRenderer page_1;
    // private final ModelRenderer page_2;
    private final ModelRenderer cover_side = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

    private final ModelRenderer cover_custom = (new ModelRenderer(this)).setTextureSize(12, 10).setTextureOffset(0, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);

    private final boolean isDeathNote;

    public SoulnomiconModel(boolean isDeathNote)
    {
        super(RenderType::getEntitySolid);
        this.pages_1 = new ModelRenderer(this).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
        this.pages_2 = new ModelRenderer(this).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
        this.page_1 = new ModelRenderer(this).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
        this.cover_1.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.cover_2.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.cover_custom.setRotationPoint(0.0F, 0.0F, 1.0F);

        this.cover_side.rotateAngleY = ((float) Math.PI / 2F);
        this.cover_side.rotationPointX -= 0.001F;
        this.isDeathNote = isDeathNote;

    }

    private static final ResourceLocation DEATH_NOTE_COVER = new ResourceLocation(SoulnomiconMain.ID, "textures/entity/death_note_cover.png");


    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        this.cover_1.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
        this.cover_side.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
        this.pages_1.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
        this.pages_2.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
        this.page_1.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);


        if(isDeathNote)
        {
            matrixStackIn.push();
            RenderSystem.scalef(-1F, -1F, 1F);
            Minecraft.getInstance().textureManager.bindTexture(DEATH_NOTE_COVER);
            cover_custom.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
            matrixStackIn.pop();
        }
        else
            this.cover_2.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);

    }


    public void setRotationAngles(float openF, boolean isOpening, FlipBookAnimation flipBook)
    {
        this.cover_1.rotateAngleY = (float) Math.PI + openF;
        this.cover_2.rotateAngleY = -openF;
        this.cover_custom.rotateAngleY = openF - (float) Math.PI;
        this.pages_1.rotateAngleY = openF;
        this.pages_2.rotateAngleY = -openF;

        if(!isOpening)
        {
            this.page_1.rotateAngleY = openF * 1;
            this.page_1.rotationPointX = openF;

        }
        else
        {
            this.page_1.rotateAngleY = -1F * openF;
            if(flipBook != null && flipBook.isFlipping)
            {
                this.page_1.rotateAngleY = (1F - (flipBook.getFlipF() * 2F)) * openF;
                this.page_1.rotationPointX = flipBook.getFlipF();
            }

        }

        this.pages_1.rotationPointX = openF;
        this.pages_2.rotationPointX = openF;
    }
}