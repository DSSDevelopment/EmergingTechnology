package io.moonman.emergingtechnology.machines.wind;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WindContainer extends Container {
    private final WindTileEntity tileEntity;

    public WindContainer(InventoryPlayer player, WindTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        IItemHandler handler = tileEntity.itemHandler;
        this.addSlotToContainer(new SlotItemHandler(handler, 0, 17, 35));

        // Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Hotbar
        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

        ItemStack stack = ItemStack.EMPTY;
        Slot fromSlot = (Slot) this.inventorySlots.get(index);

        if (fromSlot != null && fromSlot.getHasStack()) {
            ItemStack fromStack = fromSlot.getStack();
            stack = fromStack.copy();

            // If it's from the wind generator, put in player's inventory
            if (index < 1) {
                if (!this.mergeItemStack(fromStack, 1, 37, false)) {
                    return ItemStack.EMPTY;
                } else {
                    fromSlot.onSlotChanged();
                }
            } else {// Otherwise try to put it in input slot
                if (!this.mergeItemStack(fromStack, 0, 0, false)) {
                    return ItemStack.EMPTY;
                } else {
                    fromSlot.onSlotChanged();
                }
            }

            fromSlot.onTake(playerIn, fromStack);
        }
        return stack;
    }
}
