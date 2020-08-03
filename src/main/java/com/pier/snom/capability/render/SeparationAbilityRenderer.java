package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.SeparationAbility;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import com.pier.snom.client.render.entity.RenderSoulPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class SeparationAbilityRenderer extends AbilityRenderer<SeparationAbility>
{

    private static final RenderSoulPlayer RENDERER = new RenderSoulPlayer();

    public SeparationAbilityRenderer(SeparationAbility ability)
    {
        super(ability);
    }


    private static Method renderQuadsFlat;

    public static void initReflection()
    {
        renderQuadsFlat = ObfuscationReflectionHelper.findMethod(BlockModelRenderer.class, "func_228798_a_", ILightReader.class, BlockState.class, BlockPos.class, int.class, int.class, boolean.class, MatrixStack.class, IVertexBuilder.class, List.class, BitSet.class);
    }


    @Override
    public boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer)
    {
        return ability.isSeparated;
    }


    @Override
    public void renderInWorld(MatrixStack matrixStack, Minecraft mc, World world, PlayerEntity player, ISoulPlayer iSoulPlayer, Vec3d projectedView, float partialTicks)
    {
        if(mc.gameSettings.thirdPersonView == 0)
            renderBlocks(mc, world, player, projectedView, matrixStack);


    }

    public static void renderBlocks(Minecraft mc, World world, PlayerEntity player, Vec3d projectedView, MatrixStack matrixStack)
    {
        if(SeparationAbility.isSeparated(player) && isPlayerInsideBlock(player))
        {

            // RenderSystem.fog(2918, 1F, 1F, 1F, 1.0F);
            RenderSystem.enableFog();
            RenderSystem.fogDensity(0.105F);
            RenderSystem.fogMode(2049);

            BlockPos.Mutable blockPos = new BlockPos.Mutable();
            BlockPos playerPos = player.getPosition();

            final int range = 7;
            for (int x = -range; x <= range; x++)
            {
                for (int y = -range; y <= range; y++)
                {
                    for (int z = -range; z <= range; z++)
                    {
                        if(Math.abs(x) == range || Math.abs(y) == range || Math.abs(z) == range)
                        {
                            blockPos.setPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                            renderBlockAgain(mc, world, blockPos, matrixStack, projectedView);
                        }

                    }
                }
            }

            RenderSystem.disableFog();
            FogRenderer.resetFog();


        }
    }

    private static boolean isPlayerInsideBlock(PlayerEntity player)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i < 8; ++i)
        {
            double d0 = player.getPosX() + (double) (((float) (i % 2) - 0.5F) * player.getWidth() * 0.8F);
            double d1 = player.getPosYEye() + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = player.getPosZ() + (double) (((float) ((i >> 2) % 2) - 0.5F) * player.getWidth() * 0.8F);
            blockpos$mutable.setPos(d0, d1, d2);
            BlockState blockstate = player.world.getBlockState(blockpos$mutable);
            if(blockstate.getRenderType() != BlockRenderType.INVISIBLE)
            {
                return true;
            }
        }
        return false;
    }

    private static void renderBlockAgain(Minecraft minecraft, World world, BlockPos pos, MatrixStack matrixStack, Vec3d projectedView)
    {

        ILightReader w = MinecraftForgeClient.getRegionRenderCache(world, pos);
        BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
        BlockModelRenderer blockModelRenderer = blockRendererDispatcher.getBlockModelRenderer();
        BlockState state = world.getBlockState(pos);
        BlockRenderType blockrendertype = state.getRenderType();

        if(blockrendertype == BlockRenderType.MODEL && state.getShape(world, pos).equals(VoxelShapes.fullCube()))
        {
            try
            {
                IBakedModel ibakedmodel = blockRendererDispatcher.getBlockModelShapes().getModel(state);
                IModelData data = ibakedmodel.getModelData(w, pos, state, ModelDataManager.getModelData(world, pos));
                long i = state.getPositionRandom(pos);
                matrixStack.push();

                double offsetX = pos.getX() - projectedView.x;
                double offsetY = pos.getY() - projectedView.y;
                double offsetZ = pos.getZ() - projectedView.z;

                Vec3d vec3d = state.getOffset(w, pos);
                matrixStack.translate(vec3d.x, vec3d.y, vec3d.z);
                matrixStack.translate(offsetX, offsetY, offsetZ);
                IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

                renderBlock(blockModelRenderer, w, ibakedmodel, state, pos, matrixStack, renderTypeBuffer.getBuffer(Atlases.getTranslucentBlockType()), new Random(), i, data);

                renderTypeBuffer.finish();
                matrixStack.pop();
            } catch (InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void renderBlock(BlockModelRenderer renderer, ILightReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, Random randomIn, long rand, net.minecraftforge.client.model.data.IModelData modelData) throws InvocationTargetException, IllegalAccessException
    {
        BitSet bitset = new BitSet(3);

        randomIn.setSeed(rand);
        for (Direction direction : Direction.values())
            if(!Block.shouldSideBeRendered(stateIn, worldIn, posIn, direction))
            {
                List<BakedQuad> list = modelIn.getQuads(stateIn, direction, randomIn, modelData);
                if(!list.isEmpty())
                    renderQuadsFlat.invoke(renderer, worldIn, stateIn, posIn, 15728880, OverlayTexture.NO_OVERLAY, false, matrixStackIn, buffer, list, bitset);
            }
        randomIn.setSeed(rand);
        List<BakedQuad> list1 = modelIn.getQuads(stateIn, null, randomIn, modelData);
        if(!list1.isEmpty())
            renderQuadsFlat.invoke(renderer, worldIn, stateIn, posIn, -1, OverlayTexture.NO_OVERLAY, true, matrixStackIn, buffer, list1, bitset);


    }

    @Override
    public RenderAnimatedPlayer getPlayerRenderer()
    {
        return RENDERER;
    }
}
