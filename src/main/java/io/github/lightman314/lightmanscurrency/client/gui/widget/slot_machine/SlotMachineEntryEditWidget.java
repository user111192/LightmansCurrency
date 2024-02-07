package io.github.lightman314.lightmanscurrency.client.gui.widget.slot_machine;

import io.github.lightman314.lightmanscurrency.client.gui.easy.EasyScreenHelper;
import io.github.lightman314.lightmanscurrency.client.gui.easy.WidgetAddon;
import io.github.lightman314.lightmanscurrency.client.gui.easy.interfaces.ITooltipSource;
import io.github.lightman314.lightmanscurrency.api.misc.client.rendering.EasyGuiGraphics;
import io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.slot_machine.SlotMachineEntryClientTab;
import io.github.lightman314.lightmanscurrency.client.gui.widget.button.PlainButton;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyButton;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyWidgetWithChildren;
import io.github.lightman314.lightmanscurrency.client.util.IconAndButtonUtil;
import io.github.lightman314.lightmanscurrency.client.util.ScreenPosition;
import io.github.lightman314.lightmanscurrency.client.util.TextInputUtil;
import io.github.lightman314.lightmanscurrency.api.misc.EasyText;
import io.github.lightman314.lightmanscurrency.api.misc.IEasyTickable;
import io.github.lightman314.lightmanscurrency.common.menus.slots.easy.EasySlot;
import io.github.lightman314.lightmanscurrency.common.traders.permissions.Permissions;
import io.github.lightman314.lightmanscurrency.common.traders.slot_machine.SlotMachineTraderData;
import io.github.lightman314.lightmanscurrency.common.traders.slot_machine.SlotMachineEntry;
import io.github.lightman314.lightmanscurrency.util.InventoryUtil;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class SlotMachineEntryEditWidget extends EasyWidgetWithChildren implements IEasyTickable, ITooltipSource {

    public static final int WIDTH = 80;
    public static final int HEIGHT = 46;

    public final SlotMachineEntryClientTab tab;
    public final Supplier<Integer> entryIndex;

    private EditBox weightEdit;
    private PlainButton removeEntryButton;

    private int previousIndex = -1;

    private static final int ITEM_POSY = 22;

    public SlotMachineEntryEditWidget(ScreenPosition pos, SlotMachineEntryClientTab tab, Supplier<Integer> entryIndex) { this(pos.x, pos.y, tab, entryIndex); }
    public SlotMachineEntryEditWidget(int x, int y, SlotMachineEntryClientTab tab, Supplier<Integer> entryIndex) {
        super(x, y, WIDTH, HEIGHT);
        this.tab = tab;
        this.entryIndex = entryIndex;
    }

    @Override
    public SlotMachineEntryEditWidget withAddons(WidgetAddon... addons) { this.withAddonsInternal(addons); return this; }

    @Override
    public void addChildren() {
        this.weightEdit = this.addChild(new EditBox(this.tab.getFont(), this.getX() + this.tab.getFont().width(EasyText.translatable("gui.lightmanscurrency.trader.slot_machine.weight_label")), this.getY() + 10, 36, 10, EasyText.empty()));
        this.weightEdit.setMaxLength(4);
        this.removeEntryButton = this.addChild(IconAndButtonUtil.minusButton(this.getX(), this.getY(), this::Remove));
    }

    private SlotMachineEntry getEntry() { return this.tab.getEntry(this.entryIndex.get()); }

    private void Remove(EasyButton button) { this.tab.commonTab.RemoveEntry(this.entryIndex.get()); }

    @Override
    public void renderWidget(@Nonnull EasyGuiGraphics gui) {

        SlotMachineEntry entry = this.getEntry();
        if(entry != null)
        {
            //Draw label
            gui.drawString(EasyText.translatable("gui.lightmanscurrency.trader.slot_machine.entry_label", this.entryIndex.get() + 1), 12, 0, 0x404040);
            //Draw Weight label
            gui.drawString(EasyText.translatable("gui.lightmanscurrency.trader.slot_machine.weight_label"), 0, 12, 0x404040);
            //Render Items
            for(int i = 0; i < SlotMachineEntry.ITEM_LIMIT; ++i)
            {
                if(i < entry.items.size() && !entry.items.get(i).isEmpty())
                    gui.renderItem(entry.items.get(i), 18 * i, ITEM_POSY);
                else
                    gui.renderSlotBackground(EasySlot.BACKGROUND, 18 * i, ITEM_POSY);
            }
        }

    }

    @Override
    protected boolean isValidClickButton(int button) { return button == 0 || button == 1; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //Confirm that the mouse is in the general area
        if(this.clicked(mouseX, mouseY) && this.isValidClickButton(button))
        {
            boolean rightClick = button == 1;
            SlotMachineEntry entry = this.getEntry();
            if(entry != null)
            {
                int entryIndex = this.entryIndex.get();
                ItemStack heldItem = this.tab.menu.getHeldItem();
                if(mouseY >= this.getY() + ITEM_POSY && mouseY < this.getY() + ITEM_POSY + 16)
                {
                    int itemIndex = getItemSlotIndex(mouseX);
                    if(itemIndex >= 0)
                    {
                        if(itemIndex >= entry.items.size())
                        {
                            if(!heldItem.isEmpty())
                            {
                                if(rightClick) //If right-click, set as 1
                                {
                                    ItemStack copy = heldItem.copy();
                                    copy.setCount(1);
                                    this.tab.commonTab.AddEntryItem(entryIndex, copy);
                                }
                                else //Otherwise add whole stack
                                    this.tab.commonTab.AddEntryItem(entryIndex, heldItem);
                                return true;
                            }
                        }
                        else if(heldItem.isEmpty())
                        {
                            if(rightClick) //If right-click, reduce by 1
                            {
                                ItemStack newStack = entry.items.get(itemIndex).copy();
                                newStack.shrink(1);
                                if(newStack.isEmpty())
                                    this.tab.commonTab.RemoveEntryItem(entryIndex, itemIndex);
                                else
                                    this.tab.commonTab.EditEntryItem(entryIndex, itemIndex, newStack);
                            }
                            else //If left-click, remove entirely
                                this.tab.commonTab.RemoveEntryItem(entryIndex, itemIndex);
                            return true;
                        }
                        else {
                            if(rightClick) //If right-click, either set as 1 or increase by 1
                            {
                                ItemStack oldStack = entry.items.get(itemIndex);
                                if(InventoryUtil.ItemMatches(heldItem, oldStack))
                                {
                                    ItemStack newStack = entry.items.get(itemIndex).copy();
                                    if(newStack.getCount() >= newStack.getMaxStackSize())
                                        return false;
                                    newStack.grow(1);
                                    this.tab.commonTab.EditEntryItem(entryIndex, itemIndex, newStack);
                                }
                                else
                                {
                                    ItemStack copy = heldItem.copy();
                                    copy.setCount(1);
                                    this.tab.commonTab.EditEntryItem(entryIndex, itemIndex, copy);
                                }
                                return true;
                            }
                            else //Replace with new held item
                                this.tab.commonTab.EditEntryItem(entryIndex, itemIndex, heldItem);
                        }
                    }
                }
            }
        }
        return false;
    }

    private int getItemSlotIndex(double mouseX)
    {
        int x = (int)mouseX - this.getX();
        if(x < 0)
            return -1;
        int result = x / 18;
        return result >= SlotMachineEntry.ITEM_LIMIT ? -1 : result;
    }

    @Override
    public void tick()
    {
        SlotMachineEntry entry = this.getEntry();
        if(entry != null && this.tab.menu.getTrader() instanceof SlotMachineTraderData trader)
        {
            this.weightEdit.visible = true;
            boolean hasPerms = this.tab.menu.hasPermission(Permissions.EDIT_TRADES);
            this.removeEntryButton.visible = hasPerms;
            this.weightEdit.setEditable(hasPerms);
            if(trader.areEntriesChanged())
            {
                this.weightEdit.setValue(Integer.toString(entry.getWeight()));
                return;
            }

            int thisIndex = this.entryIndex.get();
            if(thisIndex != this.previousIndex)
                this.weightEdit.setValue(Integer.toString(entry.getWeight()));
            int newWeight = TextInputUtil.getIntegerValue(this.weightEdit, 1);
            if(newWeight != entry.getWeight())
                this.tab.commonTab.ChangeEntryWeight(thisIndex, newWeight);
            this.previousIndex = thisIndex;
        }
        else
            this.weightEdit.visible = this.removeEntryButton.visible = false;

        TextInputUtil.whitelistInteger(this.weightEdit, 1, 1000);
    }

    @Override
    public List<Component> getTooltipText(int mouseX, int mouseY) {
        SlotMachineEntry entry = this.getEntry();
        if(entry != null)
        {
            if(mouseY >= this.getY() + ITEM_POSY && mouseY < this.getY() + ITEM_POSY + 16)
            {
                int itemIndex = this.getItemSlotIndex(mouseX);
                if(itemIndex >= 0 && itemIndex < entry.items.size())
                {
                    ItemStack item = entry.items.get(itemIndex);
                    if(!item.isEmpty())
                        return EasyScreenHelper.getTooltipFromItem(item);
                }
            }
        }
        return null;
    }
}
