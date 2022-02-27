package com.pier.snom.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;


public class ModRenderTypes extends RenderType
{
    public ModRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn)
    {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    private static final LineState NORMAL_LINES = new LineState(OptionalDouble.empty());

    public static final RenderType HIGHLIGHT_LINES = makeType("overlay_lines", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, RenderType.State.getBuilder().line(NORMAL_LINES).layer(RenderState.NO_LAYERING).transparency(TRANSLUCENT_TRANSPARENCY).texture(NO_TEXTURE).depthTest(DEPTH_ALWAYS).cull(CULL_DISABLED).lightmap(LIGHTMAP_DISABLED).writeMask(COLOR_WRITE).build(false));


    public static final RenderType TRANSPARENT_LINES = makeType("lines", DefaultVertexFormats.POSITION_COLOR, 1, 256,false,true,
            RenderType.State.getBuilder().line(new RenderState.LineState(OptionalDouble.empty())).layer(field_239235_M_).transparency(TRANSLUCENT_TRANSPARENCY).target(field_241712_U_).writeMask(COLOR_DEPTH_WRITE).build(false));


}

