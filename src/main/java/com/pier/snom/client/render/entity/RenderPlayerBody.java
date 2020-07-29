package com.pier.snom.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pier.snom.capability.PlayerData;
import com.pier.snom.client.render.entity.layers.LayerBodyCape;
import com.pier.snom.client.render.model.PlayerBodyModel;
import com.pier.snom.entity.PlayerBodyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RenderPlayerBody extends MobRenderer<PlayerBodyEntity, PlayerBodyModel>
{
    private RenderPlayerBody(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new PlayerBodyModel(), 0.5F);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ArrowLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
        this.addLayer(new LayerBodyCape(this));
        this.addLayer(new ElytraLayer<>(this));

    }


    @Override
    public void render(PlayerBodyEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        PlayerData playerData = entity.getPlayerData();
        if(playerData != null)
        {
            boolean smallArms = RenderAnimatedPlayer.hasSmallArms(playerData.getPlayerUUID());
            this.entityModel = new PlayerBodyModel(0.0F, smallArms);
        }
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(PlayerBodyEntity entity)
    {

        PlayerData playerData = entity.getPlayerData();
        if(playerData != null)
        {
            ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
            if(connection != null)
            {
                NetworkPlayerInfo networkPlayerInfo = connection.getPlayerInfo(playerData.getPlayerUUID());

                if(networkPlayerInfo != null)
                    return networkPlayerInfo.getLocationSkin();
            }
            return DefaultPlayerSkin.getDefaultSkin(playerData.getPlayerUUID());
        }
        return DefaultPlayerSkin.getDefaultSkin(UUID.fromString("f34123ef-1659-4afd-bccc-9e3147ff36d3"));
    }

    @Override
    protected void preRenderCallback(PlayerBodyEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    public static class Factory implements IRenderFactory<PlayerBodyEntity>
    {
        @Override
        public EntityRenderer<PlayerBodyEntity> createRenderFor(EntityRendererManager manager)
        {
            return new RenderPlayerBody(manager);
        }
    }
}
