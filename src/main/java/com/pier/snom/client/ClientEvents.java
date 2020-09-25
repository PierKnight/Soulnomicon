package com.pier.snom.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.capability.abilities.AbilitiesManager;
import com.pier.snom.capability.abilities.EnumAbility;
import com.pier.snom.capability.abilities.ISoulAbility;
import com.pier.snom.capability.abilities.SeparationAbility;
import com.pier.snom.capability.render.AbilityRenderer;
import com.pier.snom.client.particle.SoulPlayerParticleData;
import com.pier.snom.client.render.entity.RenderAnimatedPlayer;
import com.pier.snom.client.render.entity.RenderSoulPlayer;
import com.pier.snom.client.render.soulnomicon.SoulnomiconRenderer;
import com.pier.snom.item.SoulnomiconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
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


    @SubscribeEvent
    public static void renderPlayerCustomModel(RenderLivingEvent.Pre<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> event)
    {
        if(event.getRenderer() instanceof RenderSoulPlayer)
            return;

        PlayerEntity watchingPlayer = Minecraft.getInstance().player;

        if(event.getEntity() instanceof AbstractClientPlayerEntity && watchingPlayer != null)
        {
            AbstractClientPlayerEntity renderedPlayer = (AbstractClientPlayerEntity) event.getEntity();

            if(!(event.getRenderer() instanceof RenderAnimatedPlayer))
            {
                renderedPlayer.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
                {
                    //prevents to see spectators as a soul player
                    if(renderedPlayer.isSpectator() && SeparationAbility.isSeparated(watchingPlayer))
                        event.setCanceled(true);

                    //prevents to see soul players in spectator
                    if(watchingPlayer.isSpectator() && soulPlayer.getAbilitiesManager().getSeparation().isSeparated)
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
        ClientPlayerEntity player = mc.player;
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
                    if(abilityRenderer.shouldRenderCustomHand(player, event.getHand(), soulPlayer))
                    {
                        abilityRenderer.renderHand(mc, player, soulPlayer, event);
                        event.setCanceled(true);

                    }
                }
                renderSoulnomiconFirstPerson(player, soulPlayer, mc, event);

            });


    }


    private static int separationHUD = 0;

    @SubscribeEvent
    public static void renderSeparationHUD(RenderGameOverlayEvent.Pre event)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if(player == null)
            return;
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(iSoulPlayer ->
            {
                AbilitiesManager abilitiesManager = iSoulPlayer.getAbilitiesManager();
                if(abilitiesManager.getSeparation().isSeparated)
                {
                    float width = event.getWindow().getScaledWidth();
                    float height = event.getWindow().getScaledHeight();
                    float scale = 80.0F;

                    if(separationHUD < 30)
                        separationHUD++;

                    float animation = separationHUD / 30F;

                    MatrixStack matrixStack = new MatrixStack();
                    RenderSystem.pushMatrix();
                    matrixStack.push();
                    matrixStack.translate(width / 2D, height, 0.0D);
                    matrixStack.translate(-10F, -animation * 30F, 0F);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));
                    matrixStack.scale(scale, scale, scale);
                    renderSoulHealth(player, iSoulPlayer, animation, matrixStack);
                    matrixStack.pop();
                    RenderSystem.popMatrix();

                }
                else if(separationHUD > 0)
                    separationHUD = 0;

            });
        }

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

    public static void resetBlockBreaking(BlockPos blockPos)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.world != null && mc.playerController != null && mc.player != null)
        {
            mc.world.sendBlockBreakProgress(mc.player.getEntityId(), blockPos, -1);
        }
    }

    public static void damageBlock(PlayerEntity player, BlockPos blockPos, Direction direction)
    {
        Minecraft mc = Minecraft.getInstance();
        if(player.equals(mc.player) && mc.playerController != null)
            mc.playerController.onPlayerDamageBlock(blockPos, direction);

    }


    @SubscribeEvent
    public static void cancelHighLightWhileUsingBeam(DrawHighlightEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(player == null)
            return;
        player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(iSoulPlayer ->
        {
            if(iSoulPlayer.getAbilitiesManager().getBeamAbility().isActive())
                event.setCanceled(true);
        });
    }


    private static boolean isVec3dInCollisionBox(World world, PlayerEntity player, Vec3d vec3d)
    {
        List<AxisAlignedBB> collisions = world.getCollisionShapes(null, new AxisAlignedBB(vec3d, vec3d).grow(0.01D), Collections.singleton(player)).flatMap((voxelShape) -> voxelShape.toBoundingBoxList().stream()).collect(Collectors.toList());
        return !collisions.isEmpty();
    }


    public static long test = 0;

    @SubscribeEvent
    public static void renderWorld(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getInstance();

        AbstractClientPlayerEntity pl = mc.player;
        ClientWorld world = mc.world;
        if(world == null)
            return;

        if(!mc.isGamePaused())
            test += 1.2;

        //render soulnomicon near players
        for (AbstractClientPlayerEntity player : world.getPlayers())
        {
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(soulPlayer ->
            {
                HandSide bookHand = soulPlayer.getAbilitiesManager().bookAbilityHand;

                LivingEntity entityIn = player;
                if(soulPlayer.getAbilitiesManager().getSeparation().isSeparated)
                    entityIn = SeparationAbility.getPlayerBodyBody(player);

                if(entityIn != null && !(entityIn.equals(pl) && mc.gameSettings.thirdPersonView == 0))
                    renderSoulnomiconThirdPerson(entityIn, soulPlayer, event.getPartialTicks(), bookHand, event.getMatrixStack());

                ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();


                for (EnumAbility i : EnumAbility.values())
                    if(i != EnumAbility.NONE)
                        soulPlayer.getAbilitiesManager().getAbility(i).getRenderer().renderInWorld(event.getMatrixStack(), mc, world, player, soulPlayer, renderInfo.getProjectedView(), event.getPartialTicks());


            });
        }


    }

    @SubscribeEvent
    public static void renderWorld(PlaySoundEvent event)
    {
        ISound sound = event.getSound();
        World world = Minecraft.getInstance().world;
        if(world != null && shouldCancelSound(sound))
        {
            AxisAlignedBB box = new AxisAlignedBB(sound.getX(), sound.getY(), sound.getZ(), sound.getX(), sound.getY(), sound.getZ()).grow(0.2D);
            List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, box);
            if(!players.isEmpty())
            {
                PlayerEntity player = players.get(0);
                player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(iSoulPlayer ->
                {
                    if(iSoulPlayer.getAbilitiesManager().getBeamAbility().isActive())
                        event.setResultSound(null);
                });
            }
        }
    }

    @SubscribeEvent
    public static void renderItemFrame(RenderItemInFrameEvent event)
    {


        MatrixStack matrixStack = event.getMatrix();
        ItemStack stack = event.getItem();

        CompoundNBT tag = stack.getTag();

        if(tag != null && tag.getBoolean("isDungeonSilhouette"))
        {
            matrixStack.scale(0.5F, 0.5F, 0.5F);

            RenderType rendertype = RenderTypeLookup.getRenderType(stack);

            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
            ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, ibakedmodel, ItemCameraTransforms.TransformType.FIXED, false);
            IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(event.getBuffers(), rendertype, true, false);
            matrixStack.push();
            matrixStack.translate(-0.5D, -0.5D, -0.5D);
            renderSilhouetteModel(ibakedmodel, stack, event.getLight(), matrixStack, ivertexbuilder);
            matrixStack.pop();

            event.setCanceled(true);
        }

    }

    @SuppressWarnings("deprecation")
    private static void renderSilhouetteModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn)
    {
        Random random = new Random();

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderBlackQuads(matrixStackIn, bufferIn, modelIn.getQuads(null, direction, random), stack, combinedLightIn, OverlayTexture.NO_OVERLAY);
        }

        random.setSeed(42L);
        renderBlackQuads(matrixStackIn, bufferIn, modelIn.getQuads(null, null, random), stack, combinedLightIn, OverlayTexture.NO_OVERLAY);
    }

    private static void renderBlackQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn)
    {
        MatrixStack.Entry entry = matrixStackIn.getLast();

        for (BakedQuad bakedquad : quadsIn)
        {
            bufferIn.addVertexData(entry, bakedquad, 0F, 0F, 0F, combinedLightIn, combinedOverlayIn, true);
        }

    }


    private static boolean shouldCancelSound(ISound sound)
    {
        ResourceLocation soundLocation = sound.getSoundLocation();
        if(isSameSound(soundLocation, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP))
            return true;
        else if(isSameSound(soundLocation, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE))
            return true;
        else if(isSameSound(soundLocation, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT))
            return true;
        else if(isSameSound(soundLocation, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK))
            return true;
        else if(isSameSound(soundLocation, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG))
            return true;
        else
            return isSameSound(soundLocation, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK);

    }

    private static boolean isSameSound(ResourceLocation soundLocation, SoundEvent soundEvent)
    {
        return soundLocation.equals(soundEvent.getRegistryName());
    }

    private static void renderSoulnomiconThirdPerson(LivingEntity player, ISoulPlayer soulPlayer, float partialTicks, HandSide hand, MatrixStack matrixStack)
    {
        float animationProgress = soulPlayer.getAbilitiesManager().bookFlyingAroundA.getAnimationF(partialTicks);
        if(animationProgress == 0.0F)
            return;

        Minecraft mc = Minecraft.getInstance();
        ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();
        double playerX = MathHelper.lerp(partialTicks, player.lastTickPosX, player.getPosX());
        double playerY = MathHelper.lerp(partialTicks, player.lastTickPosY, player.getPosY());
        double playerZ = MathHelper.lerp(partialTicks, player.lastTickPosZ, player.getPosZ());

        float startYawPosition = MathHelper.lerp(partialTicks, player.prevRenderYawOffset, player.renderYawOffset);
        float endYawPosition = player.getYaw(partialTicks);
        float yawPosition = startYawPosition + (-startYawPosition + endYawPosition) * animationProgress;

        float startPitchPosition = 0F;


        float endPitchPosition = player.getPitch(partialTicks);

        float pitchPosition = startPitchPosition + (-startPitchPosition + endPitchPosition) * animationProgress;


        double y = playerY + player.getEyeHeight();

        float startOffsetX = 0.45F;
        float endOffsetX = 2F;

        float startOffsetY = -0.95F;
        float endOffsetY = 0.85F;

        float startOffsetZ = 0.35F;
        float endOffsetZ = -1.5F;

        if(hand == HandSide.LEFT)
        {
            startOffsetZ = -0.4F;
            endOffsetZ = +1.5F;
        }
        float offsetX = startOffsetX + (-startOffsetX + endOffsetX) * animationProgress;
        float offsetY = startOffsetY + (-startOffsetY + endOffsetY) * animationProgress;
        float offsetZ = startOffsetZ + (-startOffsetZ + endOffsetZ) * animationProgress;

        offsetY += MathHelper.sin((partialTicks + player.ticksExisted) * 0.1F) * 0.1F * animationProgress;

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

        if(hand == HandSide.LEFT)
            endRotationYaw -= 50F;


        float rotationYaw = startRotationYaw + (-startRotationYaw + endRotationYaw) * animationProgress;
        float rotationPitch = startRotationPitch + (-startRotationPitch + endRotationPitch) * animationProgress;

        matrixStack.rotate(Vector3f.YP.rotationDegrees(rotationYaw));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(rotationPitch - 100F * animationProgress));
        if(hand == HandSide.RIGHT)
            matrixStack.rotate(Vector3f.XP.rotationDegrees(7F * animationProgress));
        else
            matrixStack.rotate(Vector3f.XP.rotationDegrees(-7F * animationProgress));

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
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        SoulnomiconRenderer.renderSoulnomicon(soulPlayer, false, SoulnomiconItem.isDeathNote(soulPlayer.getAbilitiesManager().soulnomiconStack), matrixStack, renderTypeBuffer, light, OverlayTexture.NO_OVERLAY);
        renderTypeBuffer.finish();

        mc.gameRenderer.getLightTexture().disableLightmap();
        matrixStack.pop();

    }

    private static void renderSoulnomiconFirstPerson(PlayerEntity player, ISoulPlayer soulPlayer, Minecraft mc, RenderHandEvent event)
    {
        MatrixStack matrixStack = event.getMatrixStack();
        float partialTicks = event.getPartialTicks();

        AbilitiesManager abilitiesManager = soulPlayer.getAbilitiesManager();
        float animationProgress = abilitiesManager.bookFlyingAroundA.getAnimationF(partialTicks, 0, 8);
        ItemStack soulnomiconStack = abilitiesManager.soulnomiconStack;


        boolean flag = event.getHand() == Hand.MAIN_HAND;
        HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        if(animationProgress == 0.0F || soulnomiconStack.isEmpty() || handside != abilitiesManager.bookAbilityHand)
            return;

        matrixStack.push();

        int i = handside == HandSide.RIGHT ? -1 : 1;
        if(mc.gameSettings.viewBobbing)
            cancelBobbing(player, matrixStack, partialTicks);

        matrixStack.translate((float) -i * 0.56F, -0.52F, -0.72F);
        matrixStack.translate(1.5F * animationProgress * i, 0.85F * animationProgress, -0.7F * animationProgress);
        matrixStack.translate(0F, MathHelper.sin((partialTicks + player.ticksExisted) * 0.1F) * 0.05F * animationProgress, 0);

        matrixStack.rotate(Vector3f.XN.rotationDegrees(-45F * animationProgress));
        matrixStack.rotate(Vector3f.YN.rotationDegrees(35F * animationProgress * i));
        matrixStack.rotate(Vector3f.ZN.rotationDegrees(-10F * animationProgress * i));
        mc.getFirstPersonRenderer().renderItemSide(player, soulnomiconStack, handside == HandSide.RIGHT ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, handside == HandSide.LEFT, matrixStack, event.getBuffers(), event.getLight());
        float f = abilitiesManager.bookFlyingAroundA.getAnimationF(partialTicks, 3, 8) * abilitiesManager.bookOpeningA.getAnimationF(partialTicks);

        if(f > 0F)
        {
            matrixStack.push();
            matrixStack.scale(f, f, f);

            matrixStack.translate((1F - f) * 0.3F * i, 0.2F + (1F - f) * 1.1F, -(1F - f) * 1.3F);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(-180F * i));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-100F * i));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(10F));
            matrixStack.translate(0F, 0F, -0.09F);
            renderSoulHealth(player, soulPlayer, f, matrixStack);
            ISoulAbility<?> ability = soulPlayer.getAbilitiesManager().getSelectedAbility();
            if(ability != null)
                ability.getRenderer().renderBookHUD(player, soulPlayer);

            matrixStack.pop();
        }
        matrixStack.pop();

    }


    private static void cancelBobbing(PlayerEntity player, MatrixStack matrixStack, float partialTicks)
    {
        float f = player.distanceWalkedModified - player.prevDistanceWalkedModified;
        float f1 = -(player.distanceWalkedModified + f * partialTicks);
        float f2 = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);
        matrixStack.translate(-MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F, Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0D);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(-MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));

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


    private static void renderSoulHealth(PlayerEntity player, ISoulPlayer soulPlayer, float f, MatrixStack matrixStack)
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
        // matrixStack.translate(0.05F + 0.20F * f, 0.1F * (1F - f), 0.15F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-90F));
        matrixStack.scale(0.03F, 0.03F, 0.03F);
        mc.getTextureManager().bindTexture(ICONS);
        RenderSystem.color4f(0.9F, 0.9F, 0.9F, 0.3F + alpha);
        renderSoulHeart(matrixStack, 10F, tx * 9);

        if(!player.isCreative())
        {
            currentHealth = (useSoulAmount >= 0 ? health : Math.max(health + useSoulAmount, 0)) / maxHealth;
            float abilityUseHealth = (useSoulAmount >= 0 ? Math.min(health + useSoulAmount, maxHealth) : health) / maxHealth;

            if(useSoulAmount >= 0)
                RenderSystem.color4f(0.2F, 1F, 0.2F, 0.3F * f);
            else
                RenderSystem.color4f(1F, 0.2F, 0.2F, 0.3F * f);

            renderSoulHeart(matrixStack, 10F * abilityUseHealth, tx * 9);
        }

        RenderSystem.color4f(1F, 1F, 1F, 1F * f);
        renderSoulHeart(matrixStack, 10F * currentHealth, tx * 9);

        RenderSystem.disableBlend();
        matrixStack.pop();


    }

    private static void renderSoulHeart(MatrixStack matrixStack, float height, int texOffset)
    {

        float vMin = (10F - height) / 256F;
        float uMin = texOffset / 256F;

        float uMax = (texOffset + 9F) / 256F;
        float vMax = 10F / 256F;
        float y = (10F - height);

        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix4f, 0, height + y, 0).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(matrix4f, 9, height + y, 0).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(matrix4f, 9, 0 + y, 0).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(matrix4f, 0, 0 + y, 0).tex(uMin, vMin).endVertex();
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
