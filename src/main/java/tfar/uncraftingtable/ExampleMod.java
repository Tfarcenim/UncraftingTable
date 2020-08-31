package tfar.uncraftingtable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.uncraftingtable.network.PacketHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // Directly reference a log4j logger.

    public static final String MODID = "uncraftingtable";

    private static final Logger LOGGER = LogManager.getLogger();

    public static UncraftingTableBlock block;
    public static TileEntityType<UncraftingTableBlockEntity> blockEntity;
    public static ContainerType<UncraftingTableMenu> menu;

    public ExampleMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class,this::blocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class,this::items);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class,this::blockEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class,this::menus);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::common);
    }

    private void common(FMLCommonSetupEvent e) {
        PacketHandler.registerMessages(MODID);
    }

    private void blockEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        blockEntity = TileEntityType.Builder.create(UncraftingTableBlockEntity::new,block).build(null);
        blockEntity.setRegistryName("uncrafting_table");
        event.getRegistry().register(blockEntity);
    }

    private void blocks(final RegistryEvent.Register<Block> event) {
        block = new UncraftingTableBlock(AbstractBlock.Properties.from(Blocks.CRAFTING_TABLE));
        block.setRegistryName("uncrafting_table");
        event.getRegistry().register(block);
    }

    private void items(final RegistryEvent.Register<Item> event) {
        Item item = new BlockItem(block,new Item.Properties().group(ItemGroup.DECORATIONS));
        item.setRegistryName("uncrafting_table");
        event.getRegistry().register(item);
    }

    private void menus(final RegistryEvent.Register<ContainerType<?>> event) {
        menu = new ContainerType<>(UncraftingTableMenu::new);
        menu.setRegistryName("uncrafting_table");
        event.getRegistry().register(menu);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(menu,UncraftingTableScreen::new);
    }
}
