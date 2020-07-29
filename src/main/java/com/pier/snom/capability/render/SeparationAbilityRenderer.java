package com.pier.snom.capability.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.abilities.SeparationAbility;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import com.pier.snom.client.render.entity.RenderSoulPlayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ILightReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Random;

public class SeparationAbilityRenderer extends AbilityRenderer<SeparationAbility>
{

   // public static Method QUADS_SMOOTH;
    public static Constructor occlusionConstructor;

    private static final RenderSoulPlayer RENDERER = new RenderSoulPlayer();

    public SeparationAbilityRenderer(SeparationAbility ability)
    {
        super(ability);
    }


    public static void initRenderer()
    {
        Class<?> occlusionClass = null;

        for (Class<?> cl : BlockModelRenderer.class.getDeclaredClasses())
        {
            if(cl.getSimpleName().equalsIgnoreCase("AmbientOcclusionFace"))
            {
                occlusionClass = cl;
                break;
            }
        }
        if(occlusionClass != null)
        {
            occlusionConstructor = ObfuscationReflectionHelper.findConstructor(occlusionClass, BlockModelRenderer.class);
         //   QUADS_SMOOTH = ObfuscationReflectionHelper.findMethod(BlockModelRenderer.class, "func_217630_a", IEnviromentBlockReader.class, BlockState.class, BlockPos.class, BufferBuilder.class, List.class, float[].class, BitSet.class, occlusionClass);
        }
    }

    @Override
    public boolean shouldRenderPlayer(PlayerEntity player, PlayerEntity watchingPlayer, ISoulPlayer soulPlayer)
    {
        return ability.isSeparated;
    }


    @Override
    public void renderInWorld(MatrixStack matrixStack, Minecraft mc, World world, PlayerEntity player, ISoulPlayer iSoulPlayer, Vec3d projectedView, float partialTicks)
    {


        if(ability.isSeparated && isPlayerInsideBlock(player))
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
                        blockPos.setPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                        if(Math.abs(x) == range || Math.abs(y) == range || Math.abs(z) == range)
                        {
                            renderBlockAgain(mc,world,blockPos,matrixStack,projectedView);
                        }

                    }
                }
            }

            RenderSystem.disableFog();
            FogRenderer.resetFog();


        }
        /*

        BlockPos pos = player.getPosition().up(3);
        ILightReader w = MinecraftForgeClient.getRegionRenderCache(world, pos);
        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        BlockModelRenderer blockModelRenderer = blockRendererDispatcher.getBlockModelRenderer();
        BlockState state = Blocks.DIAMOND_BLOCK.getDefaultState();
        IBakedModel ibakedmodel = blockRendererDispatcher.getBlockModelShapes().getModel(state);
        IModelData data = ibakedmodel.getModelData(w, pos, state, ModelDataManager.getModelData(world, pos));
        long i = state.getPositionRandom(pos);
        matrixStack.push();


        double offsetX = pos.getX() - projectedView.x;
        double offsetY = pos.getY() - projectedView.y;
        double offsetZ = pos.getZ() - projectedView.z;

        Vec3d vec3d = state.getOffset(w, pos);
        matrixStack.translate(vec3d.x, vec3d.y, vec3d.z);
        matrixStack.translate(offsetX,offsetY,offsetZ);
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        blockModelRenderer.renderModelFlat(w, ibakedmodel, state, pos, matrixStack, renderTypeBuffer.getBuffer(Atlases.getSolidBlockType()), false, new Random(), i, OverlayTexture.NO_OVERLAY, data);
        renderTypeBuffer.finish();
        matrixStack.pop();
        */
    }

    private static boolean isPlayerInsideBlock(PlayerEntity player)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(int i = 0; i < 8; ++i) {
            double d0 = player.getPosX() + (double)(((float)(i % 2) - 0.5F) * player.getWidth() * 0.8F);
            double d1 = player.getPosYEye() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = player.getPosZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * player.getWidth() * 0.8F);
            blockpos$mutable.setPos(d0, d1, d2);
            BlockState blockstate = player.world.getBlockState(blockpos$mutable);
            if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                return true;
            }
        }
        return false;
    }

    private static void renderBlockAgain(Minecraft minecraft,World world,BlockPos pos,MatrixStack matrixStack,Vec3d projectedView)
    {

        ILightReader w = MinecraftForgeClient.getRegionRenderCache(world, pos);
        BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
        BlockModelRenderer blockModelRenderer = blockRendererDispatcher.getBlockModelRenderer();
        BlockState state = world.getBlockState(pos);
        BlockRenderType blockrendertype = state.getRenderType();

        if(blockrendertype == BlockRenderType.MODEL)
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
            blockModelRenderer.renderModelFlat(w, ibakedmodel, state, pos, matrixStack, renderTypeBuffer.getBuffer(Atlases.getTranslucentCullBlockType()), false, new Random(), i, OverlayTexture.NO_OVERLAY, data);
            renderTypeBuffer.finish();
            matrixStack.pop();
        }
    }

    /*
        public static void renderWorldLast(Minecraft mc, World world, PlayerEntity player)
        {

            BlockPos playerPos = player.getPosition();

            GlStateManager.disableFog();
            mc.gameRenderer.setupFogColor(false);
            GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1F);
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            GlStateManager.fogDensity(0.105F);
            GlStateManager.enableColorMaterial();
            GlStateManager.enableFog();
            GlStateManager.colorMaterial(1028, 4608);


            if(SeparationAbility.isSeparated(player) && isPlayerInsideBlock(player))
            {
                int range = 7;
                for (int x = -range; x <= range; x++)
                {
                    for (int y = -range; y <= range; y++)
                    {
                        for (int z = -range; z <= range; z++)
                        {
                            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                            if(Math.abs(x) >= range || Math.abs(y) >= range || Math.abs(z) >= range)
                                renderBlockAgain(world, pos);
                        }
                    }
                }
            }
            GlStateManager.disableFog();


        }

        public static boolean isPlayerInsideBlock(PlayerEntity player)
        {

            try (BlockPos.PooledMutableBlockPos position = BlockPos.PooledMutableBlockPos.retain())
            {
                for (int i = 0; i < 8; ++i)
                {
                    int j = MathHelper.floor(player.posY + (double) (((float) ((i) % 2) - 0.5F) * 0.6F) + (double) player.getEyeHeight());
                    int k = MathHelper.floor(player.posX + (double) (((float) ((i >> 1) % 2) - 0.5F) * player.getSize(Pose.STANDING).width * 1.2F));
                    int l = MathHelper.floor(player.posZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * player.getSize(Pose.STANDING).width * 1.2F));
                    if(position.getX() != k || position.getY() != j || position.getZ() != l)
                    {
                        position.setPos(k, j, l);
                        BlockState state = player.world.getBlockState(position);
                        boolean isOpaque = state.getMaterial().isOpaque();
                        if(isOpaque)
                            return true;

                    }
                }
            }
            return false;
        }

        private static void renderBlockAgain(World world, BlockPos pos)
        {
            BlockState state = world.getBlockState(pos);

            if(Block.isOpaque(state.getCollisionShape(world, pos)))
            {
                Minecraft mc = Minecraft.getInstance();

                ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();


                double d0 = renderInfo.getProjectedView().x;
                double d1 = renderInfo.getProjectedView().y;
                double d2 = renderInfo.getProjectedView().z;


                GlStateManager.pushMatrix();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();

                BlockRendererDispatcher renderer = mc.getBlockRendererDispatcher();

                IModelData model = renderer.getModelForState(state).getModelData(world, pos, state, ModelDataManager.getModelData(world, new BlockPos(pos)));


                bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
                bufferBuilder.setTranslation(-d0, -d1, -d2);

                renderModelSmooth(renderer, world, state, pos, bufferBuilder, new Random(), model);

                tessellator.draw();
                GlStateManager.popMatrix();

            }
        }


        private static void renderModelSmooth(BlockRendererDispatcher dispatcher, IEnviromentBlockReader world, BlockState state, BlockPos pos, BufferBuilder bufferBuilderIn, Random rand, IModelData modelData)
        {
            long seed = state.getPositionRandom(pos);
            IBakedModel bakedModel = dispatcher.getModelForState(state);


            try
            {
                Object ambientOcclusionFace = occlusionConstructor.newInstance(dispatcher.getBlockModelRenderer());


                float[] afloat = new float[Direction.values().length * 2];
                BitSet bitset = new BitSet(3);

                for (Direction direction : Direction.values())
                {
                    rand.setSeed(seed);
                    if(!Block.shouldSideBeRendered(state, world, pos, direction))
                    {
                        List<BakedQuad> list = bakedModel.getQuads(state, direction, rand, modelData);
                        if(!list.isEmpty())
                        {
                            renderQuadsSmooth(dispatcher, world, state, pos, bufferBuilderIn, list, afloat, bitset, ambientOcclusionFace);
                        }
                    }

                }
                rand.setSeed(seed);
                List<BakedQuad> list = bakedModel.getQuads(state, null, rand, modelData);
                if(!list.isEmpty())
                {
                    renderQuadsSmooth(dispatcher, world, state, pos, bufferBuilderIn, list, afloat, bitset, ambientOcclusionFace);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }



        private static void renderQuadsSmooth(BlockRendererDispatcher dispatcher, IEnviromentBlockReader world, BlockState state, BlockPos pos, BufferBuilder bufferBuilderIn, List<BakedQuad> quads, float[] weights, BitSet bitSet, Object ambientOcclusionFace) throws InvocationTargetException, IllegalAccessException
        {
            BlockModelRenderer modelRenderer = dispatcher.getBlockModelRenderer();
            QUADS_SMOOTH.invoke(modelRenderer, world, state, pos, bufferBuilderIn, quads, weights, bitSet, ambientOcclusionFace);
        }


     */
    @Override
    public RenderAnimatedPlayer getPlayerRenderer()
    {
        return RENDERER;
    }

    private static class WorldLight implements ILightReader
    {

        private final ILightReader originalReader;

        public WorldLight(ILightReader originalReader)
        {
            this.originalReader = originalReader;
        }

        @Override
        public WorldLightManager getLightManager()
        {
            return originalReader.getLightManager();
        }

        @Override
        public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn)
        {
            return originalReader.getBlockColor(blockPosIn,colorResolverIn);
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos)
        {
            return originalReader.getTileEntity(pos);
        }

        @Override
        public BlockState getBlockState(BlockPos pos)
        {
            return originalReader.getBlockState(pos);
        }

        @Override
        public IFluidState getFluidState(BlockPos pos)
        {
            return originalReader.getFluidState(pos);
        }

        @Override
        public int getLightFor(LightType lightTypeIn, BlockPos blockPosIn)
        {
            return 15;
        }

        @Override
        public int getLightSubtracted(BlockPos blockPosIn, int amount)
        {
            return 15;
        }
    }
}
