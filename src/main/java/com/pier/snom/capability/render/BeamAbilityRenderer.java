package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.BeamAbility;
import com.pier.snom.client.ClientEvents;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import com.pier.snom.client.render.entity.RenderBeamPlayer;
import com.pier.snom.client.render.model.AnimatedPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;

public class BeamAbilityRenderer extends AbilityRenderer<BeamAbility>
{

    private static final RenderAnimatedPlayer RENDER_BEAM = new RenderBeamPlayer();

    public BeamAbilityRenderer(BeamAbility ability)
    {
        super(ability);
    }

    @Override
    public void renderHand(Minecraft mc, ClientPlayerEntity player, ISoulPlayer soulPlayer, RenderHandEvent event)
    {

        float animationProgress = getMovementProgress(event.getPartialTicks());


        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.push();
        float swingProgress = 0F;
        float equippedProgress = 0F;
        boolean flag = event.getHand() == Hand.MAIN_HAND;
        HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float f = handside == HandSide.RIGHT ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
        matrixStack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equippedProgress * -0.6F, f4 + -0.71999997F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * (45F + animationProgress * 70F)));
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f6 = MathHelper.sin(f1 * (float) Math.PI);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        matrixStack.translate(f * -1.0F, 3.6F, 3.5D);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(f * 120.0F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(200.0F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * -135.0F));
        matrixStack.translate(f * 5.6F, 0.0D, 0.0D);
        getPlayerRenderer().renderArm(handside, matrixStack, event.getBuffers(), event.getLight(), player);
        matrixStack.pop();


    }

    private float getMovementProgress(float partialTicks)
    {
        return this.ability.beamAnimation.getAnimationF(partialTicks, 0, 7);
    }

    private float getItemChargeProgress(float partialTicks)
    {
        return this.ability.beamAnimation.getAnimationF(partialTicks, 8, 30);
    }

    @Override
    public void applyTransforms(AbstractClientPlayerEntity player, AnimatedPlayerModel model, ISoulPlayer soulPlayer, float partialTicks, float netYaw, float headPitch)
    {
        float animationProgress = getMovementProgress(partialTicks);

        model.bipedRightArm.rotateAngleX = (headPitch * ((float) Math.PI / 180F) - ((float) Math.PI / 2F)) * animationProgress;
        model.bipedRightArm.rotateAngleY = (-animationProgress * 0.3F + model.bipedHead.rotateAngleY) * animationProgress;

        model.bipedLeftArm.rotateAngleX = model.bipedRightArm.rotateAngleX;
        model.bipedLeftArm.rotateAngleY = (animationProgress * 0.3F + model.bipedHead.rotateAngleY) * animationProgress;

        model.bipedRightArmwear.rotateAngleX = model.bipedRightArm.rotateAngleX;
        model.bipedRightArmwear.rotateAngleY = model.bipedRightArm.rotateAngleY;

        model.bipedLeftArmwear.rotateAngleX = model.bipedLeftArm.rotateAngleX;
        model.bipedLeftArmwear.rotateAngleY = model.bipedLeftArm.rotateAngleY;
    }

    @Override
    public void renderInWorld(MatrixStack matrixStack, Minecraft mc, World world, PlayerEntity player, ISoulPlayer iSoulPlayer, Vec3d projectedView, float partialTicks)
    {
        boolean isFirstPerson = player.equals(mc.player) && mc.gameSettings.thirdPersonView == 0;


        float movementProgress = getMovementProgress(partialTicks);
        if(movementProgress == 0.0F)
            return;

        Vec3d itemPosition = player.getEyePosition(partialTicks);
        Vec3d posVec = itemPosition.subtract(projectedView);
        if(!isFirstPerson)
            posVec = posVec.add(0D, -0.3D, 0D);

        int light = mc.getRenderManager().getPackedLight(player, partialTicks);

        renderBeam(mc, world, player, posVec, matrixStack, partialTicks, getItemChargeProgress(partialTicks), isFirstPerson);

        renderItem(Hand.MAIN_HAND, mc, player, movementProgress, matrixStack, posVec, light, partialTicks, isFirstPerson);
        renderItem(Hand.OFF_HAND, mc, player, movementProgress, matrixStack, posVec, light, partialTicks, isFirstPerson);

        //  Vec3d sphereVec = posVec.add(player.getLook(partialTicks).scale(ability.getHitDistance()));
        //renderHitCircle(matrixStack,mc,sphereVec, light,isFirstPerson);

    }

    public static final ResourceLocation TEXTURE_BEAM = new ResourceLocation(SoulnomiconMain.ID,"textures/entity/tool_beam.png");

    private void renderBeam(Minecraft mc, World world, PlayerEntity player, Vec3d posVec, MatrixStack matrixStack, float partialTicks, float animationProgress, boolean isFirstPerson)
    {
        float damageProgress = this.ability.damageTime / player.getCooldownPeriod();

        float red = 1F - damageProgress * 0.3F;
        float green = 1F - damageProgress;
        float blue = 1F - damageProgress;


        matrixStack.push();
        if(isFirstPerson)
            mc.gameRenderer.resetProjectionMatrix(mc.gameRenderer.getProjectionMatrix(mc.gameRenderer.getActiveRenderInfo(), partialTicks, false));

        matrixStack.translate(posVec.x, posVec.y, posVec.z);

        matrixStack.rotate(Vector3f.YP.rotationDegrees(-player.getYaw(partialTicks) + 90F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(player.getPitch(partialTicks)));

        if(isFirstPerson)
            matrixStack.translate(0F, -0.3F, 0F);

        matrixStack.translate(-0.5D, -0.5D, -0.5D);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(90F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(-1.3F));
        RenderHelper.disableStandardItemLighting();

        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        int distance = MathHelper.floor(ability.getHitDistance());
        int offset = isFirstPerson ? 1 : 0;
        BeaconTileEntityRenderer.renderBeamSegment(matrixStack, bufferSource, TEXTURE_BEAM, partialTicks, 1.0F, ClientEvents.test, -offset, distance + offset, new float[]{red,green,blue}, 0.2F * animationProgress, 0F);
        bufferSource.finish();
        matrixStack.pop();
    }


    private void renderItem(Hand hand, Minecraft mc, PlayerEntity player, float animationProgress, MatrixStack matrixStack, Vec3d posVec, int light, float partialTicks, boolean isFirstPerson)
    {

        matrixStack.push();
        if(isFirstPerson)
            mc.gameRenderer.resetProjectionMatrix(mc.gameRenderer.getProjectionMatrix(mc.gameRenderer.getActiveRenderInfo(), partialTicks, false));

        matrixStack.translate(posVec.x, posVec.y, posVec.z);

        matrixStack.rotate(Vector3f.YP.rotationDegrees(-player.getYaw(partialTicks) - 90F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(-player.getPitch(partialTicks)));

        if(isFirstPerson)
            matrixStack.translate(0F, -0.3F * animationProgress, 0F);

        matrixStack.translate(0.2F + animationProgress * 0.6F, 0F, 0F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));

        float angleHand = hand == Hand.MAIN_HAND ? 0F : 180F;
        float speed = ability.beamAnimation.getItemRotationSpeed(partialTicks);

        matrixStack.rotate(Vector3f.ZP.rotationDegrees((speed) * 6F + angleHand + 90F));
        matrixStack.translate(0F, 0.3F, 0F);

        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        mc.getItemRenderer().renderItem(player, player.getHeldItem(hand), ItemCameraTransforms.TransformType.GROUND, false, matrixStack, bufferSource, mc.world, light, OverlayTexture.NO_OVERLAY);
        bufferSource.finish();
        matrixStack.pop();
    }

    /*
    private static final ResourceLocation DRAGON_FIREBALL_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderType field_229044_e_ = RenderType.getEntityCutoutNoCull(DRAGON_FIREBALL_TEXTURE);


    private void renderHitCircle(MatrixStack matrixStack,Minecraft mc,Vec3d posVec,int light,boolean isFirstPerson)
    {
        matrixStack.push();

        matrixStack.translate(posVec.x, posVec.y, posVec.z);
        matrixStack.scale(1.2F, 1.2F, 1.2F);
        matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));

        matrixStack.translate(0F,0F,0.2F);

        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        IVertexBuilder ivertexbuilder = bufferSource.getBuffer(field_229044_e_);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, light, 0.0F, 0, 0, 1);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, light, 1.0F, 0, 1, 1);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, light, 1.0F, 1, 1, 0);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, light, 0.0F, 1, 0, 0);
        bufferSource.finish();
        matrixStack.pop();


    }

    private static void func_229045_a_(IVertexBuilder p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
        p_229045_0_.pos(p_229045_1_, p_229045_4_ - 0.5F, (float)p_229045_5_ - 0.7F, 0.0F).color(255, 255, 255, 255).tex((float)p_229045_6_, (float)p_229045_7_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229045_3_).normal(p_229045_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }
*/
    @Override
    public boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer)
    {
        return this.ability.isActive() || this.ability.beamAnimation.ticks > 0;
    }


    @Override
    public RenderAnimatedPlayer getPlayerRenderer()
    {
        return RENDER_BEAM;
    }
}
