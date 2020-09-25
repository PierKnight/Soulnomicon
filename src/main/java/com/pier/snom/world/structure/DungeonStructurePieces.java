package com.pier.snom.world.structure;

import com.google.common.collect.Lists;
import com.pier.snom.init.ModBlocks;
import com.pier.snom.tile.StartPedestalTile;
import com.pier.snom.world.structure.sections.*;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class DungeonStructurePieces
{

    private static final BlockState BRICKS = ModBlocks.DUNGEON_BRICKS.getDefaultState();
    private static final BlockState CHISELED_BRICKS = ModBlocks.CHISELED_DUNGEON_BRICKS.getDefaultState();
    private static final BlockState IRON_BARS = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.TRUE).with(PaneBlock.SOUTH, Boolean.TRUE);

    private static final List<Class<? extends Room>> rooms = Lists.newArrayList(BlazeRoom.class,IronGolemRoom.class,ItemQuestionRoom.class,NoteblockRoom.class,SpiderRoom.class,TNTRoom.class);

    @Nullable
    private static Room getNextRoom(List<StructurePiece> listIn,Random random,Direction direction,int x,int z)
    {
        List<Room> roomsList = rooms.stream().filter(room -> listIn.stream().noneMatch(piece -> room.equals(piece.getClass()))).map(room ->
        {
            try
            {
                return room.getConstructor(Direction.class,int.class,int.class).newInstance(direction,x,z);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());


        return roomsList.isEmpty() ? null : roomsList.get(random.nextInt(roomsList.size()));


    }


    public static class Start extends Room
    {


        public BlockPos dungeonStartPos = null;
        public List<RoomSection> roomSections = Lists.newArrayList();
        public MutableBoundingBox dungeonBoundingBox;


        protected Start(Random rand, int xIn, int zIn)
        {
            super(ModStructures.DUN_START, Direction.Plane.HORIZONTAL.random(rand), xIn, 0, zIn, 15, 10, 15);
            this.dungeonUUID = UUID.randomUUID();
        }

        protected Start(TemplateManager templateManager, CompoundNBT compound)
        {
            super(ModStructures.DUN_START, compound);

            ListNBT dungeonRoomsList = compound.getList("dungeonRoomsList", 10);
            for (int i = 0; i < dungeonRoomsList.size(); i++)
            {
                CompoundNBT tagToRead = dungeonRoomsList.getCompound(i);
                this.roomSections.add(RoomSection.readFromNBT(tagToRead));
            }

            this.dungeonStartPos = new BlockPos(compound.getInt("startX"), compound.getInt("startY"), compound.getInt("startZ"));
            this.dungeonBoundingBox = new MutableBoundingBox(compound.getIntArray("dungeonBox"));
        }

        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn)
        {

            int height = getMaxHeight(worldIn);
            this.boundingBox.offset(0, height, 0);

            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 14, 0, 14, BRICKS, BRICKS, false);
            this.fillWithAir(worldIn, mutableBoundingBoxIn, 0, 1, 0, 14, 9, 14);

            int pedestalX = getXWithOffset(7, 7);
            int pedestalY = getYWithOffset(1);
            int pedestalZ = getZWithOffset(7, 7);
            BlockPos pedestalPos = new BlockPos(pedestalX, pedestalY, pedestalZ);

            if(mutableBoundingBoxIn.isVecInside(pedestalPos))
            {
                worldIn.setBlockState(pedestalPos, ModBlocks.DUNGEON_START_PEDESTAL.getDefaultState(), 2);
                TileEntity tileEntity = worldIn.getTileEntity(pedestalPos);
                if(tileEntity instanceof StartPedestalTile)
                {
                    StartPedestalTile pedestalTile = (StartPedestalTile) tileEntity;
                    pedestalTile.setDungeonUUID(dungeonUUID);
                }
            }
            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT compound)
        {
            super.readAdditional(compound);

            ListNBT roomsNBT = new ListNBT();
            roomSections.forEach(roomSection ->
            {
                CompoundNBT tagToSave = new CompoundNBT();
                roomSection.writeToNBT(tagToSave);
                roomsNBT.add(tagToSave);

            });
            if(!roomsNBT.isEmpty())
                compound.put("dungeonRoomsList", roomsNBT);

            compound.putInt("startX", dungeonStartPos.getX());
            compound.putInt("startY", dungeonStartPos.getY());
            compound.putInt("startZ", dungeonStartPos.getZ());

            compound.put("dungeonBox", this.dungeonBoundingBox.toNBTTagIntArray());

        }

        @Override
        public void buildComponent(@Nonnull StructurePiece componentIn, @Nonnull List<StructurePiece> listIn, @Nonnull Random rand)
        {
            FirstRoom room = new FirstRoom(getCoordBaseMode(), this.boundingBox.minX, this.boundingBox.minZ);
            room.setDungeonUUID(dungeonUUID);
            listIn.add(room);
            room.buildComponent(componentIn, listIn, rand);

        }

        @Override
        public RoomSection getRoomSection(Random rand)
        {
            return null;
        }


        private int getMaxHeight(IWorld world)
        {
            int max = world.getSeaLevel();

            for (int x = 0; x < this.width; x++)
                for (int z = 0; z < this.depth; z++)
                {
                    int xOffset = getXWithOffset(x, z);
                    int zOffset = getZWithOffset(x, z);
                    int height = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, xOffset, zOffset);
                    if(height > max)
                        max = height;
                }
            return max;
        }


        public void setDungeonBoundingBox(MutableBoundingBox dungeonBoundingBox)
        {
            this.dungeonBoundingBox = dungeonBoundingBox;
        }
    }

    public static abstract class Room extends StructurePiece
    {

        protected UUID dungeonUUID;
        protected final int width;
        protected final int height;
        protected final int depth;


        public Room(IStructurePieceType structurePieceTypeIn, Direction direction, int xIn, int yIn, int zIn, int widthIn, int heightIn, int depthIn)
        {
            super(structurePieceTypeIn, 0);
            this.width = widthIn;
            this.height = heightIn;
            this.depth = depthIn;
            this.setCoordBaseMode(direction);

            if(this.getCoordBaseMode().getAxis() == Direction.Axis.Z)
            {
                this.boundingBox = new MutableBoundingBox(xIn, yIn, zIn, xIn + widthIn - 1, yIn + heightIn - 1, zIn + depthIn - 1);
            }
            else
            {
                this.boundingBox = new MutableBoundingBox(xIn, yIn, zIn, xIn + depthIn - 1, yIn + heightIn - 1, zIn + widthIn - 1);
            }

        }

        public Room(IStructurePieceType structurePieceTypeIn, CompoundNBT nbt)
        {
            super(structurePieceTypeIn, nbt);
            this.width = nbt.getInt("Width");
            this.height = nbt.getInt("Height");
            this.depth = nbt.getInt("Depth");
            this.dungeonUUID = nbt.getUniqueId("dungeonUUID");
        }


        protected void readAdditional(CompoundNBT tagCompound)
        {
            tagCompound.putInt("Width", this.width);
            tagCompound.putInt("Height", this.height);
            tagCompound.putInt("Depth", this.depth);
            tagCompound.putUniqueId("dungeonUUID", this.dungeonUUID);
        }


        @Override
        public void buildComponent(@Nonnull StructurePiece componentIn, @Nonnull List<StructurePiece> listIn, @Nonnull Random rand)
        {


            Direction direction = getCoordBaseMode();

            Start start = (Start) componentIn;

            if(this instanceof FirstRoom)
            {
                start.dungeonStartPos = new BlockPos(getXWithOffset(4, 4), getYWithOffset(1), getZWithOffset(4, 4));
            }
            else
            {
                RoomSection roomSection = getRoomSection(rand);
                if(roomSection != null)
                {
                    roomSection.setDoorYOffset(doorOffsetY());
                    start.roomSections.add(roomSection);
                }

            }

            if(listIn.size() - 3 < rooms.size() && direction != null)
            {

                Room room = getNextRoom(listIn,rand,direction, this.boundingBox.minX, this.boundingBox.minZ);

                if(room == null)
                    return;

                room.setDungeonUUID(this.dungeonUUID);

                Direction d = direction == Direction.NORTH || direction == Direction.EAST ? direction.rotateYCCW() : direction.rotateY();

                int offset = (room.width - this.width) / 2;

                int width = room.width;
                int depth = room.depth;

                if(direction == Direction.SOUTH)
                {
                    width = this.width;
                    depth = this.depth;
                }
                else if(direction == Direction.EAST)
                {
                    width = this.depth;
                    depth = this.width;
                }
                else if(direction == Direction.WEST)
                {
                    width = room.depth;
                    depth = room.width;
                }

                int offsetX = d.getXOffset() * offset;
                int offsetZ = d.getZOffset() * offset;

                room.boundingBox.offset(offsetX + direction.getXOffset() * width, 0, offsetZ + direction.getZOffset() * depth);

                listIn.add(room);
                room.buildComponent(componentIn, listIn, rand);
            }



        }

        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn)
        {
            int width = this.width - 1;
            int height = this.height - 1;
            int depth = this.depth - 1;

            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, width, 0, depth, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, height, 0, width, height, depth, BRICKS, BRICKS, false);

            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, width, height, 0, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, depth, width, height, depth, BRICKS, BRICKS, false);

            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 0, height, depth, false, randomIn, new WallSelector());
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, width, 0, 0, width, height, depth, false, randomIn, new WallSelector());

            this.fillWithAir(worldIn, mutableBoundingBoxIn, 1, 1, 1, width - 1, height - 1, depth - 1);

            int center = MathHelper.floor(width / 2F);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, center - 1, 1 + doorOffsetY(), depth, center + 1, 3 + doorOffsetY(), depth, IRON_BARS, IRON_BARS, false);
            this.fillWithAir(worldIn, mutableBoundingBoxIn, center - 1, 1 + doorOffsetY(), 0, center + 1, 3 + doorOffsetY(), 0);

            for (int x = 1; x < width - 1; x++)
                for (int z = 1; z < depth - 1; z++)
                    if(x % 3 == 0 && z % 3 == 0)
                        createLight(worldIn, mutableBoundingBoxIn, x, height - 1, z);


            return true;
        }

        protected void createLight(IWorld world, MutableBoundingBox mutableBoundingBox, int x, int y, int z)
        {
            for (int i = 0; i < 2; i++)
                this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x, y - i, z, mutableBoundingBox);
            this.setBlockState(world, Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true), x, y - 2, z, mutableBoundingBox);
        }

        public void setDungeonUUID(UUID dungeonUUID)
        {
            this.dungeonUUID = dungeonUUID;
        }

        public int doorOffsetY()
        {
            return 0;
        }

        public abstract RoomSection getRoomSection(Random rand);

        private static class WallSelector extends BlockSelector
        {

            @Override
            public void selectBlocks(Random rand, int x, int y, int z, boolean wall)
            {
                if(x % 2 == 0 && z % 2 == 0 && y % 3 == 0)
                    blockstate = CHISELED_BRICKS;
                else
                    blockstate = BRICKS;
            }
        }

    }

    public static class FirstRoom extends Room
    {

        protected FirstRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_FIRST_ROOM, direction, xIn, 30, zIn, 9, 9, 9);
        }

        public FirstRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_FIRST_ROOM, nbt);
        }


        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn)
        {

            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 8, 0, 8, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 8, 0, 8, 8, 8, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 8, 8, 0, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 0, 0, 8, 8, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 0, 0, 8, 8, 8, 8, BRICKS, BRICKS, false);
            this.fillWithBlocks(worldIn, mutableBoundingBoxIn, 8, 0, 0, 8, 8, 8, BRICKS, BRICKS, false);
            this.fillWithAir(worldIn, mutableBoundingBoxIn, 1, 1, 1, 7, 7, 7);
            this.fillWithAir(worldIn, mutableBoundingBoxIn, 3, 1, 8, 5, 3, 8);


            fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 1, 1, 7, 7, 7, false, randomIn, new VinesSelector());
            this.setBlockState(worldIn, Blocks.LANTERN.getDefaultState(), 2, 2, 2, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.LANTERN.getDefaultState(), 6, 2, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.LANTERN.getDefaultState(), 6, 2, 2, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.LANTERN.getDefaultState(), 2, 2, 6, mutableBoundingBoxIn);

            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 2, 1, 2, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 1, 6, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 1, 2, mutableBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 2, 1, 6, mutableBoundingBoxIn);

            return true;
        }

        @Override
        protected void createLight(IWorld world, MutableBoundingBox mutableBoundingBox, int x, int y, int z)
        {
        }

        @Override
        public RoomSection getRoomSection(Random rand)
        {
            return null;
        }

        private static class VinesSelector extends BlockSelector
        {
            private static final Vec3d center = new Vec3d(4, 4, 4);;

            @Override
            public void selectBlocks(@Nonnull Random rand, int x, int y, int z, boolean wall)
            {
                if((x == 1 || y == 1 || z == 1 || x == 7 || y == 7 || z == 7) && rand.nextBoolean())
                {
                    Vec3d vec = center.subtract(x, y, z);
                    Direction direction = Direction.getFacingFromVector(vec.x, vec.y, vec.z).getOpposite();
                    if(direction == Direction.DOWN)
                        direction = Direction.UP;
                    this.blockstate = Blocks.VINE.getDefaultState().with(VineBlock.getPropertyFor(direction), true);
                }
            }
        }
    }


    public static class TNTRoom extends Room
    {

        public TNTRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_TNT_ROOM, direction, xIn, 30, zIn, 15, 10, 15);
        }

        public TNTRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_TNT_ROOM, nbt);
        }


        @Override
        public RoomSection getRoomSection(Random rand)
        {
            return new TNTRoomSection(getCoordBaseMode(), getBoundingBox());
        }
    }

    public static class SpiderRoom extends Room
    {


        public SpiderRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_SPIDER_ROOM, direction, xIn, 30, zIn, 19, 10, 19);
        }

        public SpiderRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_SPIDER_ROOM, nbt);
        }


        @Override
        public RoomSection getRoomSection(Random rand)
        {
            return new SpiderRoomSection(getCoordBaseMode(), getBoundingBox());
        }

        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn)
        {

            super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
            this.fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 1, 1, 17, 1, 17, false, randomIn, new WebSelector());
            return true;
        }

        @Override
        protected void createLight(IWorld world, MutableBoundingBox mutableBoundingBox, int x, int y, int z)
        {
        }

        private static class WebSelector extends BlockSelector
        {

            @Override
            public void selectBlocks(Random rand, int x, int y, int z, boolean wall)
            {
                if(rand.nextDouble() <= 0.3D)
                    blockstate = Blocks.COBWEB.getDefaultState();
                else
                    blockstate = Blocks.AIR.getDefaultState();
            }
        }
    }

    public static class IronGolemRoom extends Room
    {

        public IronGolemRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_IRON_GOLEM_ROOM, direction, xIn, 30, zIn, 19, 12, 25);
        }

        public IronGolemRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_IRON_GOLEM_ROOM, nbt);
        }

        @Override
        public RoomSection getRoomSection(Random rand)
        {
            return new IronGolemSection(getCoordBaseMode(), getBoundingBox());
        }
    }

    public static class BlazeRoom extends Room
    {
        public BlazeRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_BLAZE_ROOM, direction, xIn, 29, zIn, 17, 11, 17);
        }

        public BlazeRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_BLAZE_ROOM, nbt);
        }


        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn)
        {

            super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);

            setBlockState(worldIn, Blocks.LAVA.getDefaultState(), 1, 9, 1, mutableBoundingBoxIn);
            setBlockState(worldIn, Blocks.LAVA.getDefaultState(), 15, 9, 15, mutableBoundingBoxIn);
            setBlockState(worldIn, Blocks.LAVA.getDefaultState(), 15, 9, 1, mutableBoundingBoxIn);
            setBlockState(worldIn, Blocks.LAVA.getDefaultState(), 1, 9, 15, mutableBoundingBoxIn);

            fillWithRandomizedBlocks(worldIn, mutableBoundingBoxIn, 1, 1, 1, 15, 1, 15, false, randomIn, new MagmaBlockSelector());


            return true;
        }

        @Override
        protected void createLight(IWorld world, MutableBoundingBox mutableBoundingBox, int x, int y, int z)
        {
        }

        @Override
        public int doorOffsetY()
        {
            return 1;
        }

        @Override
        public RoomSection getRoomSection(Random rand)
        {
            return new BlazeSection(getCoordBaseMode(), getBoundingBox());
        }

        private static class MagmaBlockSelector extends BlockSelector
        {

            @Override
            public void selectBlocks(Random rand, int x, int y, int z, boolean wall)
            {
                if(rand.nextDouble() <= 0.35D)
                    this.blockstate = Blocks.MAGMA_BLOCK.getDefaultState();
                else if(rand.nextDouble() <= 0.15D)
                    this.blockstate = Blocks.LAVA.getDefaultState();
                else
                    this.blockstate = BRICKS;

            }
        }
    }

    public static class ItemQuestionRoom extends Room
    {

        public ItemQuestionRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_ITEM_ROOM, direction, xIn, 30, zIn, 15, 11, 7);
        }

        public ItemQuestionRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_ITEM_ROOM, nbt);
        }


        @Override
        public RoomSection getRoomSection(Random rand)
        {
            ItemQuestionSection section = new ItemQuestionSection(getCoordBaseMode(), getBoundingBox());
            section.init(this.dungeonUUID,rand);
            return section;
        }

    }

    public static class NoteblockRoom extends Room
    {

        public NoteblockRoom(Direction direction, int xIn, int zIn)
        {
            super(ModStructures.DUN_NOTEBLOCK_ROOM, direction, xIn, 30, zIn, 15, 11, 10);
        }

        public NoteblockRoom(TemplateManager templateManager, CompoundNBT nbt)
        {
            super(ModStructures.DUN_NOTEBLOCK_ROOM, nbt);
        }


        @Override
        public RoomSection getRoomSection(Random rand)
        {
            NoteBlockSection section = new NoteBlockSection(getCoordBaseMode(), getBoundingBox());
            section.init(this.dungeonUUID,rand);
            return section;
        }

    }

}
