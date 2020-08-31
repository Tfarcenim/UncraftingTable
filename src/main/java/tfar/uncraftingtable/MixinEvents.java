package tfar.uncraftingtable;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.stream.Collectors;

public class MixinEvents {

	public static void onRecipeReload(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> allRecipes) {
		Map<ResourceLocation,IRecipe<?>> simpleCraftingRecipes = allRecipes.get(IRecipeType.CRAFTING).entrySet().stream()
						.filter(resourceLocationIRecipeEntry -> {
							Class<?> clazz = resourceLocationIRecipeEntry.getValue().getClass();
							return clazz == ShapedRecipe.class || clazz == ShapelessRecipe.class;
						}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		UncraftingManager.recipes = simpleCraftingRecipes;
	}
}
