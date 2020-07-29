package com.pier.snom.capability.abilities;

import com.google.common.collect.Sets;
import com.pier.snom.capability.ISoulPlayer;
import com.pier.snom.capability.animation.ClairvoyanceScan;
import com.pier.snom.capability.render.ClairvoyanceAbilityRenderer;
import com.pier.snom.client.gui.ClairvoyanceScreen;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.client.PacketUpdateClairvoyance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class ClairvoyanceAbility implements ISoulAbility<ClairvoyanceAbilityRenderer>
{
    private static final int range = 30;

    public NonNullList<ItemStack> knownItems = NonNullList.create();
    public ItemStack searchStack = ItemStack.EMPTY;
    public int remainingSearchTime = 0;

    private boolean ignoreNBT = false;

    public static Set<BlockPos> highLightedPositions = Sets.newHashSet();

    public ClairvoyanceScan clairvoyanceScan = new ClairvoyanceScan();


    public void addItem(ItemStack stack)
    {
        ItemStack s = stack.copy();
        s.setCount(1);
        if(isItemNew(stack))
            this.knownItems.add(s);
    }

    public void startSearch(ItemStack stack, boolean ignoreNBT)
    {
        this.searchStack = stack.copy();
        this.remainingSearchTime = 100;
        this.ignoreNBT = ignoreNBT;
        this.clairvoyanceScan.startScan();
    }

    private boolean isItemNew(ItemStack stack)
    {
        for (ItemStack s : knownItems)
        {
            if(s.isItemEqualIgnoreDurability(stack) && ItemStack.areItemStackTagsEqual(s, stack))
                return false;
        }
        return true;
    }

    public NonNullList<ItemStack> getAllItems()
    {
        NonNullList<ItemStack> items = NonNullList.create();
        items.addAll(this.knownItems);
        for (Item item : ForgeRegistries.ITEMS.getValues())
        {
            NonNullList<ItemStack> i = NonNullList.create();
            item.fillItemGroup(ItemGroup.SEARCH, i);
            for (ItemStack stack : i)
                if(isItemNew(stack))
                    items.add(stack);

        }
        return items;
    }

    public boolean inventoryContainsStack(IInventory inventory)
    {
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if(this.isValidItem(stack))
                return true;
        }
        return false;
    }

    public boolean isValidItem(ItemStack stack)
    {
        return stack.isItemEqualIgnoreDurability(this.searchStack) && (ignoreNBT || ItemStack.areItemStackTagsEqual(stack, searchStack));
    }

    @Override
    public EnumAbility getAbility()
    {
        return EnumAbility.CLAIRVOYANCE;
    }

    @Override
    public boolean canUse(PlayerEntity player, ISoulPlayer soulPlayer)
    {
        return true;
    }

    @Override
    public void onUpdate(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        if(player.world.isRemote)
            clairvoyanceScan.update(this);
        if(this.remainingSearchTime > 0)
        {
            if(!this.searchStack.isEmpty() && !player.world.isRemote)
            {
                Set<BlockPos> highlightedPositions = Sets.newHashSet();

                if(remainingSearchTime > 1)
                    for (int x = -range; x < range; x++)
                    {
                        for (int y = -range; y < range; y++)
                        {
                            for (int z = -range; z < range; z++)
                            {
                                BlockPos checkPos = new BlockPos.Mutable(player.getPosition()).add(x, y, z);
                                TileEntity tileEntity = player.world.getTileEntity(checkPos);
                                if(tileEntity instanceof IInventory)
                                {
                                    IInventory tileInventory = (IInventory) tileEntity;
                                    if(inventoryContainsStack(tileInventory))
                                        highlightedPositions.add(checkPos);
                                }
                            }
                        }
                    }
                PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketUpdateClairvoyance(highlightedPositions));

            }

            this.remainingSearchTime--;

        }

    }


    @Override
    public boolean active(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        if(player.world.isRemote)
        {
            Minecraft.getInstance().displayGuiScreen(new ClairvoyanceScreen(player, player.isCreative() ? getAllItems() : this.knownItems));
        }
        return false;
    }


    @Override
    public float soulUsePreview(ISoulPlayer soulPlayer, PlayerEntity player)
    {
        return 0;
    }

    @Override
    public void writeToNBT(CompoundNBT nbt)
    {
        ListNBT listnbt = new ListNBT();

        for (ItemStack itemstack : knownItems)
        {
            if(!itemstack.isEmpty())
            {
                CompoundNBT compoundnbt = new CompoundNBT();
                itemstack.write(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }
        if(!listnbt.isEmpty())
            nbt.put("KnownItems", listnbt);

        CompoundNBT stackTag = new CompoundNBT();
        searchStack.write(stackTag);
        nbt.put("searchingStack", stackTag);

        nbt.putInt("remainingSearchTime", this.remainingSearchTime);
        nbt.putBoolean("ignoreNBT", this.ignoreNBT);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        knownItems.clear();

        ListNBT listnbt = nbt.getList("KnownItems", 10);

        for (int i = 0; i < listnbt.size(); ++i)
        {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            knownItems.add(ItemStack.read(compoundnbt));
        }

        CompoundNBT stackTag = nbt.getCompound("searchingStack");
        this.searchStack = ItemStack.read(stackTag);
        this.remainingSearchTime = nbt.getInt("remainingSearchTime");
        this.ignoreNBT = nbt.getBoolean("ignoreNBT");


    }

    @Override
    public ClairvoyanceAbilityRenderer getRenderer()
    {
        return new ClairvoyanceAbilityRenderer(this);
    }
}
