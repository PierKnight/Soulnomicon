package com.pier.snom.world.structure.sections;

import com.google.common.collect.Lists;
import com.pier.snom.init.ModBlocks;
import com.pier.snom.tile.DungeonButtonTile;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class ItemQuestionSection extends RoomSection
{

    private static final RoomEntry[] ENTRIES = new RoomEntry[]{new RoomEntry(Items.OAK_SAPLING, Items.ACACIA_SAPLING, Items.BIRCH_SAPLING, Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING, Items.SPRUCE_SAPLING),
            new RoomEntry(Items.BREAD, Items.PORKCHOP, Items.BEEF, Items.ROTTEN_FLESH, Items.MUTTON, Items.BAKED_POTATO),
            new RoomEntry(Items.POPPY, Items.ALLIUM, Items.CORNFLOWER, Items.BROWN_MUSHROOM, Items.LILY_OF_THE_VALLEY, Items.BLUE_ORCHID),
            new RoomEntry(Items.SUGAR, Items.KELP, Items.BAMBOO, Items.STICK, Items.INK_SAC, Items.RED_DYE),
            new RoomEntry(Items.TURTLE_EGG, Items.EGG, Items.BAT_SPAWN_EGG, Items.ENDER_PEARL, Items.SNOWBALL, Items.CLAY),
            new RoomEntry(Items.COAL, Items.DIAMOND, Items.EMERALD, Items.QUARTZ, Items.CHARCOAL, Items.COAL)};

    private UUID dungeonUUID;
    private int timer = 0;
    private final RoomEntry[] roomEntries = new RoomEntry[3];
    private int n = 0;

    public ItemQuestionSection(Direction direction, MutableBoundingBox roomBox)
    {
        super(direction, roomBox);
    }

    public void init(UUID dungeonUUID, Random random)
    {
        this.dungeonUUID = dungeonUUID;

        List<RoomEntry> entries = new ArrayList<>(Arrays.asList(ENTRIES));

        Collections.shuffle(entries,random);

        for (int i = 0; i < roomEntries.length; i++)
        {
            RoomEntry roomEntry = random.nextDouble() <= 0.6D ? entries.get(i) : RoomEntry.getRandomEntry(random);
            roomEntry.shuffle(random);
            this.roomEntries[i] = roomEntry;
        }
    }


    public void nextEntry(World world)
    {
        n++;
        if(n < 3)
            this.placeButtons(world);
        else
            this.completeRoom(world);

    }

    private RoomEntry t;

    @Override
    public boolean updateCheck(World world, List<PlayerEntity> players)
    {
        boolean update = super.updateCheck(world, players);

        final BlockPos pos = new BlockPos(getXWithOffset(7, 5), getYWithOffset(4), getZWithOffset(7, 5));


            System.out.println(this.roomEntries[n].correctItem);

        t = this.roomEntries[n];


        if(triggered)
        {
            if(timer < 70)
            {
                timer++;
                if(timer == 70)
                {
                    sendMessage(players, "dungeon.boss.item2");
                    placeButtons(world);
                }
                else if(timer == 45)
                {
                    sendMessage(players, "dungeon.boss.item3");
                }
                update = true;
            }
            else
            {
                updateItemPedestal(world, pos);
            }
        }

        return update;
    }

    @Override
    public String getBossPhrase()
    {
        return timer >= 70 ? "dungeon.boss.item4" : "dungeon.boss.item1";
    }

    @Override
    public void onPlayersEnterRoom(World world, List<PlayerEntity> players)
    {
        if(timer >= 70)
            this.placeButtons(world);
    }

    @Override
    public void onPlayersExitDungeon(World world)
    {
        destroyButton(world, 1);
        destroyButton(world, 3);
        destroyButton(world, 5);
        destroyButton(world, 9);
        destroyButton(world, 11);
        destroyButton(world, 13);

        world.getEntitiesWithinAABB(ItemFrameEntity.class, getRoomBox()).forEach(Entity::remove);
        this.n = 0;
    }

    @Override
    public boolean isRoomComplete(World world, List<PlayerEntity> players)
    {
        return completed;
    }

    public void updateItemPedestal(World world, BlockPos pos)
    {

        ItemStack stack = new ItemStack(this.roomEntries[n].correctItem);
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("isDungeonSilhouette", true);
        stack.setTag(tag);

        List<ItemFrameEntity> itemFrameItems = world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(pos));
        if(itemFrameItems.isEmpty())
        {
            world.setBlockState(pos.offset(direction), ModBlocks.DUNGEON_BRICKS.getDefaultState());

            ItemFrameEntity itemFrame = new ItemFrameEntity(world, pos, direction.getOpposite());
            itemFrame.setInvulnerable(true);
            itemFrame.setDisplayedItemWithUpdate(stack, false);
            world.addEntity(itemFrame);
        }
    }

    public void placeButtons(World world)
    {
        RoomEntry roomEntry = roomEntries[n];

        generateButton(world, 1, roomEntry.items.get(0), roomEntry.isCorrectButton(0));
        generateButton(world, 3, roomEntry.items.get(1), roomEntry.isCorrectButton(1));
        generateButton(world, 5, roomEntry.items.get(2), roomEntry.isCorrectButton(2));
        generateButton(world, 9, roomEntry.items.get(3), roomEntry.isCorrectButton(3));
        generateButton(world, 11, roomEntry.items.get(4), roomEntry.isCorrectButton(4));
        generateButton(world, 13, roomEntry.items.get(5), roomEntry.isCorrectButton(5));


        world.getEntitiesWithinAABB(ItemFrameEntity.class, getRoomBox()).forEach(Entity::remove);
    }

    private void destroyButton(IWorld world, int x)
    {
        BlockPos pos = new BlockPos(getXWithOffset(x, 5), getYWithOffset(2), getZWithOffset(x, 5));
        BlockPos posUp = pos.up();
        world.destroyBlock(pos, false);
        world.destroyBlock(posUp, false);


    }

    private static final BlockState SIGN = Blocks.OAK_WALL_SIGN.getDefaultState();

    private void generateButton(World world, int x, Item item, boolean correct)
    {
        Direction direction = Rotation.NONE.rotate(this.direction.getOpposite());

        BlockPos pos = new BlockPos(getXWithOffset(x, 5), getYWithOffset(2), getZWithOffset(x, 5));
        world.setBlockState(pos, BUTTON.with(HorizontalBlock.HORIZONTAL_FACING, direction), 2);
        world.playEvent(2001, pos, Block.getStateId(BUTTON));

        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof DungeonButtonTile)
        {
            ((DungeonButtonTile) tile).setCorrect(correct);
            ((DungeonButtonTile) tile).setDungeonUUID(dungeonUUID);
        }

        BlockPos signPos = pos.up();
        world.setBlockState(signPos, SIGN.with(WallSignBlock.FACING, direction), 2);
        world.playEvent(2001, signPos, Block.getStateId(SIGN));

        TileEntity signTile = world.getTileEntity(signPos);
        if(signTile instanceof SignTileEntity)
        {
            ((SignTileEntity) signTile).setText(0, item.getDisplayName(new ItemStack(item)));

            BlockState state = world.getBlockState(signPos);
            world.notifyBlockUpdate(signPos, state, state, 2);


        }


    }

    @Override
    public void writeToNBT(CompoundNBT tag)
    {
        super.writeToNBT(tag);
        tag.putUniqueId("dungeonUUID", this.dungeonUUID);
        tag.putInt("timer", this.timer);
        tag.putInt("round", this.n);

        ListNBT listNBT = new ListNBT();
        for (RoomEntry roomEntry : roomEntries)
        {
            CompoundNBT nbt = new CompoundNBT();
            roomEntry.writeToNBT(nbt);
            listNBT.add(nbt);
        }
        tag.put("roomEntries", listNBT);

    }

    @Override
    public void readNBT(CompoundNBT tag)
    {
        super.readNBT(tag);
        this.dungeonUUID = tag.getUniqueId("dungeonUUID");
        this.timer = tag.getInt("timer");
        this.n = tag.getInt("round");

        ListNBT listNBT = tag.getList("roomEntries", 10);
        for (int i = 0; i < listNBT.size(); i++)
        {
            CompoundNBT nbt = listNBT.getCompound(i);
            roomEntries[i] = RoomEntry.readFromNBT(nbt);
        }

    }

    public static class RoomEntry
    {

        private Item correctItem;
        private List<Item> items;


        public RoomEntry(Item... items)
        {
            this.correctItem = null;
            this.items = Arrays.asList(items.clone());
        }

        public RoomEntry(Item correctItem, List<Item> items)
        {
            this.correctItem = correctItem;
            this.items = items;
        }

        private static RoomEntry getRandomEntry(Random random)
        {
            Item[] items = new Item[6];

            List<Item> validItems = ForgeRegistries.ITEMS.getValues().stream().filter(item ->
            {
                ResourceLocation res = item.getRegistryName();
                return res != null && res.getNamespace().equals("minecraft") && !(item instanceof ShieldItem) && !(item instanceof BlockItem) && !(item instanceof SpawnEggItem);
            }).collect(Collectors.toList());

            for (int i = 0; i < items.length; i++)
                items[i] = validItems.get(random.nextInt(validItems.size()));

            return new RoomEntry(items);
        }


        public RoomEntry shuffle(Random rand)
        {
            correctItem = this.items.get(rand.nextInt(this.items.size()));

            return this;
        }

        private boolean isCorrectButton(int index)
        {
            return this.items.get(index).equals(correctItem);
        }

        public void writeToNBT(CompoundNBT tag)
        {
            tag.putInt("correctItem", Item.getIdFromItem(correctItem));
            ListNBT itemsTag = new ListNBT();
            items.forEach(item ->
            {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putInt("itemID", Item.getIdFromItem(item));
                itemsTag.add(nbt);
            });
            if(!itemsTag.isEmpty())
                tag.put("itemsTag", itemsTag);
        }

        public static RoomEntry readFromNBT(CompoundNBT tag)
        {
            Item correctItem = Item.getItemById(tag.getInt("correctItem"));

            List<Item> items = Lists.newArrayList();

            ListNBT itemsTag = tag.getList("itemsTag", 10);
            for (int i = 0; i < itemsTag.size(); i++)
            {
                CompoundNBT nbt = itemsTag.getCompound(i);
                items.add(Item.getItemById(nbt.getInt("itemID")));
            }
            return new RoomEntry(correctItem, items);
        }
    }
}
