package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.ControlAbility;
import com.pier.snom.client.render.model.AnimatedPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class ControlAbilityRenderer extends AbilityRenderer<ControlAbility>
{

    public ControlAbilityRenderer(ControlAbility controlAbility) {super(controlAbility);}

    @Override
    public boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer)
    {
        return ability.isControllingEntity();
    }
/*
    @Override
    public void renderHand(Minecraft mc, ClientPlayerEntity player, ISoulPlayer soulPlayer, float partialTicks)
    {


        Entity entity = player.world.getEntityByID(ability.selectedEntityID);

        mc.gameRenderer.enableLightmap();
        boolean flag = player.getPrimaryHand() != HandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        rotateArm(player, partialTicks);
        setHandLightmap(player);
        GlStateManager.translatef(f * 0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.rotatef(f * 45.0F, 0.0F, 1.0F, 0.0F);

        Minecraft.getInstance().getTextureManager().bindTexture(player.getLocationSkin());
        if(entity != null && !entity.isPassenger(player))
        {

            Vec2f yawEPitch = ability.getRotationYawPitch(player, entity, partialTicks);
            double entityYaw = Math.toDegrees(yawEPitch.x);
            double playerYaw = player.getYaw(partialTicks);
            double entityPitch = Math.toDegrees(yawEPitch.y);
            double playerPitch = player.getPitch(partialTicks);
            GlStateManager.rotated((playerPitch - entityPitch), 0F, 0D, 1F);
            GlStateManager.rotated((-(entityYaw - playerYaw) + (player.getPrimaryHand() == HandSide.RIGHT ? 120D : 75D)), 0F, 1D, 0F);
        }
        GlStateManager.translatef(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotatef(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(f * 5.6F, 0.0F, 0.0F);

        GlStateManager.disableCull();
        getPlayerRenderer().renderArm(player, soulPlayer, ability, partialTicks);
        GlStateManager.enableCull();
        mc.gameRenderer.disableLightmap();

    }
     */


    @Override
    public void renderHand(Minecraft mc, ClientPlayerEntity player, ISoulPlayer soulPlayer, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn)
    {
        super.renderHand(mc, player, soulPlayer, partialTicks, matrixStack, bufferIn, combinedLightIn);
    }

    @Override
    public void applyTransforms(AbstractClientPlayerEntity player, AnimatedPlayerModel model, ISoulPlayer soulPlayer, float partialTicks)
    {

        Entity entity = player.world.getEntityByID(ability.selectedEntityID);

        if(entity != null && !entity.isPassenger(player))
        {
            Vec2f rotationRawPitch = ability.getRotationYawPitch(player, entity, partialTicks);
            float rotationYaw = rotationRawPitch.x;
            float rotationPitch = rotationRawPitch.y;
            float yawOffset = (MathHelper.lerp(partialTicks, player.prevRenderYawOffset, player.renderYawOffset) % 360F) * ((float) Math.PI / 180F);

            float armRotationX = rotationPitch - (float) Math.PI / 2F;
            float armRotationY = (float) (rotationYaw - yawOffset - Math.PI / 2D);


            if(player.getPrimaryHand() == HandSide.RIGHT)
            {
                model.bipedRightArm.rotateAngleX = armRotationX;
                model.bipedRightArm.rotateAngleY = armRotationY;
                model.bipedRightArmwear.copyModelAngles(model.bipedRightArm);
            }
            else
            {
                model.bipedLeftArm.rotateAngleX = armRotationX;
                model.bipedLeftArm.rotateAngleY = armRotationY;
                model.bipedLeftArmwear.copyModelAngles(model.bipedLeftArm);
            }
        }
    }


}
