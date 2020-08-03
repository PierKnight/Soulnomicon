package com.pier.snom.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.EnumAbility;
import com.pier.snom.client.KeyBoardHandler;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.server.PacketFlySoulnomicon;
import com.pier.snom.network.server.PacketSetAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class AbilityWheelScreen extends Screen
{

    private double prevMouseX = 0;
    private double prevMouseY = 0;

    public static int selectedAbilityIndex = 0;

    private final PlayerEntity player;

    public AbilityWheelScreen(PlayerEntity player)
    {
        super(null);
        this.player = player;
        this.passEvents = false;
        onOpen();

    }


    @Override
    public void mouseMoved(double x, double y)
    {

        double motionX = x - prevMouseX;
        double motionY = y - prevMouseY;
        double mouseAngle = MathHelper.atan2(-motionX, motionY) + (Math.PI / 2D);
        if(x != 0 || y != 0)
        {
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {
                selectedAbilityIndex = getSelectedAbilityIndex(mouseAngle);
                if(selectedAbilityIndex != soulPlayer.getAbilitiesManager().selectedAbilityIndex)
                {
                    soulPlayer.getAbilitiesManager().setSelectedAbility(player, selectedAbilityIndex);
                    PacketManager.channel.sendToServer(new PacketSetAbility(selectedAbilityIndex));
                }
            });
        }
        prevMouseX = x;
        prevMouseY = y;

    }


    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_)
    {

        float f = 1F;
        float distance = f * 60;

        double anglePiece = (Math.PI * 2D) / EnumAbility.values().length;

        for (int i = 0; i < EnumAbility.values().length; i++)
        {
            String abilityName = EnumAbility.values()[i].getLocalizedName();

            int color = 0XFFFFFF;
            if(i == selectedAbilityIndex)
                color = 0X000000;

            int stringWidth = font.getStringWidth(abilityName);


            MatrixStack matrixstack = new MatrixStack();
            matrixstack.push();
            double angle = anglePiece * i - (Math.PI / 2);
            float stringX = (float) Math.cos(angle) * distance - stringWidth / 2F;
            float stringY = (float) Math.sin(angle) * distance - 3F;
            matrixstack.scale(f, f, f);
            this.font.drawString(abilityName, (width / 2F + stringX) / f, (height / 2F + stringY) / f, color);
            matrixstack.pop();
        }

    }

    public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_)
    {
        super.init(p_init_1_, p_init_2_, p_init_3_);
        //hides pointer
        if(minecraft != null)
        GLFW.glfwSetInputMode(minecraft.getMainWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    private void onOpen()
    {
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer -> selectedAbilityIndex = soulPlayer.getAbilitiesManager().selectedAbilityIndex);
    }

    @Override
    public void onClose()
    {
        super.onClose();
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
        {
            boolean shouldStartBookAnimation = selectedAbilityIndex > 0;
            soulPlayer.getAbilitiesManager().bookFlyingAroundA.isFlying = shouldStartBookAnimation;
            PacketManager.channel.sendToServer(new PacketFlySoulnomicon(shouldStartBookAnimation));
        });
    }

    @Override
    public void tick()
    {

        if(!KeyBoardHandler.isHoldingUseButton)
            onClose();

    }

    private static int getSelectedAbilityIndex(double mouseAngle)
    {
        double anglePiece = (Math.PI * 2D) / EnumAbility.values().length;
        for (int i = 0; i < EnumAbility.values().length; i++)
        {
            double abilityAngle = anglePiece * i - (Math.PI / 2);
            double minAngle = abilityAngle - anglePiece / 2D;
            double maxAngle = abilityAngle + anglePiece / 2D;
            if(mouseAngle >= Math.min(minAngle, maxAngle) && mouseAngle <= Math.max(minAngle, maxAngle))
                return i;
        }
        return 0;

    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }


}
