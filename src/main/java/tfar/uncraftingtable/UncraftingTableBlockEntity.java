package tfar.uncraftingtable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

public class UncraftingTableBlockEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

	public final IntReferenceHolder recipeCountHolder = new IntReferenceHolder() {
		@Override
		public int get() {
			return conflicts.size();
		}

		@Override
		public void set(int p_221494_1_) {
		}
	};


	public ItemStackHandler handler = new ItemStackHandler(10) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (slot == INPUT) {
				inputChanged = true;
			}
			markDirty();
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (!getBlockState().get(UncraftingTableBlock.LOCKED) && slot != INPUT) {
				return ItemStack.EMPTY;
			}
			return super.extractItem(slot, amount, simulate);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (slot != INPUT) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};

	public LazyOptional<IItemHandler> externalHandler = LazyOptional.of(() -> new ExternalIItemHandler(handler));

	public static class ExternalIItemHandler implements IItemHandler {

		final ItemStackHandler handler;

		public ExternalIItemHandler(ItemStackHandler handler) {
			this.handler = handler;
		}

		@Override
		public int getSlots() {
			return handler.getSlots();
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot) {
			return handler.getStackInSlot(slot);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (slot == INPUT) {
				return handler.insertItem(slot, stack, simulate);
			} else {
				return stack;
			}
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == INPUT) {
				return ItemStack.EMPTY;
			}
			return handler.extractItem(slot,amount,simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return handler.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return handler.isItemValid(slot,stack);
		}
	}

	public boolean inputChanged = true;

	public boolean lockedInput;

	public IRecipe<?> selectedRecipe;

	public List<IRecipe<?>> conflicts = new ArrayList<>();

	int index = 0;

	public static final int INPUT = 0;

	public UncraftingTableBlockEntity() {
		super(ExampleMod.blockEntity);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			if (inputChanged && !lockedInput) {
					if (!getBlockState().get(UncraftingTableBlock.LOCKED)) {
						scan();
						fillOutput();
					}
				}
				inputChanged = false;
		}
		if (canUncraft()) {
			uncraft();
		}
		if (isOutputEmpty() && lockedInput) {
			lockedInput = false;
		}
	}

	public boolean canUncraft() {
		return selectedRecipe != null && getBlockState().get(UncraftingTableBlock.LOCKED) && !lockedInput && UncraftingManager.test(handler.getStackInSlot(INPUT),selectedRecipe);
	}

	public boolean isOutputEmpty() {
		return IntStream.range(1,10).mapToObj(handler::getStackInSlot).allMatch(ItemStack::isEmpty);
	}

	public void clear() {
		for (int i = 1 ; i < 10; i++) {
			handler.setStackInSlot(i,ItemStack.EMPTY);
		}
	}

	public void scan() {
		selectedRecipe = null;
		index = 0;
		if (handler.getStackInSlot(0).isEmpty()) {
			conflicts.clear();
			return;
		}
		conflicts = UncraftingManager.getMatches(handler.getStackInSlot(INPUT));
		if (!conflicts.isEmpty()) {
			selectedRecipe = conflicts.get(0);
		}
	}

	public void cycleRecipe(boolean right) {
		if (!conflicts.isEmpty()) {
			if (right) {
				index++;
			} else {
				index--;
			}
			if (index >= conflicts.size()) {
				index = 0;
			} else if (index < 0) {
				index = conflicts.size() - 1;
			}
		}
		selectedRecipe = conflicts.get(index);
		fillOutput();
	}

	public void lock() {
		world.setBlockState(pos,getBlockState().with(UncraftingTableBlock.LOCKED,!getBlockState().get(UncraftingTableBlock.LOCKED)));
	}

	public static Map<Item,Integer> salt = new HashMap<>();

	public void fillOutput() {
		clear();
		ItemStack input = handler.getStackInSlot(INPUT);
		double damageRatio = input.isDamageable() ? (double)input.getDamage() / input.getMaxDamage() : 0;
		List<ItemStack> stacks = UncraftingManager.getOutputs(getHash(input),selectedRecipe,damageRatio);
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack stack = stacks.get(i);
			handler.setStackInSlot(i+1,stack.copy());
		}
	}

	public int getHash(ItemStack stack) {
		int hash = Objects.hash(stack.getItem(),stack.getTag());
		hash += getOrCreateSalt(stack.getItem());
		return hash;
	}

	public static int getOrCreateSalt(Item item) {
		if (!salt.containsKey(item)) {
			salt.put(item,0);
		}
		return salt.get(item);
	}

	public void uncraft() {
		fillOutput();
		lockedInput = true;
		ItemStack input = handler.getStackInSlot(INPUT);
		salt.put(input.getItem(),salt.get(input.getItem()) + 1);
		input.shrink(selectedRecipe.getCraftingResult(null).getCount());
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		handler.deserializeNBT(nbt.getCompound("inv"));
		this.selectedRecipe = UncraftingManager.recipes.get(new ResourceLocation(nbt.getString("recipe")));
		super.read(state, nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("inv",handler.serializeNBT());
		if (selectedRecipe != null) {
			compound.putString("recipe", this.selectedRecipe.getId().toString());
		}
		return super.write(compound);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getBlockState().getBlock().getTranslationKey());
	}

	@Nullable
	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new UncraftingTableMenu(p_createMenu_1_,p_createMenu_2_,handler, recipeCountHolder, IWorldPosCallable.of(world,pos));
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? externalHandler.cast() : super.getCapability(cap, side);
	}
}
