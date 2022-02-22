package com.pier.snom.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.SoulnomiconMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents
{


    public static final ResourceLocation ICONS = new ResourceLocation(SoulnomiconMain.ID, "textures/gui/icons.png");



    @SubscribeEvent
    public static void renderItemFrame(RenderItemInFrameEvent event)
    {


        MatrixStack matrixStack = event.getMatrix();
        ItemStack stack = event.getItem();

        CompoundNBT tag = stack.getTag();

        if(tag != null && tag.getBoolean("isDungeonSilhouette"))
        {
            matrixStack.scale(0.5F, 0.5F, 0.5F);

            RenderType rendertype = RenderTypeLookup.func_239219_a_(stack, false);

            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
            ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, ibakedmodel, ItemCameraTransforms.TransformType.FIXED, false);
            IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(event.getBuffers(), rendertype, true, false);
            matrixStack.push();
            matrixStack.translate(-0.5D, -0.5D, -0.5D);
            renderSilhouetteModel(ibakedmodel, stack, event.getLight(), matrixStack, ivertexbuilder);
            matrixStack.pop();

            event.setCanceled(true);
        }

    }

    @SuppressWarnings("deprecation")
    private static void renderSilhouetteModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn)
    {
        Random random = new Random();
        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderBlackQuads(matrixStackIn, bufferIn, modelIn.getQuads(null, direction, random), stack, combinedLightIn, OverlayTexture.NO_OVERLAY);
        }
        random.setSeed(42L);
        renderBlackQuads(matrixStackIn, bufferIn, modelIn.getQuads(null, null, random), stack, combinedLightIn, OverlayTexture.NO_OVERLAY);
    }

    private static void renderBlackQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn)
    {
        MatrixStack.Entry entry = matrixStackIn.getLast();
        for (BakedQuad bakedquad : quadsIn)
            bufferIn.addVertexData(entry, bakedquad, 0F, 0F, 0F, combinedLightIn, combinedOverlayIn, true);
    }

    public static float getPartialTicks()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.isGamePaused() ? 1F : mc.getRenderPartialTicks();
    }

}
