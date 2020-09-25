package com.pier.snom.client.render.entity;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;

public class RenderBeamPlayer extends RenderAnimatedPlayer
{
    public RenderBeamPlayer()
    {
        super();
        this.layerRenderers.removeIf(layer -> layer instanceof HeldItemLayer);
    }
}
