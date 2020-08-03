package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.ISoulAbility;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import com.pier.snom.client.render.model.AnimatedPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;

@OnlyIn(Dist.CLIENT)
public abstract class AbilityRenderer<T extends ISoulAbility<?>>
{
    private static final RenderAnimatedPlayer RENDERER = new RenderAnimatedPlayer();

    protected final T ability;

    AbilityRenderer(T ability) {this.ability = ability;}


    /**
     * use this to edit player model rotation,position ,scale etc..
     * only if method {@link AnimatedPlayerModel#applyAbilityTransforms()} is true
     */
    public void applyTransforms(AbstractClientPlayerEntity player, AnimatedPlayerModel model, ISoulPlayer soulPlayer, float partialTicks) {}

    public void renderHand(Minecraft mc, ClientPlayerEntity player, ISoulPlayer soulPlayer, RenderHandEvent event)
    {

        if(event.getHand() == Hand.OFF_HAND)
            return;
        MatrixStack matrixStack = event.getMatrixStack();
        matrixStack.push();
        float swingProgress = event.getSwingProgress();
        float equippedProgress = event.getEquipProgress();
        boolean flag = event.getHand() == Hand.MAIN_HAND;
        HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();

        float f = handside == HandSide.RIGHT ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
        matrixStack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equippedProgress * -0.6F, f4 + -0.71999997F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f * 45.0F));
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


    /**
     * @param player         the player who is being rendered
     * @param watchingPlayer the player who is watching the player
     * @return if {@link AbilityRenderer#getPlayerRenderer()} should be rendered
     */
    public abstract boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer);

    public boolean shouldRenderCustomHand(PlayerEntity player,Hand hand, ISoulPlayer soulPlayer)
    {
        return shouldRenderPlayer(player, player, soulPlayer);
    }

    public void renderBookHUD(PlayerEntity player, ISoulPlayer iSoulPlayer) {}

    public void renderInWorld(MatrixStack matrixStack, Minecraft mc, World world, PlayerEntity player, ISoulPlayer iSoulPlayer, Vec3d projectedView, float partialTicks) {}

    public RenderAnimatedPlayer getPlayerRenderer()
    {
        return RENDERER;
    }

}
