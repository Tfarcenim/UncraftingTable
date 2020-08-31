package tfar.uncraftingtable;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class Tags {

	public static final ITag<Item> blacklisted_inputs = ItemTags.makeWrapperTag(new ResourceLocation(ExampleMod.MODID,"blacklisted_inputs").toString());

}
