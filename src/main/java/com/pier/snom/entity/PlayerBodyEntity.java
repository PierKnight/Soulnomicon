package com.pier.snom.entity;

import com.pier.snom.capability.PlayerData;
import com.pier.snom.init.ModDamageSource;
import com.pier.snom.world.SavedBodyPlayers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerBodyEntity extends MobEntity
{

    private static final DataParameter<CompoundNBT> PLAYER_NBT = EntityDataManager.createKey(TameableEntity.class, DataSerializers.COMPOUND_NBT);


    public double prevChasingPosX;
    public double prevChasingPosY;
    public double prevChasingPosZ;
    public double chasingPosX;
    public double chasingPosY;
    public double chasingPosZ;

    public float prevCameraYaw;
    public float cameraYaw;

    private float bodYaw = 0F;
    private float headYaw = 0F;
    private float headPitch = 0F;


    public PlayerBodyEntity(EntityType<? extends MobEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    public PlayerBodyEntity(World worldIn, float bodYaw, float headYaw, float headPitch)
    {
        super(EntityRegistry.PLAYER_BODY_ENTITY, worldIn);
        this.bodYaw = rotationYaw;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
    }


    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(0.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(PLAYER_NBT, new CompoundNBT());
    }

    protected void registerGoals()
    {
    }

    public boolean isInvulnerableTo(@Nonnull DamageSource source)
    {
        PlayerData data = getPlayerData();
        return data != null ? (data.isCreative() || data.isInvulnerable()) && !source.canHarmInCreative() : super.isInvulnerableTo(source);
    }

    @Override
    protected void setRotation(float yaw, float pitch)
    {
    }

    public void setPlayerSoul(PlayerEntity player)
    {
        CompoundNBT nbt = new CompoundNBT();
        player.writeWithoutTypeId(nbt);
        nbt.putUniqueId("UniqueUUID", player.getUniqueID());
        nbt.putString("PlayerName", player.getName().getFormattedText());
        player.writeAdditional(nbt);
        nbt.putBoolean("isCreative", player.isCreative());
        setPlayerNBT(nbt);

        for (EquipmentSlotType slotType : EquipmentSlotType.values())
            this.setItemStackToSlot(slotType, player.getItemStackFromSlot(slotType));
        this.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);

        nbt.putFloat("bodyYaw", player.rotationYaw);
        nbt.putFloat("headPitch", player.rotationPitch);
        nbt.putFloat("headYaw", player.rotationYawHead);
        nbt.putFloat("renderYawOffset", player.renderYawOffset);

        this.setSneaking(player.isSneaking() && !player.abilities.isFlying);
        this.setLeftHanded(player.getPrimaryHand() == HandSide.LEFT);

    }

    @Override
    public boolean isCrouching()
    {
        PlayerEntity player = getSoulPlayer();
        if(player != null)
        {
            return player.isCrouching();
        }
        return super.isCrouching();
    }

    private CompoundNBT getPlayerNBT()
    {
        return this.dataManager.get(PLAYER_NBT);
    }

    private void setPlayerNBT(CompoundNBT CompoundNBT)
    {
        this.dataManager.set(PLAYER_NBT, CompoundNBT);
    }

    @Nullable
    public PlayerEntity getSoulPlayer()
    {
        PlayerData playerData = getPlayerData();
        if(playerData != null)
        {
            UUID uuid = playerData.getPlayerUUID();
            return world.getPlayerByUuid(uuid);
        }
        return null;
    }

    @Nullable
    public PlayerData getPlayerData()
    {
        CompoundNBT plTag = getPlayerNBT();
        if(!plTag.isEmpty())
            return new PlayerData(plTag);
        return null;
    }

    public boolean canDespawn(double distanceToClosestPlayer)
    {
        return false;
    }

    public void onDeath(@Nonnull DamageSource cause)
    {


        PlayerEntity player = getSoulPlayer();
        if(player != null)
        {

            PlayerData playerData = getPlayerData();
            playerData.readPlayerInventory(player);
            playerData.readPlayerGameType(player);


            player.setPositionAndUpdate(getPosX(), getPosY(), getPosZ());

            ModDamageSource.BodyDamageSource bodyDamageSource = new ModDamageSource.BodyDamageSource(this);
            player.attackEntityFrom(bodyDamageSource, Integer.MAX_VALUE);


        }
        super.onDeath(cause);

    }

    public void logOutBody()
    {
        if(!this.world.isRemote && this.getPlayerData() != null)
        {
            SavedBodyPlayers save = SavedBodyPlayers.getSave(world);
            CompoundNBT bodyData = new CompoundNBT();
            writeWithoutTypeId(bodyData);
            save.savePlayerBody(this.getPlayerData(), bodyData);

            this.remove();

            System.out.println("logOut");
        }
    }


    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        CompoundNBT plTag = getPlayerNBT();
        compound.put("PlayerDataNBT", plTag);
    }

    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        setPlayerNBT(compound.getCompound("PlayerDataNBT"));
    }

    protected float getDropChance(EquipmentSlotType slotIn)
    {
        return super.getDropChance(slotIn);
    }

    protected void updateEquipmentIfNeeded(ItemEntity itemEntity)
    {
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn)
    {
    }

    public void tick()
    {
        super.tick();
        this.updateCape();

        PlayerData playerData = getPlayerData();
        if(playerData != null)
        {
            if(playerData.isFlying())
                this.setMotion(this.getMotion().mul(1D, 0, 1D));
            float headPitch = playerData.playerTag.getFloat("headPitch");
            float headYaw = playerData.playerTag.getFloat("headYaw");
            float bodyYaw = playerData.playerTag.getFloat("bodyYaw");
            float renderYawOffset = playerData.playerTag.getFloat("renderYawOffset");
            this.rotationPitch = headPitch;
            this.prevRotationPitch = rotationPitch;
            this.rotationYaw = bodyYaw;
            this.prevRotationYaw = bodyYaw;
            this.rotationYawHead = headYaw;
            this.prevRotationYawHead = headYaw;
            this.renderYawOffset = renderYawOffset;
            this.prevRenderYawOffset = this.renderYawOffset;

        }

    }

    private void updateCape()
    {
        this.prevCameraYaw = this.cameraYaw;
        float f;
        if(this.onGround && !(this.getHealth() <= 0.0F) && !this.isSwimming())
        {
            f = Math.min(0.1F, MathHelper.sqrt(horizontalMag(this.getMotion())));
        }
        else
        {
            f = 0.0F;
        }
        this.cameraYaw += (f - this.cameraYaw) * 0.4F;

        this.prevChasingPosX = this.chasingPosX;
        this.prevChasingPosY = this.chasingPosY;
        this.prevChasingPosZ = this.chasingPosZ;
        double d0 = this.getPosX() - this.chasingPosX;
        double d1 = this.getPosY() - this.chasingPosY;
        double d2 = this.getPosZ() - this.chasingPosZ;
        double d3 = 10.0D;
        if(d0 > 10.0D)
        {
            this.chasingPosX = this.getPosX();
            this.prevChasingPosX = this.chasingPosX;
        }

        if(d2 > 10.0D)
        {
            this.chasingPosZ = this.getPosZ();
            this.prevChasingPosZ = this.chasingPosZ;
        }

        if(d1 > 10.0D)
        {
            this.chasingPosY = this.getPosY();
            this.prevChasingPosY = this.chasingPosY;
        }

        if(d0 < -10.0D)
        {
            this.chasingPosX = this.getPosX();
            this.prevChasingPosX = this.chasingPosX;
        }

        if(d2 < -10.0D)
        {
            this.chasingPosZ = this.getPosZ();
            this.prevChasingPosZ = this.chasingPosZ;
        }

        if(d1 < -10.0D)
        {
            this.chasingPosY = this.getPosY();
            this.prevChasingPosY = this.chasingPosY;
        }

        this.chasingPosX += d0 * 0.25D;
        this.chasingPosZ += d2 * 0.25D;
        this.chasingPosY += d1 * 0.25D;


    }

    @Override
    public void updateRidden()
    {
        super.updateRidden();
        if(this.world.isRemote && !this.isCrouching() && !this.isPassenger())
        {

            this.prevCameraYaw = this.cameraYaw;
            this.cameraYaw = 0.0F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getAlwaysRenderNameTagForRender()
    {
        return getPlayerData() != null || super.getAlwaysRenderNameTagForRender();
    }

    @Override
    @Nonnull
    public ITextComponent getName()
    {
        PlayerData playerData = getPlayerData();
        if(playerData != null)
            return new StringTextComponent(playerData.getPlayerName());
        return super.getName();
    }
}
