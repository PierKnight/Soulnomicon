package com.pier.snom.client.render.soulnomicon;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.AbilitiesManager;
import com.pier.snom.capability.abilities.EnumAbility;
import com.pier.snom.capability.animation.BookOpenAnimation;
import com.pier.snom.capability.animation.FlipBookAnimation;
import com.pier.snom.init.ModItems;
import com.pier.snom.item.SoulnomiconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


@OnlyIn(Dist.CLIENT)
public class SoulnomiconRenderer extends ItemStackTileEntityRenderer
{
    public static final ResourceLocation DEATH_NOTE_TEXTURE = new ResourceLocation(SoulnomiconMain.ID, "textures/entity/death_note.png");

    private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");

    static ItemCameraTransforms.TransformType transform = ItemCameraTransforms.TransformType.NONE;

    public static LivingEntity entity;

    @Override
    public void render(ItemStack itemStackIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if(itemStackIn.getItem() == ModItems.SOULNOMICON)
        {
            renderSoulnomicon(entity != null ? entity.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).orElseGet(null) : null, true, SoulnomiconItem.isDeathNote(itemStackIn), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }

    public static void renderSoulnomicon(@Nullable ISoulPlayer soulPlayer, boolean considerTransform, boolean isDeathNote, MatrixStack mat, IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn)
    {
        ResourceLocation texture = isDeathNote ? DEATH_NOTE_TEXTURE : BOOK_TEXTURE;

        IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(renderTypeBuffer, RenderType.getEntityTranslucentCull(texture), false, false);


        mat.push();
        mat.rotate(Vector3f.XP.rotationDegrees(180F));
        SoulnomiconModel bookModel = new SoulnomiconModel(isDeathNote);
        boolean flag = !considerTransform || transform != ItemCameraTransforms.TransformType.GUI;
        if(soulPlayer != null && flag)
        {
            BookOpenAnimation bookOpeningAnimation = soulPlayer.getAbilitiesManager().bookOpeningA;
            FlipBookAnimation flipBookAnimation = soulPlayer.getAbilitiesManager().flipBookA;
            float openingTicks = bookOpeningAnimation.getAnimationF();
            bookModel.setRotationAngles(openingTicks, bookOpeningAnimation.ticks > 0, flipBookAnimation);
            bookModel.render(mat, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
            renderText(soulPlayer.getAbilitiesManager(), flipBookAnimation, mat, renderTypeBuffer);

        }
        else
        {
            bookModel.setRotationAngles(0F, false, null);
            bookModel.render(mat, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
        }
        mat.pop();
    }


    private static void renderText(AbilitiesManager abilitiesManager, FlipBookAnimation flipBookAnimation, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer)
    {
        float f = 1F - flipBookAnimation.getTextF();
        f *= abilitiesManager.bookOpeningA.getAnimationF();
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        int index = flipBookAnimation.ticks >= 1 ? abilitiesManager.prevSelectedAbilityIndex : abilitiesManager.selectedAbilityIndex;
        if(index > 0)
        {
            String ability = EnumAbility.values()[index].getLocalizedName();
            float scale = 0.5F / fontRenderer.getStringWidth(ability);
            matrixStack.push();
            matrixStack.translate(0.25F * f, -0.17F, -0.24F);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-90F));
            matrixStack.scale(scale * f, scale * f, scale * f);
            fontRenderer.renderString(ability, 0, 0, 0X000000, false, matrixStack.getLast().getMatrix(), typeBuffer, false, 0, 15728880);
            matrixStack.pop();

        }
    }


}
