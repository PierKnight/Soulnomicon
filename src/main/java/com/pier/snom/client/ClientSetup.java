package com.pier.snom.client;

import com.pier.snom.client.render.ModRenderingRegistry;

public class ClientSetup
{
    public ClientSetup()
    {
        ModRenderingRegistry.registerRender();
    }
}
