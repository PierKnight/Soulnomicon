package com.pier.snom.client.render.model;

import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.ISoulAbility;
import com.pier.snom.client.ClientEvents;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;

import javax.annotation.Nonnull;

public class AnimatedPlayerModel extends PlayerModel<AbstractClientPlayerEntity>
{
    private final boolean isHand;
    protected final PlayerEntity player;

    public AnimatedPlayerModel(PlayerEntity player, float scale, boolean isHand)
    {
        super(scale, RenderAnimatedPlayer.hasSmallArms(player.getUniqueID()));
        this.isHand = isHand;
        this.player = player;
    }

    public AnimatedPlayerModel(PlayerEntity player, boolean isHand)
    {
        this(player, 0F, isHand);
    }

    public boolean applyAbilityTransforms()
    {
        return true;
    }


    @Override
    public void setRotationAngles(AbstractClientPlayerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if(!isHand && applyAbilityTransforms())
        {
            entityIn.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {
                ISoulAbility<?> ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
                if(ability != null)
                    ability.getRenderer().applyTransforms(entityIn, this, soulPlayer, ClientEvents.getPartialTicks());
            });
        }
    }

    @Nonnull
    public ModelRenderer getArmForSide(@Nonnull HandSide side)
    {
        return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }

    public ModelRenderer getArmWearForSide(HandSide side)
    {
        return side == HandSide.LEFT ? this.bipedLeftArmwear : this.bipedRightArmwear;
    }
}
