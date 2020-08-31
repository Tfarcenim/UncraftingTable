package tfar.uncraftingtable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class UncraftingTableMenu extends Container {

	public final IntReferenceHolder intReferenceHolder;
	public final IWorldPosCallable callable;

	protected UncraftingTableMenu(int id, PlayerInventory inventory) {
		this(id, inventory, new ItemStackHandler(10), IntReferenceHolder.single(), IWorldPosCallable.DUMMY);
	}

	public UncraftingTableMenu(int id, PlayerInventory inventory, ItemStackHandler handler, IntReferenceHolder intReferenceHolder, IWorldPosCallable callable) {
		super(ExampleMod.menu, id);
		this.intReferenceHolder = intReferenceHolder;
		this.callable = callable;
		//input
		this.addSlot(new SlotItemHandler(handler, 0, 37, 35));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlot(new SlotItemHandler(handler, j + i * 3 + 1, 94 + j * 18, 17 + i * 18){
					@Override
					public boolean canTakeStack(PlayerEntity playerIn) {
						return false;
					}
				});
			}
		}

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
		}
		trackInt(intReferenceHolder);
	}

	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index > 0 && index < 10) {
				if (!this.mergeItemStack(itemstack1, 10, 38, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else if (index == 0) {
				if (!this.mergeItemStack(itemstack1, 10, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index < 29) {
				if (!this.mergeItemStack(itemstack1, 0, 1, false) &&
								!this.mergeItemStack(itemstack1, 29, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 1, false) &&
							index < 38 && !this.mergeItemStack(itemstack1, 10, 29, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			}
			slot.onSlotChanged();
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, itemstack1);
			this.detectAndSendChanges();
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}
