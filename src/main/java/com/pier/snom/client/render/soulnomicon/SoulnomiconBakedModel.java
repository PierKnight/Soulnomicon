package com.pier.snom.client.render.soulnomicon;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class SoulnomiconBakedModel implements IBakedModel
{


    private final IBakedModel originalModel;

    public SoulnomiconBakedModel(IBakedModel originalModel) {this.originalModel = originalModel;}

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return Lists.newArrayList();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return originalModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return originalModel.isGui3d();
    }

    @Override
    public boolean func_230044_c_()
    {
        return originalModel.func_230044_c_();
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return true;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleTexture()
    {
        return originalModel.getParticleTexture();
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides()
    {
        return new ItemOverride();
    }


    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        SoulnomiconRenderer.transform = cameraTransformType;
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType,mat);
    }

    @Override
    @Nonnull
    public ItemCameraTransforms getItemCameraTransforms()
    {
        ItemCameraTransforms origin = originalModel.getItemCameraTransforms();
        ItemTransformVec3f ground = new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0.2F, 0.4F, 0.3F), new Vector3f(0.55F, 0.55F, 0.55F));
        ItemTransformVec3f gui = new ItemTransformVec3f(new Vector3f(0, -40F, 0), new Vector3f(-0.10F, 0.65F, 0), new Vector3f(1.3F, 1.3F, 1.3F));
        ItemTransformVec3f firstperson_right = new ItemTransformVec3f(new Vector3f(0, -110F, 30F), new Vector3f(-0.3F, 0.6F, 0F), new Vector3f(0.55F, 0.55F, 0.55F));
        ItemTransformVec3f firstperson_left = new ItemTransformVec3f(new Vector3f(0, 70F, -30F), new Vector3f(0.2F, 0.6F, 0.16F), new Vector3f(0.55F, 0.55F, 0.55F));

        ItemTransformVec3f thirdperson_right = new ItemTransformVec3f(new Vector3f(0F, -90F, 15F), new Vector3f(-0.5F, 0.8F, 0.2F), new Vector3f(1F, 1F,1F));

        ItemTransformVec3f thirdperson_left = new ItemTransformVec3f(new Vector3f(0, 90F, -15F), new Vector3f(0.5F, 0.8F, 0.2F), new Vector3f(1F,1F,1F));
        ItemTransformVec3f itemframe = new ItemTransformVec3f(new Vector3f(0, 180F, 0F), new Vector3f(-0.45F,0.75F,-0.75F), new Vector3f(1.5F,1.5F,1.5F));


        return new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, ItemTransformVec3f.DEFAULT, gui, ground, itemframe);
    }


    static class ItemOverride extends ItemOverrideList
    {

        @Nullable
        public IBakedModel getModelWithOverrides(@Nonnull IBakedModel model, @Nonnull ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
        {
            SoulnomiconRenderer.entity = entityIn;
            return super.getModelWithOverrides(model, stack, worldIn, entityIn);
        }
    }


}
