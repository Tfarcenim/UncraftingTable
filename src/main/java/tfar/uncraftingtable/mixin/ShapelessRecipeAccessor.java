package tfar.uncraftingtable.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapelessRecipe.class)
public interface ShapelessRecipeAccessor {

	@Accessor
	NonNullList<ItemStack> getRecipeItems();

}
