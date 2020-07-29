package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.ClairvoyanceAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClairvoyanceAbilityRenderer extends AbilityRenderer<ClairvoyanceAbility>
{
    public ClairvoyanceAbilityRenderer(ClairvoyanceAbility ability)
    {
        super(ability);
    }

    @Override
    public boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer)
    {
        return false;
    }

    @Override
    public void renderInWorld(MatrixStack matrixStack, Minecraft mc, World world, PlayerEntity player, ISoulPlayer iSoulPlayer, Vec3d projectedView, float partialTicks)
    {
        for (BlockPos highLightPos : ClairvoyanceAbility.highLightedPositions)
        {
            renderHighlightBox(highLightPos.getX(), highLightPos.getY(), highLightPos.getZ(), 1F, matrixStack);
        }
        Vec3d playerPos = player.getPositionVec();

        if(!ability.searchStack.isEmpty() && ability.remainingSearchTime > 0)
        {
            AxisAlignedBB searchBoxRange = new AxisAlignedBB(playerPos.x, playerPos.y, playerPos.z, playerPos.x, playerPos.y, playerPos.z).grow(30D);
            for (ItemEntity itemEntity : world.getEntitiesWithinAABB(ItemEntity.class, searchBoxRange))
                if(ability.isValidItem(itemEntity.getItem()))
                    renderHighlightBox(itemEntity.getPosX() - 0.5D, itemEntity.getPosY() - 0.1D, itemEntity.getPosZ() - 0.5D, 0.5F, matrixStack);
        }


        if(ability.clairvoyanceScan.isScanning())
        {
            float progress = ability.clairvoyanceScan.getAnimationF(partialTicks);

            float distance = mc.gameSettings.renderDistanceChunks * 32;
            float scale = progress * distance;

            float fade = 1F - ability.clairvoyanceScan.getAnimationF(partialTicks, 23, 30);
            float alpha = fade * 0.5F;

            double playerX = MathHelper.lerp(partialTicks, player.lastTickPosX, player.getPosX());
            double playerY = MathHelper.lerp(partialTicks, player.lastTickPosY, player.getPosY());
            double playerZ = MathHelper.lerp(partialTicks, player.lastTickPosZ, player.getPosZ());

            double scanX = playerX - projectedView.x;
            double scanY = playerY - projectedView.y;
            double scanZ = playerZ - projectedView.z;

            matrixStack.push();
            RenderSystem.enableBlend();
            matrixStack.translate(scanX, scanY + player.getEyeHeight() - scale * 0.5F, scanZ);
            matrixStack.scale(scale, scale, scale);
            RenderHelper.disableStandardItemLighting();
            RenderSystem.disableTexture();
            for (int i = 0; i < 6; i++)
            renderFace(i, alpha, matrixStack);
            RenderHelper.enableStandardItemLighting();
            RenderSystem.disableBlend();
            matrixStack.pop();
        }

    }

    private static void renderHighlightBox(double x, double y, double z, float scale, MatrixStack matrix)
    {
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        double highlightX = x - renderInfo.getProjectedView().x;
        double highlightY = y - renderInfo.getProjectedView().y;
        double highlightZ = z - renderInfo.getProjectedView().z;
        double offset = (1D - scale) / 2D;
        final AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, scale, scale, scale);
        matrix.push();
        matrix.translate(highlightX + offset, highlightY + offset, highlightZ + offset);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        WorldRenderer.drawBoundingBox(matrix, bufferSource.getBuffer(RenderType.LINES), box, 1F, 1F, 1F, 1F);
        bufferSource.finish();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderHelper.enableStandardItemLighting();
        matrix.pop();

    }

    private static void renderFace(int index, float alpha, MatrixStack matrixStack)
    {
        matrixStack.push();
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        IVertexBuilder bufferBuilder = renderTypeBuffer.getBuffer(RenderType.getLightning());

        if(index < 4)
        {
            matrixStack.rotate(Vector3f.YP.rotationDegrees(index * -90F));
            matrixStack.translate(0.5F, 0F, 0.5F);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
        }
        else
        {
            matrixStack.translate(0F,0.5F,0F);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(index == 4 ? 90F : -90F));
            matrixStack.translate(-0.5F,-0.5F,-0.5F);

        }

        bufferBuilder.pos(matrix4f, 0, 0, 0).color(1F, 1F, 1F, alpha).endVertex();
        bufferBuilder.pos(matrix4f, 1, 0, 0).color(1F, 1F, 1F, alpha).endVertex();
        bufferBuilder.pos(matrix4f, 1, 1, 0).color(1F, 1F, 1F, alpha).endVertex();
        bufferBuilder.pos(matrix4f, 0, 1, 0).color(1F, 1F, 1F, alpha).endVertex();

        renderTypeBuffer.finish();
        matrixStack.pop();

    }
}
