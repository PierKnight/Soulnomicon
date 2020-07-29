package com.pier.snom.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pier.snom.SoulnomiconMain;
import com.pier.snom.capability.SoulPlayerProvider;
import com.pier.snom.init.ModSounds;
import com.pier.snom.network.PacketManager;
import com.pier.snom.network.server.PacketStartSearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ClairvoyanceScreen extends ContainerScreen<ClairvoyanceScreen.ClairvoyanceContainer>
{

    private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(SoulnomiconMain.ID, "textures/gui/clairvoyance_gui.png");

    private static final Inventory TMP_INVENTORY = new Inventory(45);
    private TextFieldWidget searchField;
    private CheckBox checkboxButton;


    private int currentIndex = 0;
    private final PlayerEntity player;

    public ClairvoyanceScreen(PlayerEntity player, NonNullList<ItemStack> knownItems)
    {
        super(new ClairvoyanceContainer(player, knownItems), null, new StringTextComponent(""));
        this.xSize = 178;
        this.ySize = 127;
        this.passEvents = true;
        this.player = player;

    }


    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        ItemStack stack = this.hoveredSlot != null ? this.hoveredSlot.getStack() : ItemStack.EMPTY;

        this.checkboxButton.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);


        if(!stack.isEmpty() && p_mouseClicked_5_ == 0)
        {
            player.swingArm(Hand.MAIN_HAND);
            player.getCapability(SoulPlayerProvider.SOUL_PLAYER_CAPABILITY).ifPresent(iSoulPlayer -> iSoulPlayer.getAbilitiesManager().getClairvoyanceAbility().startSearch(stack, checkboxButton.isChecked()));
            minecraft.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), ModSounds.CLAIRVOYANCE_START, SoundCategory.PLAYERS, 1.2F, 1F);
            PacketManager.channel.sendToServer(new PacketStartSearch(stack, checkboxButton.isChecked()));
            onClose();
            return true;
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    protected void init()
    {
        super.init();
        this.searchField = new TextFieldWidget(this.font, this.guiLeft + 55, this.guiTop + 9, 80, 9, I18n.format("itemGroup.search"));
        this.searchField.setMaxStringLength(50);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setTextColor(16777215);
        this.searchField.setVisible(true);
        this.searchField.setFocused2(true);
        this.children.add(this.searchField);

        this.checkboxButton = new CheckBox(this.guiLeft + 8, this.guiTop + 4, 20, 20, this);
        this.buttons.add(this.checkboxButton);

        this.updateItems();
    }

    public void tick()
    {
        super.tick();
        if(this.searchField != null)
            this.searchField.tick();

    }

    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_)
    {
        String s = this.searchField.getText();
        boolean checked = this.checkboxButton.isChecked();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.searchField.setText(s);
        if(!checked)
            this.checkboxButton.onPress();
        this.updateItems();


    }

    @Nonnull
    @Override
    public List<String> getTooltipFromItem(@Nonnull ItemStack itemStack)
    {
        List<String> list = super.getTooltipFromItem(itemStack);
        list.add(SoulnomiconMain.getFormattedText("clairvoyance.click.item"));
        return list;
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_)
    {
        String s = this.searchField.getText();
        if(this.searchField.charTyped(p_charTyped_1_, p_charTyped_2_))
        {
            if(!Objects.equals(s, this.searchField.getText()))
                this.updateItems();
            return false;
        }
        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        String s = this.searchField.getText();

        if(this.searchField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_))
        {
            if(!Objects.equals(s, this.searchField.getText()))
            {
                this.updateItems();
            }
            return false;
        }
        else
        {
            return this.searchField.isFocused() && this.searchField.getVisible() && p_keyPressed_1_ != 256 || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);


    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        this.minecraft.getTextureManager().bindTexture(TEXTURE_GUI);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, 0.5F);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        this.searchField.render(mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();

    }

    private boolean containsItem(ItemStack stack)
    {
        for (ItemStack s : this.container.listItems)
            if(ItemStack.areItemsEqual(s, stack))
                return true;
        return false;
    }

    private void updateItems()
    {
        this.container.listItems.clear();

        String filter = this.searchField.getText();
        for (ItemStack stack : this.container.knownItems)
        {
            ItemStack s = stack.copy();
            if(this.checkboxButton.isChecked())
            {
                s.setTag(null);
                if(containsItem(s))
                    continue;
            }


            if(!filter.isEmpty())
            {
                String search = filter.toLowerCase(Locale.ROOT);

                if(search.startsWith("@") && search.length() > 1)
                {

                    String modId = s.getItem().getRegistryName().getNamespace();
                    if(modId.contains(search.substring(1)))
                        this.container.listItems.add(s);

                }
                else
                    for (ITextComponent line : s.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL))
                    {
                        String l = TextFormatting.getTextWithoutFormattingCodes(line.getString()).toLowerCase(Locale.ROOT);
                        if(l.contains(search))
                        {
                            this.container.listItems.add(s);
                            break;
                        }
                    }
            }
            else
                this.container.listItems.add(s);

        }

        container.scrollTo(0);

    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double scroll)
    {
        if(canScroll())
        {
            int line = this.currentIndex + (int) -scroll;

            int maxLine = this.container.listItems.size() / 9;
            if(maxLine > 0)
                line = MathHelper.clamp(line, 0, maxLine);

            this.container.scrollTo(line);
            this.currentIndex = line;

        }

        return false;
    }

    private boolean canScroll()
    {
        return this.container.knownItems.size() > 45;
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClairvoyanceContainer extends Container
    {

        private final NonNullList<ItemStack> knownItems;
        private final NonNullList<ItemStack> listItems = NonNullList.create();

        protected ClairvoyanceContainer(PlayerEntity player, NonNullList<ItemStack> knownItems)
        {
            super(null, 0);

            PlayerInventory inventory = player.inventory;
            this.knownItems = knownItems;
            this.listItems.addAll(knownItems);

            for (int i = 0; i < 5; ++i)
            {
                for (int j = 0; j < 9; ++j)
                {
                    this.addSlot(new Slot(ClairvoyanceScreen.TMP_INVENTORY, i * 9 + j, 9 + j * 18, 26 + i * 18));
                }
            }
            scrollTo(0);
        }

        public void scrollTo(int line)
        {

            for (int i = 0; i < 5; ++i)
            {
                for (int j = 0; j < 9; ++j)
                {
                    int indexSlot = i * 9 + j;
                    int itemIndex = (i + line) * 9 + j;

                    if(itemIndex < this.listItems.size())
                        TMP_INVENTORY.setInventorySlotContents(indexSlot, this.listItems.get(itemIndex));
                    else
                        TMP_INVENTORY.setInventorySlotContents(indexSlot, ItemStack.EMPTY);
                }
            }
        }

        @Override
        public boolean canInteractWith(@Nonnull PlayerEntity playerIn)
        {
            return true;
        }


    }

    private static class CheckBox extends CheckboxButton
    {
        private final ClairvoyanceScreen screen;

        public CheckBox(int x, int y, int width, int height, ClairvoyanceScreen screen)
        {
            super(x, y, width, height, "", false);
            this.screen = screen;
            this.onPress();
        }

        @Override
        public void render(int x, int y, float p_render_3_)
        {
            super.render(x, y, p_render_3_);
            if(isHovered)
                screen.renderTooltip(SoulnomiconMain.getFormattedText("clairvoyance.filter"), x, y);
        }

        @Override
        public void onClick(double p_onClick_1_, double p_onClick_3_)
        {
            super.onClick(p_onClick_1_, p_onClick_3_);
            screen.updateItems();
        }
    }

}
