package com.pier.snom.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.EnumAbility;
import com.pier.snom.capability.abilities.ISoulAbility;
import com.pier.snom.capability.abilities.SeparationAbility;
import com.pier.snom.capability.render.AbilityRenderer;
import com.pier.snom.capability.render.SeparationAbilityRenderer;
import com.pier.snom.client.particle.SoulPlayerParticleData;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import com.pier.snom.client.render.entity.RenderSoulPlayer;
import com.pier.snom.client.render.soulnomicon.SoulnomiconRenderer;
import com.pier.snom.item.SoulnomiconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents
{


    public static final ResourceLocation ICONS = new ResourceLocation(SoulnomiconMain.ID, "textures/gui/icons.png");


    public static void initRendering()
    {
        SeparationAbilityRenderer.initRenderer();
    }


    @SubscribeEvent
    public static void renderPlayerCustomModel(RenderLivingEvent.Pre<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> event)
    {
        if(event.getRenderer() instanceof RenderSoulPlayer)
            return;

        PlayerEntity watchingPlayer = Minecraft.getInstance().player;

        if(event.getEntity() instanceof AbstractClientPlayerEntity)
        {
            AbstractClientPlayerEntity renderedPlayer = (AbstractClientPlayerEntity) event.getEntity();

            if(!(event.getRenderer() instanceof RenderAnimatedPlayer))
            {
                renderedPlayer.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
                {
                    //prevents to see spectators as a soul player
                    if(!soulPlayer.getAbilitiesManager().getSeparation().isSeparated && renderedPlayer.isSpectator() && SeparationAbility.isSeparated(watchingPlayer))
                        event.setCanceled(true);

                    ISoulAbility<?> ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
                    if(ability != null)
                    {
                        AbilityRenderer<?> abilityRenderer = ability.getRenderer();
                        if(abilityRenderer.shouldRenderPlayer(renderedPlayer, watchingPlayer, soulPlayer))
                        {
                            abilityRenderer.getPlayerRenderer().render(renderedPlayer, renderedPlayer.rotationYaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
                            event.setCanceled(true);
                        }
                    }
                });
            }

        }

    }

    @SubscribeEvent
    public static void renderPlayerCustomHand(RenderHandEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null)
            return;

        boolean isSleeping = mc.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity) mc.getRenderViewEntity()).isSleeping();

        if(mc.gameSettings.thirdPersonView == 0 && !isSleeping && !mc.gameSettings.hideGUI)
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {

                ISoulAbility<?> ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
                if(ability != null)
                {
                    AbilityRenderer<?> abilityRenderer = ability.getRenderer();
                    if(abilityRenderer.shouldRenderCustomHand(player, soulPlayer))
                    {
                        abilityRenderer.renderHand(mc, player, soulPlayer, event.getPartialTicks(), event.getMatrixStack(), event.getBuffers(), event.getLight());
                        event.setCanceled(true);

                    }
                }
            });
    }


    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event)
    {
        PlayerEntity player = event.player;
        World world = player.world;

        ParticleStatus particleStatus = Minecraft.getInstance().gameSettings.particles;

        if(event.side != LogicalSide.CLIENT)
            return;

        if(SeparationAbility.isSeparated(player) && particleStatus != ParticleStatus.MINIMAL)
        {
            AxisAlignedBB playerBox = player.getBoundingBox();

            List<AxisAlignedBB> boxes = world.getCollisionShapes(player, player.getBoundingBox(), Collections.singleton(player)).flatMap((voxelShape) -> voxelShape.toBoundingBoxList().stream()).collect(Collectors.toList());

            for (AxisAlignedBB box : boxes)
            {

                AxisAlignedBB intersectionBox = box.intersect(playerBox);
                AxisAlignedBB particleBox = intersectionBox.grow(0.1D);

                Vec3d center = particleBox.getCenter();

                int particlesAmount = particleStatus == ParticleStatus.ALL ? 3 : 1;

                for (int i = 0; i < particlesAmount; ++i)
                {
                    Random rand = new Random();

                    double particleX = center.x + (rand.nextDouble() - 0.5D) * particleBox.getXSize();
                    double particleY = center.y + (rand.nextDouble() - 0.5D) * particleBox.getYSize();
                    double particleZ = center.z + (rand.nextDouble() - 0.5D) * particleBox.getZSize();
                    Vec3d particleVec = new Vec3d(particleX, particleY, particleZ);

                    Vec3d motion = player.getMotion();

                    if(!intersectionBox.contains(particleVec) && particleBox.contains(particleVec) && !isVec3dInCollisionBox(world, player, particleVec))
                        world.addParticle(new SoulPlayerParticleData(player.getUniqueID()), particleX, particleY, particleZ, -motion.x * 0.5D, -motion.y * 0.6D, -motion.z * 0.5D);
                }
            }
        }

    }

    private static boolean isVec3dInCollisionBox(World world, PlayerEntity player, Vec3d vec3d)
    {
        List<AxisAlignedBB> collisions = world.getCollisionShapes(null, new AxisAlignedBB(vec3d, vec3d).grow(0.01D), Collections.singleton(player)).flatMap((voxelShape) -> voxelShape.toBoundingBoxList().stream()).collect(Collectors.toList());
        return !collisions.isEmpty();
    }


    @SubscribeEvent
    public static void renderWorld(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getInstance();

        AbstractClientPlayerEntity pl = mc.player;
        ClientWorld world = mc.world;
        if(world == null)
            return;

        // SeparationAbilityRenderer.renderWorldLast(mc, world, pl);

        //render soulnomicon near players
        for (AbstractClientPlayerEntity player : world.getPlayers())
        {
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {
                HandSide bookHand = soulPlayer.getAbilitiesManager().bookAbilityHand;
                boolean isFirstPerson = player.equals(pl) && mc.gameSettings.thirdPersonView == 0;
                float f = soulPlayer.getAbilitiesManager().bookFlyingAroundA.getAnimationF(event.getPartialTicks());
                if(f > 0)
                    renderSoulnomicon(player, soulPlayer, event.getPartialTicks(), isFirstPerson, f, bookHand, event.getMatrixStack());

            });
        }


        ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();

        pl.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(iSoulPlayer ->
        {
            for (EnumAbility i : EnumAbility.values())
                if(i != EnumAbility.NONE)
                    iSoulPlayer.getAbilitiesManager().getAbility(i).getRenderer().renderInWorld(event.getMatrixStack(), mc, world, pl, iSoulPlayer, renderInfo.getProjectedView(), event.getPartialTicks());
        });

    }


    private static void renderSoulnomicon(PlayerEntity player, ISoulPlayer soulPlayer, float partialTicks, boolean isFirstPerson, float f, HandSide hand, MatrixStack matrixStack)
    {

        Minecraft mc = Minecraft.getInstance();
        ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();
        RenderSystem.enableBlend();
        double playerX = MathHelper.lerp(partialTicks, player.lastTickPosX, player.getPosX());
        double playerY = MathHelper.lerp(partialTicks, player.lastTickPosY, player.getPosY());
        double playerZ = MathHelper.lerp(partialTicks, player.lastTickPosZ, player.getPosZ());

        float startYawPosition = isFirstPerson ? player.getYaw(partialTicks) : MathHelper.lerp(partialTicks, player.prevRenderYawOffset, player.renderYawOffset);
        float endYawPosition = player.getYaw(partialTicks);
        float yawPosition = startYawPosition + (-startYawPosition + endYawPosition) * f;


        float startPitchPosition = 0F;
        if(isFirstPerson)
            startPitchPosition = player.getPitch(partialTicks);


        float endPitchPosition = player.getPitch(partialTicks);

        float pitchPosition = startPitchPosition + (-startPitchPosition + endPitchPosition) * f;


        double y = playerY + player.getEyeHeight();

        float startOffsetX = 0.45F;
        float endOffsetX = 2F;

        float startOffsetY = -0.95F;
        float endOffsetY = 0.85F;

        float startOffsetZ = 0.35F;
        float endOffsetZ = -1.5F;

        if(isFirstPerson)
        {
            startOffsetY = -0.4F;
            startOffsetX = 1F;
            startOffsetZ = 0.8F;
        }
        if(hand == HandSide.LEFT)
        {
            startOffsetZ = -(isFirstPerson ? 0.7F : 0.4F);
            endOffsetZ = +1.5F;
        }
        float offsetX = startOffsetX + (-startOffsetX + endOffsetX) * f;
        float offsetY = startOffsetY + (-startOffsetY + endOffsetY) * f;
        float offsetZ = startOffsetZ + (-startOffsetZ + endOffsetZ) * f;

        offsetY += MathHelper.sin((partialTicks + player.ticksExisted) * 0.1F) * 0.1F * f;

        double bookX = playerX - renderInfo.getProjectedView().x;
        double bookY = y - renderInfo.getProjectedView().y;
        double bookZ = playerZ - renderInfo.getProjectedView().z;

        matrixStack.push();
        matrixStack.translate(bookX, bookY, bookZ);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-yawPosition - 90F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(-pitchPosition));
        matrixStack.translate(offsetX, offsetY, offsetZ);

        float startRotationYaw = -180F;
        float endRotationYaw = -150F;

        float startRotationPitch = 90F;
        float endRotationPitch = 80F;

        if(isFirstPerson)
        {
            startRotationYaw = hand == HandSide.RIGHT ? -200F : -160F;
            startRotationPitch = 25F;
        }
        if(hand == HandSide.LEFT)
            endRotationYaw -= 50F;


        float rotationYaw = startRotationYaw + (-startRotationYaw + endRotationYaw) * f;
        float rotationPitch = startRotationPitch + (-startRotationPitch + endRotationPitch) * f;

        matrixStack.rotate(Vector3f.YP.rotationDegrees(rotationYaw));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(rotationPitch - 100F * f));
        if(hand == HandSide.RIGHT)
            matrixStack.rotate(Vector3f.XP.rotationDegrees(7F * f));
        else
            matrixStack.rotate(Vector3f.XP.rotationDegrees(-7F * f));
        if(isFirstPerson)
        {
            float scale = 0.7F + 0.3F * f;
            matrixStack.scale(scale, scale, scale);
        }

        if(player instanceof ClientPlayerEntity)
        {
            ClientPlayerEntity clientPlayer = (ClientPlayerEntity) player;
            float armPitch = MathHelper.lerp(partialTicks, clientPlayer.prevRenderArmPitch, clientPlayer.renderArmPitch);
            float armYaw = MathHelper.lerp(partialTicks, clientPlayer.prevRenderArmYaw, clientPlayer.renderArmYaw);
            matrixStack.translate(0F, (player.getPitch(partialTicks) - armPitch) * 0.01F, 0.0F);
            matrixStack.translate(0F, 0.0F, (player.getYaw(partialTicks) - armYaw) * 0.01F);
        }

        int light = mc.getRenderManager().getPackedLight(player, partialTicks);

        mc.gameRenderer.getLightTexture().enableLightmap();
        RenderHelper.enableStandardItemLighting();
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        SoulnomiconRenderer.renderSoulnomicon(player, false, SoulnomiconItem.isDeathNote(soulPlayer.getAbilitiesManager().soulnomiconStack),matrixStack,renderTypeBuffer,light, OverlayTexture.NO_OVERLAY);
        renderTypeBuffer.finish();
        RenderHelper.disableStandardItemLighting();
        mc.gameRenderer.getLightTexture().disableLightmap();
        if(f >= 1.0F)
        {
            float hudF = soulPlayer.getAbilitiesManager().bookFlyingAroundA.getHudAnimationF(partialTicks);
            float openF = soulPlayer.getAbilitiesManager().bookOpeningA.getAnimationF(partialTicks);
            renderSoulHealth(player, soulPlayer, hudF * openF,matrixStack);
        }

        ISoulAbility<?> ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
        if(ability != null)
            ability.getRenderer().renderBookHUD(player, soulPlayer);


        matrixStack.pop();


        RenderSystem.disableBlend();

    }

    @SubscribeEvent
    public static void renderFogColor(EntityViewRenderEvent.FogColors event)
    {

        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null)
            return;

        /*
        if(SeparationAbility.isSeparated(player) && SeparationAbilityRenderer.isPlayerInsideBlock(player))
        {
            event.setBlue(0.75F);
            event.setRed(0.7F);
            event.setGreen(0.7F);
        }

         */
    }


    private static void renderSoulHealth(PlayerEntity player, ISoulPlayer soulPlayer, float f,MatrixStack matrixStack)
    {

        matrixStack.push();
        RenderSystem.enableBlend();
        Minecraft mc = Minecraft.getInstance();

        float health = soulPlayer.getHealth();
        float maxHealth = soulPlayer.getMaxHealth();

        float useSoulAmount = 0F;
        ISoulAbility<?> ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
        if(ability != null)
            useSoulAmount = ability.soulUsePreview(soulPlayer, player);


        float currentHealth = health;

        int ticks = mc.ingameGUI.getTicks();
        ticks = (int) (ticks * 0.5D);
        int tx = ticks % 5;

        float alpha = ((MathHelper.sin(ticks * 0.3F) + 1F) * 0.5F) * 0.3F;
        matrixStack.translate(0.05F + 0.20F * f, 0.1F * (1F - f), 0.15F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-90F));
        matrixStack.scale(-0.03F * f, -0.03F * f, 0.03F * f);

        mc.getTextureManager().bindTexture(ICONS);
        RenderSystem.color4f(0.9F, 0.9F, 0.9F, 0.3F + alpha);
        renderSoulHeart(matrixStack,10F, tx * 9);

        if(!player.isCreative())
        {
            currentHealth = (useSoulAmount >= 0 ? health : Math.max(health + useSoulAmount, 0)) / maxHealth;
            float abilityUseHealth = (useSoulAmount >= 0 ? Math.min(health + useSoulAmount, maxHealth) : health) / maxHealth;

            if(useSoulAmount >= 0)
                RenderSystem.color4f(0.2F, 1F, 0.2F, 0.3F);
            else
                RenderSystem.color4f(1F, 0.2F, 0.2F, 0.3F);

            RenderSystem.translatef(0F, 0F, -0.01F);
            renderSoulHeart(matrixStack,10F * abilityUseHealth, tx * 9);
        }

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        matrixStack.translate(0F, 0F, -0.01F);
        renderSoulHeart(matrixStack,10F * currentHealth, tx * 9);

        RenderSystem.disableBlend();
        matrixStack.pop();


    }

    private static void renderSoulHeart(MatrixStack matrixStack,float height, int texOffset)
    {

        float vMin = (10F - height) / 256F;
        float uMin = texOffset / 256F;

        float uMax = (texOffset + 9F) / 256F;
        float vMax = 10F / 256F;
        float y = (10F - height);

        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix4f,0, height + y, 0).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(matrix4f,9, height + y, 0).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(matrix4f,9, 0 + y, 0).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(matrix4f,0, 0 + y, 0).tex(uMin, vMin).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }


    public static float getPartialTicks()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.isGamePaused() ? 1F : mc.getRenderPartialTicks();
    }

}
