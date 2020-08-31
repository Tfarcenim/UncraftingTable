package tfar.uncraftingtable.mixin;

import com.google.gson.JsonElement;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.uncraftingtable.MixinEvents;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Shadow private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes;

	@Inject(at = @At("RETURN"), method = "apply")
	private void init(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo ci) {
		MixinEvents.onRecipeReload(this.recipes);
	}
}
