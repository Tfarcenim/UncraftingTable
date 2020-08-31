package tfar.uncraftingtable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class UncraftingTableBlock extends Block {

	public static final BooleanProperty LOCKED = BooleanProperty.create("locked");

	public UncraftingTableBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(LOCKED,false));
	}

	private static final ITextComponent containerName = new TranslationTextComponent("container.uncrafting_table");

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			UncraftingManager.locked = state.get(LOCKED);
			return ActionResultType.SUCCESS;
		} else {
			player.openContainer(state.getContainer(worldIn, pos));
			player.addStat(Stats.INTERACT_WITH_ANVIL);
			return ActionResultType.CONSUME;
		}
	}

	@Nullable
	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
		return (UncraftingTableBlockEntity)worldIn.getTileEntity(pos);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(LOCKED);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new UncraftingTableBlockEntity();
	}
}
