package tfar.uncraftingtable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;


public class UncraftingManager {

	public static final Random rand = new Random();

	public static Map<ResourceLocation,IRecipe<?>> recipes;

	public static List<IRecipe<?>> getMatches(ItemStack stack) {
		return recipes.values().stream().filter(recipe -> test(stack,recipe)).collect(Collectors.toList());
	}

	public static boolean test(ItemStack stack, IRecipe<?> recipe) {
		ItemStack result = recipe.getCraftingResult(null);
		return ItemStack.areItemsEqual(result,stack) && result.getCount() <= stack.getCount();
	}

	public static boolean testDamaged(ItemStack stack, IRecipe<?> recipe) {
		ItemStack result = recipe.getCraftingResult(null);
		return ItemStack.areItemsEqual(result,stack) && result.getCount() <= stack.getCount();
	}


	public static List<ItemStack> getOutputs(int hash, @Nullable IRecipe<?> recipe, double scaledDamage) {

		if (recipe == null) return new ArrayList<>();
		List<Ingredient> ingredients = recipe.getIngredients();
		List<ItemStack> stacks = new ArrayList<>();
		rand.setSeed(hash);
		for (Ingredient ingredient : ingredients) {
			ItemStack[] stacks1 = ingredient.getMatchingStacks();
			if (stacks1.length != 0) {
				if (rand.nextDouble() > scaledDamage) {
					stacks.add(stacks1[0]);
				}
			}
		}
		return stacks;
	}

	public static boolean locked;

}
