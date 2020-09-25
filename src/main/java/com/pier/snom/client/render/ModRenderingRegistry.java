package com.pier.snom.client.render;

import com.pier.snom.client.render.entity.RenderPlayerBody;
import com.pier.snom.client.render.entity.RenderSoulMaster;
import com.pier.snom.entity.EntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class ModRenderingRegistry
{
    public static void registerRender()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.PLAYER_BODY_ENTITY,new RenderPlayerBody.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SOUL_MASTER_ENTITY,new RenderSoulMaster.Factory());
    }
}
