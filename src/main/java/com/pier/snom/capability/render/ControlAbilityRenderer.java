package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.ControlAbility;
import com.pier.snom.client.render.model.AnimatedPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.event.RenderHandEvent;

public class ControlAbilityRenderer extends AbilityRenderer<ControlAbility>
{

    public ControlAbilityRenderer(ControlAbility controlAbility) {super(controlAbility);}

    @Override
    public boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer)
    {
        return ability.isControllingEntity();
    }

    @Override
    public void renderHand(Minecraft mc, ClientPlayerEntity player, ISoulPlayer soulPlayer, RenderHandEvent event)
    {
        if(event.getHand() == Hand.OFF_HAND)
            return;

        Entity entity = player.world.getEntityByID(ability.selectedEntityID);

        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.push();

        float swingProgress = event.getSwingProgress();
        float equippedProgress = event.getEquipProgress();
        boolean flag = event.getHand() == Hand.MAIN_HAND;
        HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();

        float f = handside == HandSide.RIGHT ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
        matrixStack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equippedProgress * -0.6F, f4 + -0.71999997F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * 45.0F));
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
        mc.getTextureManager().bindTexture(player.getLocationSkin());


        if(entity != null && !entity.isPassenger(player))
        {

            Vec2f yawEPitch = ability.getRotationYawPitch(player, entity, event.getPartialTicks());
            float entityYaw = (float)Math.toDegrees(yawEPitch.x);
            float playerYaw = player.getYaw(event.getPartialTicks());
            float entityPitch = (float)Math.toDegrees(yawEPitch.y);
            float playerPitch = player.getPitch(event.getPartialTicks());
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(playerPitch - entityPitch));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-(entityYaw - playerYaw) + (player.getPrimaryHand() == HandSide.RIGHT ? 120F : 75F)));
        }


        matrixStack.translate(f * -1.0F, 3.6F, 3.5D);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(f * 120.0F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(200.0F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * -135.0F));
        matrixStack.translate(f * 5.6F, 0.0D, 0.0D);
        getPlayerRenderer().renderArm(handside,matrixStack,event.getBuffers(),event.getLight(),player);
        matrixStack.pop();


    }

    @Override
    public boolean shouldRenderCustomHand(PlayerEntity player, Hand hand, ISoulPlayer soulPlayer)
    {
        return super.shouldRenderCustomHand(player, hand, soulPlayer) && hand == Hand.MAIN_HAND;
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
