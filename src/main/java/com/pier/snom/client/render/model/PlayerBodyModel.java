package com.pier.snom.client.render.model;

import com.pier.snom.entity.PlayerBodyEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerBodyModel extends PlayerModel<PlayerBodyEntity>
{
    public PlayerBodyModel(float modelSize, boolean smallArmsIn)
    {
        super(modelSize, smallArmsIn);
    }

    public PlayerBodyModel()
    {
        super(0.0F, false);
    }



    @Override
    public void setRotationAngles(PlayerBodyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.isSneak = entityIn.isSneaking();

        for (Hand hand : Hand.values())
        {
            ItemStack stack = entityIn.getHeldItem(hand);
            if(stack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(stack))
            {
                if(entityIn.getPrimaryHand() == HandSide.RIGHT)
                {
                    if(hand == Hand.MAIN_HAND)
                        this.rightArmPose = ArmPose.CROSSBOW_HOLD;
                    else
                        this.leftArmPose = ArmPose.CROSSBOW_HOLD;
                }
                else if(this.leftArmPose == ArmPose.EMPTY)
                {
                    if(hand == Hand.MAIN_HAND)
                        this.leftArmPose = ArmPose.CROSSBOW_HOLD;
                    else
                        this.rightArmPose = ArmPose.CROSSBOW_HOLD;
                }

            }
        }

        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
